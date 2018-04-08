package kr.co.plasticcity.jmata;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class JMMachineImpl implements JMMachine
{
	private enum COND
	{
		CREATED, RUNNING, PAUSED, STOPPED, TERMINATED
	}
	
	private final String machineName;
	private final Class startState;
	private final Map<Class, ? extends JMState> stateMap;
	private final Runnable onPause;
	private final Runnable onResume;
	private final Runnable onStop;
	private final Runnable onRestart;
	private final Runnable onTerminate;
	
	private volatile ExecutorService machineQue;
	private volatile Class curState;
	private volatile Object savedSignal;
	private volatile COND cond;
	private volatile boolean isLogEnabled;
	
	JMMachineImpl(final String name, final Class startState, final Map<Class, ? extends JMState> stateMap,
	              final Runnable onPause, final Runnable onResume, final Runnable onStop, final Runnable onRestart, final Runnable onTerminate)
	{
		this.machineName = name;
		this.startState = startState;
		this.stateMap = stateMap;
		this.onPause = onPause;
		this.onResume = onResume;
		this.onStop = onStop;
		this.onRestart = onRestart;
		this.onTerminate = onTerminate;
		this.curState = startState;
		this.savedSignal = COND.CREATED;
		this.cond = COND.CREATED;
	}
	
	@Override
	public void setLogEnabled(final boolean enabled)
	{
		isLogEnabled = enabled;
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
			if (onRestart != null)
			{
				onRestart.run();
			}
			machineQue = newMachineQue();
			if (!isStarted())
			{
				startUp();
			}
			else
			{
				input(savedSignal);
			}
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
		else if (ifThisToNext(COND.STOPPED, COND.PAUSED))
		{
			if (onRestart != null)
			{
				onRestart.run();
			}
			machineQue = newMachineQue();
			if (!isStarted())
			{
				startUp();
			}
			else
			{
				input(savedSignal);
			}
		}
		else
		{
			ifThisToNext(COND.RUNNING, COND.PAUSED);
		}
	}
	
	@Override
	public void stop()
	{
		if (ifThisToNext(COND.CREATED, COND.STOPPED))
		{
			if (onStop != null)
			{
				onStop.run();
			}
		}
		else if (ifNextsToThis(COND.STOPPED, COND.RUNNING, COND.PAUSED))
		{
			try
			{
				machineQue.shutdownNow();
				if (!machineQue.awaitTermination(5, TimeUnit.SECONDS))
				{
					JMLog.error(out -> out.print(JMLog.MACHINE_SHUTDOWN_FAILED_AS_TIMEOUT, machineName));
				}
				if (onStop != null)
				{
					onStop.run();
				}
			}
			catch (InterruptedException e) // Unknown os level interrupt
			{
				JMLog.error(out -> out.print(JMLog.MACHINE_SHUTDOWN_FAILED_AS_INTERRUPT, machineName));
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Override
	public void terminate()
	{
		if (ifNextsToThis(COND.TERMINATED, COND.CREATED, COND.STOPPED))
		{
			if (isStarted())
			{
				stateMap.get(curState).runExitFunction();
			}
			if (onTerminate != null)
			{
				onTerminate.run();
			}
		}
		else if (ifNotThisToThis(COND.TERMINATED))
		{
			try
			{
				machineQue.shutdownNow();
				if (!machineQue.awaitTermination(5, TimeUnit.SECONDS))
				{
					JMLog.error(out -> out.print(JMLog.MACHINE_SHUTDOWN_FAILED_AS_TIMEOUT, machineName));
				}
				if (isStarted())
				{
					stateMap.get(curState).runExitFunction();
				}
				if (onTerminate != null)
				{
					onTerminate.run();
				}
			}
			catch (InterruptedException e) // Unknown os level interrupt
			{
				JMLog.error(out -> out.print(JMLog.MACHINE_SHUTDOWN_FAILED_AS_INTERRUPT, machineName));
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Override
	public <S> void input(final S signal)
	{
		if (signal != null && is(COND.RUNNING, COND.PAUSED))
		{
			machineQue.execute(() ->
			{
				Object nextSignal = signal;
				while (nextSignal != null && !Thread.interrupted())
				{
					if (ifPauseThenAwait())
					{
						return;
					}
					else
					{
						nextSignal = doInput(nextSignal);
						savedSignal = nextSignal;
					}
				}
			});
		}
	}
	
	private void startUp()
	{
		machineQue.execute(() ->
		{
			if (!ifPauseThenAwait())
			{
				Object nextSignal = stateMap.get(startState).runEnterFunction();
				savedSignal = nextSignal;
				while (nextSignal != null && !Thread.interrupted())
				{
					if (ifPauseThenAwait())
					{
						return;
					}
					else
					{
						nextSignal = doInput(nextSignal);
						savedSignal = nextSignal;
					}
				}
			}
		});
	}
	
	private <S> Object doInput(final S signal)
	{
		if (signal instanceof String)
		{
			return stateMap.get(curState).runExitFunction((String)signal, stateMap::containsKey, nextState ->
			{
				if (nextState != null)
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_STRING, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal)); }
					curState = nextState;
					return stateMap.get(nextState).runEnterFunction((String)signal);
				}
				else
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_STRING, machineName, curState.getSimpleName(), "null", signal)); }
					return null;
				}
			});
		}
		else if (signal instanceof Enum)
		{
			return stateMap.get(curState).runExitFunction((Enum)signal, stateMap::containsKey, nextState ->
			{
				if (nextState != null)
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal))); }
					curState = nextState;
					return stateMap.get(nextState).runEnterFunction((Enum)signal);
				}
				else
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), "null", signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal))); }
					return null;
				}
			});
		}
		else
		{
			return stateMap.get(curState).runExitFunctionC(signal, stateMap::containsKey, nextState ->
			{
				if (nextState != null)
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), nextState.getSimpleName(), JMLog.getPackagelessName(signal))); }
					curState = nextState;
					return stateMap.get(nextState).runEnterFunctionC(signal);
				}
				else
				{
					if (isLogEnabled)
					{ JMLog.debug(out -> out.print(JMLog.STATE_SWITCHED_BY_CLASS, machineName, curState.getSimpleName(), "null", JMLog.getPackagelessName(signal))); }
					return null;
				}
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
	
	private boolean isStarted()
	{
		return !COND.CREATED.equals(savedSignal);
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
	
	private synchronized boolean ifNextsToThis(final COND thiz, final COND... nexts)
	{
		for (COND cond : nexts)
		{
			if (this.cond == cond)
			{
				switchCond(thiz);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true == interrupted. It means machine was STOPPED or TERMINATED.
	 */
	private synchronized boolean ifPauseThenAwait()
	{
		if (this.cond == COND.PAUSED)
		{
			try
			{
				if (onPause != null)
				{
					onPause.run();
				}
				wait();
				if (onResume != null)
				{
					onResume.run();
				}
				return false;
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return true;
			}
		}
		else
		{
			return false;
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