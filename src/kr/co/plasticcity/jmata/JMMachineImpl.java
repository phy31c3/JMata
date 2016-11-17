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
	
	private volatile ExecutorService[] machineQue;
	private volatile Class<?>[] curStates;
	private COND[] conds;
	private int activeInstances;
	
	JMMachineImpl(Object tag, int numInstances, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap)
	{
		this.tag = tag;
		this.startState = startState;
		this.stateMap = stateMap;
		this.machineQue = new ExecutorService[numInstances];
		this.curStates = new Class<?>[numInstances];
		this.conds = new COND[numInstances];
		this.activeInstances = numInstances;
		
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
			machineQue[idx].execute(() -> stateMap.get(startState).runEnterFunction(idx));
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
	public boolean terminate(int idx) throws JMException
	{
		idxTest(idx);
		if (notCondOf(idx, COND.TERMINATED))
		{
			setCondOf(idx, COND.TERMINATED);
			machineQue[idx].shutdownNow();
			return --activeInstances == 0 ? true : false;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public <S> void inputToAll(S signal) throws JMException
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
	public <S extends Enum<S>> void inputToAll(Enum<S> signal) throws JMException
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
	public void inputToAll(String signal) throws JMException
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
			machineQue[idx].execute(() ->
			{
				if (isCondOf(idx, COND.RUNNING) && !Thread.interrupted())
				{
					stateMap.get(curStates[idx]).runExitFunctionC(idx, signal, nextState ->
					{
						JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curStates[idx].getSimpleName(), nextState.getSimpleName(), signal);
						curStates[idx] = nextState;
						stateMap.get(curStates[idx]).runEnterFunctionC(idx, signal);
					});
				}
			});
		}
	}
	
	@Override
	public <S extends Enum<S>> void input(final int idx, final Enum<S> signal) throws JMException
	{
		idxTest(idx);
		if (isCondOf(idx, COND.RUNNING))
		{
			machineQue[idx].execute(() ->
			{
				if (isCondOf(idx, COND.RUNNING) && !Thread.interrupted())
				{
					JMConsumer<Class<?>> switchToNext = nextState ->
					{
						JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curStates[idx].getSimpleName(), nextState.getSimpleName(), signal);
						curStates[idx] = nextState;
						if (!stateMap.get(curStates[idx]).runEnterFunction(idx, signal))
						{
							stateMap.get(curStates[idx]).runEnterFunctionC(idx, signal);
						}
					};
					
					if (!stateMap.get(curStates[idx]).runExitFunction(idx, signal, switchToNext))
					{
						stateMap.get(curStates[idx]).runExitFunctionC(idx, signal, switchToNext);
					}
				}
			});
		}
	}
	
	@Override
	public void input(final int idx, final String signal) throws JMException
	{
		idxTest(idx);
		if (isCondOf(idx, COND.RUNNING))
		{
			machineQue[idx].execute(() ->
			{
				if (isCondOf(idx, COND.RUNNING) && !Thread.interrupted())
				{
					JMConsumer<Class<?>> switchToNext = nextState ->
					{
						JMLog.debug("%s : switch from [%s] to [%s] due to [%s]", tag, curStates[idx].getSimpleName(), nextState.getSimpleName(), signal);
						curStates[idx] = nextState;
						if (!stateMap.get(curStates[idx]).runEnterFunction(idx, signal))
						{
							stateMap.get(curStates[idx]).runEnterFunctionC(idx, signal);
						}
					};
					
					if (!stateMap.get(curStates[idx]).runExitFunction(idx, signal, switchToNext))
					{
						stateMap.get(curStates[idx]).runExitFunctionC(idx, signal, switchToNext);
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
			throw new JMException("'%s' 머신 인덱스 테스트 에러 : 인스턴스 갯수 = %d, 요청 idx = %d", tag, machineQue.length, idx);
		}
	}
}