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
	
	private final Object tag;
	private final Class<?> startState;
	private final Map<Class<?>, ? extends JMState> stateMap;
	private final JMVoidConsumer terminateWork;
	
	private volatile ExecutorService machineQue;
	private volatile Class<?> curState;
	private volatile COND cond;
	
	JMMachineImpl(Object tag, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap, JMVoidConsumer terminateWork)
	{
		this.tag = tag;
		this.startState = startState;
		this.stateMap = stateMap;
		this.terminateWork = terminateWork;
		this.curState = startState;
		this.cond = COND.CREATED;
	}
	
	@Override
	public synchronized void run()
	{
		if (cond == COND.CREATED)
		{
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor();
			machineQue.execute(() -> Thread.currentThread().setName(String.format("JMataMachineThread-%s", tag)));
			machineQue.execute(() -> stateMap.get(startState).runEnterFunction());
		}
		else if (cond == COND.STOPPED)
		{
			cond = COND.RUNNING;
			machineQue = Executors.newSingleThreadExecutor();
			machineQue.execute(() -> Thread.currentThread().setName(String.format("JMataMachineThread-%s", tag)));
		}
	}
	
	@Override
	public synchronized void stop()
	{
		if (cond == COND.RUNNING)
		{
			cond = COND.STOPPED;
			machineQue.shutdownNow();
		}
	}
	
	@Override
	public synchronized void terminate()
	{
		if (cond == COND.CREATED)
		{
			cond = COND.TERMINATED;
			if (terminateWork != null)
			{
				terminateWork.accept();
			}
		}
		else if (cond != COND.TERMINATED)
		{
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
						JMLog.error("%s : 머신 종료에 너무 긴 시간(1초 이상)이 소요되어 종료 동작을 수행하지 못했습니다.", tag);
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
				if (cond == COND.RUNNING && !Thread.interrupted())
				{
					if (signal instanceof String)
					{
						stateMap.get(curState).runExitFunction((String)signal, nextState ->
						{
							if (cond == COND.RUNNING && !Thread.interrupted())
							{
								JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curState.getSimpleName(), nextState.getSimpleName(), signal);
								curState = nextState;
								stateMap.get(curState).runEnterFunction((String)signal);
							}
						});
					}
					else if (signal instanceof Enum)
					{
						stateMap.get(curState).runExitFunction((Enum<?>)signal, nextState ->
						{
							if (cond == COND.RUNNING && !Thread.interrupted())
							{
								JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curState.getSimpleName(), nextState.getSimpleName(), signal);
								curState = nextState;
								stateMap.get(curState).runEnterFunction((Enum<?>)signal);
							}
						});
					}
					else
					{
						stateMap.get(curState).runExitFunctionC(signal, nextState ->
						{
							if (cond == COND.RUNNING && !Thread.interrupted())
							{
								JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curState.getSimpleName(), nextState.getSimpleName(), signal);
								curState = nextState;
								stateMap.get(curState).runEnterFunctionC(signal);
							}
						});
					}
				}
			});
		}
	}
}