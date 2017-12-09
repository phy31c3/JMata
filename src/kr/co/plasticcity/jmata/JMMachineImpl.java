package kr.co.plasticcity.jmata;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class JMMachineImpl implements JMMachine
{
	enum COND
	{
		CREATED, RUNNING, STOPPED, TERMINATED
	}
	
	private final String machineName;
	private final Class startState;
	private final Map<Class, ? extends JMState> stateMap;
	private final Runnable terminateWork;
	
	private volatile ExecutorService machineQue;
	private volatile Class curState;
	private volatile COND cond;
	
	JMMachineImpl(final Object tag, final Class startState, final Map<Class, ? extends JMState> stateMap, final Runnable terminateWork)
	{
		this.machineName = JMLog.getPackagelessName(tag);
		this.startState = startState;
		this.stateMap = stateMap;
		this.terminateWork = terminateWork;
		this.curState = startState;
		this.cond = COND.CREATED;
		
		JMLog.debug(out -> out.print(JMLog.MACHINE_BUILT, machineName));
	}
	
	@Override
	public void run()
	{
		if (ifThisToNext(COND.CREATED, COND.RUNNING))
		{
			machineQue = Executors.newSingleThreadExecutor(r ->
			{
				final Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName(String.format("JMataMachineThread-%s", machineName));
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
		else if (ifThisToNext(COND.STOPPED, COND.RUNNING))
		{
			machineQue = Executors.newSingleThreadExecutor(r ->
			{
				final Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName(String.format("JMataMachineThread-%s", machineName));
				return t;
			});
		}
	}
	
	@Override
	public void stop()
	{
		if (ifThisToNext(COND.RUNNING, COND.STOPPED))
		{
			machineQue.shutdownNow();
		}
	}
	
	@Override
	public void terminate()
	{
		if (ifThisToNext(COND.CREATED, COND.TERMINATED))
		{
			stateMap.get(curState).runExitFunction();
			if (terminateWork != null)
			{
				terminateWork.run();
			}
		}
		else if (ifNotThisToThis(COND.TERMINATED))
		{
			machineQue.shutdownNow();
			try
			{
				if (machineQue.awaitTermination(1, TimeUnit.SECONDS))
				{
					stateMap.get(curState).runExitFunction();
					if (terminateWork != null)
					{
						terminateWork.run();
					}
				}
				else
				{
					JMLog.error(out -> out.print(JMLog.TERMINATION_WORK_FAILED_AS_TIMEOUT, machineName));
				}
			}
			catch (InterruptedException e)
			{
				JMLog.error(out -> out.print(JMLog.TERMINATION_WORK_FAILED_AS_INTERRUPT, machineName));
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Override
	public <S> void input(final S signal)
	{
		if (is(COND.RUNNING))
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
	
	private <S> Object doInput(final S signal)
	{
		if (is(COND.RUNNING) && !Thread.interrupted())
		{
			if (signal instanceof String)
			{
				return stateMap.get(curState).runExitFunction((String)signal, stateMap::containsKey, nextState ->
				{
					if (is(COND.RUNNING) && !Thread.interrupted())
					{
						JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_STRING, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal));
						curState = nextState;
						return stateMap.get(nextState).runEnterFunction((String)signal);
					}
					else
					{
						return null;
					}
				});
			}
			else if (signal instanceof Enum)
			{
				return stateMap.get(curState).runExitFunction((Enum)signal, stateMap::containsKey, nextState ->
				{
					if (is(COND.RUNNING) && !Thread.interrupted())
					{
						JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal)));
						curState = nextState;
						return stateMap.get(nextState).runEnterFunction((Enum)signal);
					}
					else
					{
						return null;
					}
				});
			}
			else
			{
				return stateMap.get(curState).runExitFunctionC(signal, stateMap::containsKey, nextState ->
				{
					if (is(COND.RUNNING) && !Thread.interrupted())
					{
						JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), JMLog.getPackagelessName(signal)));
						curState = nextState;
						return stateMap.get(nextState).runEnterFunctionC(signal);
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
	
	private synchronized boolean is(final COND cond)
	{
		return this.cond == cond;
	}
	
	private synchronized boolean is(final COND... conds)
	{
		for (COND cond : conds)
		{
			if (this.cond == cond)
			{
				return true;
			}
		}
		return false;
	}
	
	private synchronized boolean ifThisToNext(final COND thiz, final COND next)
	{
		if (this.cond == thiz)
		{
			switchCond(next);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private synchronized boolean ifNotThisToThis(final COND thiz)
	{
		if (this.cond != thiz)
		{
			switchCond(thiz);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private synchronized void ifThisDoWork(final COND thiz, final Runnable work)
	{
		if (this.cond == thiz)
		{
			work.run();
		}
	}
	
	private void switchCond(final COND next)
	{
		final COND prev = cond;
		this.cond = next;
		JMLog.debug(out -> out.print(JMLog.MACHINE_STATE_CHANGED, machineName, prev.name(), next.name()));
	}
}