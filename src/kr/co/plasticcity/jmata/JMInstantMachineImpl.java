package kr.co.plasticcity.jmata;

import java.util.Map;

class JMInstantMachineImpl implements JMMachine
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
	
	private volatile Class curState;
	private volatile Object savedSignal;
	private volatile COND cond;
	private volatile boolean isLogEnabled;
	
	JMInstantMachineImpl(final String name, final Class startState, final Map<Class, ? extends JMState> stateMap,
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
			startUp();
		}
		else if (ifThisToNext(COND.STOPPED, COND.RUNNING))
		{
			if (onRestart != null)
			{
				onRestart.run();
			}
			if (!isStarted())
			{
				startUp();
			}
		}
		else if (ifThisToNext(COND.PAUSED, COND.RUNNING))
		{
			if (onResume != null)
			{
				onResume.run();
			}
			if (!isStarted())
			{
				startUp();
			}
		}
	}
	
	@Override
	public void pause()
	{
		if (ifThisToNext(COND.CREATED, COND.PAUSED))
		{
			if (onPause != null)
			{
				onPause.run();
			}
		}
		else if (ifThisToNext(COND.STOPPED, COND.PAUSED))
		{
			if (onRestart != null)
			{
				onRestart.run();
			}
			if (onPause != null)
			{
				onPause.run();
			}
		}
		else if (ifThisToNext(COND.RUNNING, COND.PAUSED))
		{
			if (onPause != null)
			{
				onPause.run();
			}
		}
	}
	
	@Override
	public void stop()
	{
		if (ifNextsToThis(COND.STOPPED, COND.CREATED, COND.RUNNING, COND.PAUSED))
		{
			if (onStop != null)
			{
				onStop.run();
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
			if (isStarted())
			{
				stateMap.get(curState).runExitFunction();
			}
			if (onTerminate != null)
			{
				onTerminate.run();
			}
		}
	}
	
	@Override
	public <S> void input(final S signal)
	{
		if (signal != null && is(COND.RUNNING))
		{
			Object nextSignal = signal;
			while (nextSignal != null)
			{
				nextSignal = doInput(nextSignal);
				savedSignal = nextSignal;
			}
		}
	}
	
	private void startUp()
	{
		Object nextSignal = stateMap.get(startState).runEnterFunction();
		savedSignal = nextSignal;
		while (nextSignal != null)
		{
			nextSignal = doInput(nextSignal);
			savedSignal = nextSignal;
		}
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
	
	private void switchCond(final COND next)
	{
		final COND prev = cond;
		this.cond = next;
		JMLog.debug(out -> out.print(JMLog.MACHINE_STATE_CHANGED, machineName, prev.name(), next.name()));
	}
}