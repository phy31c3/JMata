package kr.co.plasticcity.jmata;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class JMMachineImpl implements JMMachine
{
	enum COND
	{
		CREATED, RUNNING, PAUSED, STOPPED, TERMINATED
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
			machineQue = newMachineQue();
			startUp();
		}
		else if (ifThisToNext(COND.STOPPED, COND.RUNNING))
		{
			machineQue = newMachineQue();
		}
		else
		{
			ifPauseThenResume();
		}
	}
	
	@Override
	public void pause()
	{
		if (ifThisToNext(COND.CREATED, COND.PAUSED))
		{
			machineQue = newMachineQue();
			startUp();
		}
		else if (ifThisToNext(COND.RUNNING, COND.PAUSED))
		{
			/* do nothing */
		}
		else if (ifThisToNext(COND.STOPPED, COND.PAUSED))
		{
			machineQue = newMachineQue();
		}
	}
	
	@Override
	public void stop()
	{
		if (ifThisToNext(COND.RUNNING, COND.STOPPED))
		{
			machineQue.shutdownNow();
		}
		else if (ifThisToNext(COND.PAUSED, COND.STOPPED))
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
		if (is(COND.RUNNING, COND.PAUSED))
		{
			machineQue.execute(() ->
			{
				ifPauseThenAwait();
				Object nextSignal = signal;
				while (nextSignal != null && is(COND.RUNNING, COND.PAUSED))
				{
					nextSignal = doInput(nextSignal);
					ifPauseThenAwait();
				}
			});
		}
	}
	
	private <S> Object doInput(final S signal)
	{
		if (signal instanceof String)
		{
			return stateMap.get(curState).runExitFunction((String)signal, stateMap::containsKey, nextState ->
			{
				JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_STRING, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal));
				curState = nextState;
				return stateMap.get(nextState).runEnterFunction((String)signal);
			});
		}
		else if (signal instanceof Enum)
		{
			return stateMap.get(curState).runExitFunction((Enum)signal, stateMap::containsKey, nextState ->
			{
				JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal)));
				curState = nextState;
				return stateMap.get(nextState).runEnterFunction((Enum)signal);
			});
		}
		else
		{
			return stateMap.get(curState).runExitFunctionC(signal, stateMap::containsKey, nextState ->
			{
				JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), JMLog.getPackagelessName(signal)));
				curState = nextState;
				return stateMap.get(nextState).runEnterFunctionC(signal);
			});
		}
	}
	
	private ExecutorService newMachineQue()
	{
		return Executors.newSingleThreadExecutor(r ->
		{
			final Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName(String.format("JMataMachineThread-%s", machineName));
			return t;
		});
	}
	
	private void startUp()
	{
		machineQue.execute(() ->
		{
			ifPauseThenAwait();
			Object nextSignal = stateMap.get(startState).runEnterFunction();
			while (nextSignal != null && is(COND.RUNNING, COND.PAUSED))
			{
				nextSignal = doInput(nextSignal);
				ifPauseThenAwait();
			}
		});
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
	
	private synchronized void ifPauseThenAwait()
	{
		if (this.cond == COND.PAUSED)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				/* do nothing */
			}
		}
	}
	
	private synchronized void ifPauseThenResume()
	{
		if (this.cond == COND.PAUSED)
		{
			switchCond(COND.RUNNING);
			notify();
		}
	}
	
	private void switchCond(final COND next)
	{
		final COND prev = cond;
		this.cond = next;
		JMLog.debug(out -> out.print(JMLog.MACHINE_STATE_CHANGED, machineName, prev.name(), next.name()));
	}
}