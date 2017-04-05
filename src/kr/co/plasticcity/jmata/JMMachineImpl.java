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
		
		JMLog.debug(JMLog.MACHINE_BUILT, tag);
	}
	
	@Override
	public synchronized void run()
	{
		if (cond == COND.CREATED)
		{
			switchCond(COND.RUNNING);
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
			switchCond(COND.RUNNING);
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
			switchCond(COND.STOPPED);
			machineQue.shutdownNow();
		}
	}
	
	@Override
	public synchronized void terminate()
	{
		if (cond == COND.CREATED)
		{
			switchCond(COND.TERMINATED);
			stateMap.get(curState).runExitFunction();
			if (terminateWork != null)
			{
				terminateWork.accept();
			}
		}
		else if (cond != COND.TERMINATED)
		{
			switchCond(COND.TERMINATED);
			machineQue.shutdownNow();
			try
			{
				if (machineQue.awaitTermination(1, TimeUnit.SECONDS))
				{
					stateMap.get(curState).runExitFunction();
					if (terminateWork != null)
					{
						terminateWork.accept();
					}
				}
				else
				{
					JMLog.error(JMLog.TERMINATION_WORK_FAILED_AS_TIMEOUT, machineTag);
				}
			}
			catch (InterruptedException e)
			{
				JMLog.error(JMLog.TERMINATION_WORK_FAILED_AS_INTERRUPT, machineTag);
				Thread.currentThread().interrupt();
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
						curState = nextState;
						JMLog.debug(JMLog.STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
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
						curState = nextState;
						JMLog.debug(JMLog.STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
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
						curState = nextState;
						JMLog.debug(JMLog.STATE_SWITCHED, machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
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
	
	private void switchCond(COND next)
	{
		final COND prev = cond;
		this.cond = next;
		JMLog.debug(JMLog.MACHINE_STATE_CHANGED, machineTag, prev.name(), next.name());
	}
}