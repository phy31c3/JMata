package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;

import kr.co.plasticcity.jmata.function.*;

class JMMachineImpl implements JMMachine
{
	enum COND
	{
		CREATED, RUNNING, STOPPED, TERMINATED
	}
	
	private static final String LOG_MACHINE_STATE_CHANGED = "[%s] machine state changed : [%s] -> [%s]";
	private static final String LOG_STATE_SWITCHED = "[%s] machine : switch from [%s] to [%s] due to [%s]";
	
	private final Object machineTag;
	private final Class<?> startState;
	private final Map<Class<?>, ? extends JMState> stateMap;
	private final JMVoidConsumer terminateWork;
	
	private volatile ExecutorService machineQue;
	private volatile Class<?> curState;
	private volatile COND cond;
	
	JMMachineImpl(Object tag, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap, JMVoidConsumer terminateWork)
	{
		this.machineTag = tag;
		this.startState = startState;
		this.stateMap = stateMap;
		this.terminateWork = terminateWork;
		this.curState = startState;
		this.cond = COND.CREATED;
		
		JMLog.debug("[%s] machine has been built", tag);
	}
	
	@Override
	public synchronized void run()
	{
		if (cond == COND.CREATED)
		{
			JMLog.debug(LOG_MACHINE_STATE_CHANGED, machineTag, cond.name(), COND.RUNNING.name());
			
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor(r ->
			{
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName(String.format("JMataMachineThread-%s", machineTag));
				return t;
			});
			machineQue.execute(() ->
			{
				Object nextSignal = stateMap.get(startState).runEnterFunction();
				while (nextSignal != null)
				{
					nextSignal = doInput(nextSignal);
				}
			});
		}
		else if (cond == COND.STOPPED)
		{
			JMLog.debug(LOG_MACHINE_STATE_CHANGED, machineTag, cond.name(), COND.RUNNING.name());
			
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor(r ->
			{
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName(String.format("JMataMachineThread-%s", machineTag));
				return t;
			});
		}
	}
	
	@Override
	public synchronized void stop()
	{
		if (cond == COND.RUNNING)
		{
			JMLog.debug(LOG_MACHINE_STATE_CHANGED, machineTag, cond.name(), COND.STOPPED.name());
			
			cond = COND.STOPPED;
			machineQue.shutdownNow();
		}
	}
	
	@Override
	public synchronized void terminate()
	{
		if (cond == COND.CREATED)
		{
			JMLog.debug(LOG_MACHINE_STATE_CHANGED, machineTag, cond.name(), COND.TERMINATED.name());
			
			cond = COND.TERMINATED;
			if (terminateWork != null)
			{
				terminateWork.accept();
			}
		}
		else if (cond != COND.TERMINATED)
		{
			JMLog.debug(LOG_MACHINE_STATE_CHANGED, machineTag, cond.name(), COND.TERMINATED.name());
			
			cond = COND.TERMINATED;
			machineQue.shutdownNow();
			if (terminateWork != null)
			{
				try
				{
					if (machineQue.awaitTermination(1, TimeUnit.SECONDS))
					{
						terminateWork.accept();
					}
					else
					{
						JMLog.error("[%s] machine : The shutdown operation failed because the machine shutdown took too long (over 1 second)", machineTag);
					}
				}
				catch (InterruptedException e)
				{
					JMLog.error("[%s] machine : The shutdown operation failed because this thread interrupted before ", machineTag);
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	@Override
	public synchronized <S> void input(final S signal)
	{
		if (cond == COND.RUNNING)
		{
			machineQue.execute(() ->
			{
				Object nextSignal = signal;
				while (nextSignal != null)
				{
					nextSignal = doInput(nextSignal);
				}
			});
		}
	}
	
	private <S> Object doInput(S signal)
	{
		if (cond == COND.RUNNING && !Thread.interrupted())
		{
			if (signal instanceof String)
			{
				return stateMap.get(curState).runExitFunction((String)signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug(LOG_STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						return stateMap.get(curState).runEnterFunction((String)signal);
					}
					else
					{
						return null;
					}
				});
			}
			else if (signal instanceof Enum)
			{
				return stateMap.get(curState).runExitFunction((Enum<?>)signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug(LOG_STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						return stateMap.get(curState).runEnterFunction((Enum<?>)signal);
					}
					else
					{
						return null;
					}
				});
			}
			else
			{
				return stateMap.get(curState).runExitFunctionC(signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug(LOG_STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						return stateMap.get(curState).runEnterFunctionC(signal);
					}
					else
					{
						return null;
					}
				});
			}
		}
		else
		{
			return null;
		}
	}
}