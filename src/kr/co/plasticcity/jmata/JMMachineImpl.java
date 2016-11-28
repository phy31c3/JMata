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
		
		JMLog.debug("[%s] machine has been built", tag);
	}
	
	@Override
	public synchronized void run()
	{
		if (cond == COND.CREATED)
		{
			JMLog.debug("[%s] machine state changed : [%s] -> [%s]", machineTag, cond.name(), COND.RUNNING.name());
			
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor();
			machineQue.execute(() -> Thread.currentThread().setName(String.format("JMataMachineThread-%s", machineTag)));
			machineQue.execute(() -> stateMap.get(startState).runEnterFunction());
		}
		else if (cond == COND.STOPPED)
		{
			JMLog.debug("[%s] machine state changed : [%s] -> [%s]", machineTag, cond.name(), COND.RUNNING.name());
			
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor();
			machineQue.execute(() -> Thread.currentThread().setName(String.format("JMataMachineThread-%s", machineTag)));
		}
	}
	
	@Override
	public synchronized void stop()
	{
		if (cond == COND.RUNNING)
		{
			JMLog.debug("[%s] machine state changed : [%s] -> [%s]", machineTag, cond.name(), COND.STOPPED.name());
			
			cond = COND.STOPPED;
			machineQue.shutdownNow();
		}
	}
	
	@Override
	public synchronized void terminate()
	{
		if (cond == COND.CREATED)
		{
			JMLog.debug("[%s] machine state changed : [%s] -> [%s]", machineTag, cond.name(), COND.TERMINATED.name());
			
			cond = COND.TERMINATED;
			if (terminateWork != null)
			{
				terminateWork.accept();
			}
		}
		else if (cond != COND.TERMINATED)
		{
			JMLog.debug("[%s] machine state changed : [%s] -> [%s]", machineTag, cond.name(), COND.TERMINATED.name());
			
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
					/* do nothing */
				}
			}
		}
	}
	
	@Override
	public <S> void input(S signal)
	{
		if (cond == COND.RUNNING)
		{
			machineQue.execute(() ->
			{
				doInput(signal);
			});
		}
	}
	
	private <S> void doInput(S signal)
	{
		if (cond == COND.RUNNING && !Thread.interrupted())
		{
			if (signal instanceof String)
			{
				stateMap.get(curState).runExitFunction((String)signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug("[%s] machine : switch from [%s] to [%s] due to [\"%s\"]", machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						Object nextSignal = stateMap.get(curState).runEnterFunction((String)signal);
						if (nextSignal != null)
						{
							doInput(nextSignal);
						}
					}
				});
			}
			else if (signal instanceof Enum)
			{
				stateMap.get(curState).runExitFunction((Enum<?>)signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug("[%s] machine : switch from [%s] to [%s] due to [%s]", machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						Object nextSignal = stateMap.get(curState).runEnterFunction((Enum<?>)signal);
						if (nextSignal != null)
						{
							doInput(nextSignal);
						}
					}
				});
			}
			else
			{
				stateMap.get(curState).runExitFunctionC(signal, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
					{
						JMLog.debug("[%s] machine : switch from [%s] to [%s] due to [%s]", machineTag, curState.getSimpleName(), nextState.getSimpleName(), signal);
						curState = nextState;
						Object nextSignal = stateMap.get(curState).runEnterFunctionC(signal);
						if (nextSignal != null)
						{
							doInput(nextSignal);
						}
					}
				});
			}
		}
	}
}