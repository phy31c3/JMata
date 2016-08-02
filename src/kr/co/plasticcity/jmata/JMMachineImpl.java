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
	
	private final Class<?> machineTag;
	private final Class<?> startState;
	private final Map<Class<?>, ? extends JMState> stateMap;
	
	private volatile ExecutorService[] machineQue;
	private volatile Class<?>[] curStates;
	private COND[] conds;
	
	JMMachineImpl(Class<?> tag, int numInstances, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap)
	{
		this.machineTag = tag;
		this.startState = startState;
		this.stateMap = stateMap;
		this.machineQue = new ExecutorService[numInstances];
		this.curStates = new Class<?>[numInstances];
		this.conds = new COND[numInstances];
		
		for (int idx = 0; idx < numInstances; ++idx)
		{
			machineQue[idx] = Executors.newSingleThreadExecutor();
			curStates[idx] = startState;
			conds[idx] = COND.CREATED;
		}
	}
	
	@Override
	public void runAll()
	{
		for (int idx = 0; idx < machineQue.length; ++idx)
		{
			try
			{
				run(idx);
			}
			catch (JMException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run(final int idx) throws JMException
	{
		idxTest(idx);
		if (isCondOf(idx, COND.CREATED))
		{
			setCondOf(idx, COND.RUNNING);
			machineQue[idx].execute(new Runnable()
			{
				@Override
				public void run()
				{
					stateMap.get(startState).runEnterFunction(idx);
				}
			});
		}
		else if (isCondOf(idx, COND.STOPPED))
		{
			setCondOf(idx, COND.RUNNING);
		}
	}
	
	@Override
	public void stopAll()
	{
		for (int idx = 0; idx < machineQue.length; ++idx)
		{
			try
			{
				stop(idx);
			}
			catch (JMException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void stop(int idx) throws JMException
	{
		idxTest(idx);
		if (isCondOf(idx, COND.RUNNING))
		{
			setCondOf(idx, COND.STOPPED);
		}
	}
	
	@Override
	public void terminateAll()
	{
		for (int idx = 0; idx < machineQue.length; ++idx)
		{
			try
			{
				terminate(idx);
			}
			catch (JMException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void terminate(int idx) throws JMException
	{
		idxTest(idx);
		if (notCondOf(idx, COND.TERMINATED))
		{
			setCondOf(idx, COND.TERMINATED);
			machineQue[idx].shutdownNow();
		}
	}
	
	@Override
	public <S> void inputAll(S signal) throws JMException
	{
		for (int idx = 0; idx < machineQue.length; ++idx)
		{
			try
			{
				input(idx, signal);
			}
			catch (JMException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public <S> void input(final int idx, final S signal) throws JMException
	{
		idxTest(idx);
		if (isCondOf(idx, COND.RUNNING))
		{
			machineQue[idx].execute(new Runnable()
			{
				@Override
				public void run()
				{
					if (isCondOf(idx, COND.RUNNING) && !Thread.interrupted())
					{
						stateMap.get(curStates[idx]).runExitFunction(idx, signal, new JMConsumer<Class<?>>()
						{
							@Override
							public void accept(Class<?> nextState)
							{
								curStates[idx] = nextState;
								stateMap.get(curStates[idx]).runEnterFunction(idx, signal);
							}
						});
					}
				}
			});
		}
	}
	
	private boolean isCondOf(int idx, COND cond)
	{
		synchronized (conds[idx])
		{
			return this.conds[idx] == cond;
		}
	}
	
	private boolean notCondOf(int idx, COND cond)
	{
		synchronized (conds[idx])
		{
			return this.conds[idx] != cond;
		}
	}
	
	private void setCondOf(int idx, COND cond)
	{
		synchronized (conds[idx])
		{
			conds[idx] = cond;
		}
	}
	
	private void idxTest(int idx) throws JMException
	{
		if (idx >= machineQue.length)
		{
			JMLog.out("");
			throw new JMException("'%s' 머신 인덱스 테스트 에러 : 인스턴스 갯수 = %d, 요청 idx = %d", machineTag.getSimpleName(), machineQue.length, idx);
		}
	}
}