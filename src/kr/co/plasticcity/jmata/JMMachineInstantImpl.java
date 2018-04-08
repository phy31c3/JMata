package kr.co.plasticcity.jmata;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

class JMMachineInstantImpl implements JMMachine
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
	
	private volatile Deque<Object> signalQue;
	private volatile Class curState;
	private volatile Object savedSignal;
	private volatile COND cond;
	private volatile boolean isLogEnabled;
	
	JMMachineInstantImpl(final String name, final Class startState, final Map<Class, ? extends JMState> stateMap,
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
			signalQue = newSignalQue();
			startUp();
		}
		else if (ifThisToNext(COND.STOPPED, COND.RUNNING))
		{
			signalQue = newSignalQue();
			if (!isStarted())
			{
				startUp();
			}
			else
			{
				input(savedSignal);
			}
			if (onRestart != null)
			{
				onRestart.run();
			}
		}
		else if (is(COND.PAUSED))
		{
			if (onResume != null)
			{
				onResume.run();
			}
			ifThisToNext(COND.PAUSED, COND.RUNNING);
			if (!isStarted())
			{
				startUp();
			}
			else
			{
				doLoop();
			}
		}
	}
	
	@Override
	public void pause()
	{
		if (ifThisToNext(COND.CREATED, COND.PAUSED))
		{
			signalQue = newSignalQue();
			if (onPause != null)
			{
				onPause.run();
			}
		}
		else if (ifThisToNext(COND.STOPPED, COND.PAUSED))
		{
			signalQue = newSignalQue();
			if (isStarted())
			{
				input(savedSignal);
			}
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
		if (ifThisToNext(COND.CREATED, COND.STOPPED))
		{
			if (onStop != null)
			{
				onStop.run();
			}
		}
		else if (ifNextsToThis(COND.STOPPED, COND.RUNNING, COND.PAUSED))
		{
			signalQue.clear();
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
			signalQue.clear();
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
		if (signal != null && is(COND.RUNNING, COND.PAUSED))
		{
			signalQue.addLast(signal);
			if (signalQue.size() == 1)
			{
				doLoop();
			}
		}
	}
	
	private void startUp()
	{
		signalQue.addFirst(savedSignal);
		savedSignal = stateMap.get(startState).runEnterFunction();
		if (is(COND.RUNNING, COND.PAUSED))
		{
			signalQue.pollFirst();
			if (savedSignal != null)
			{
				signalQue.addFirst(savedSignal);
			}
			doLoop();
		}
	}
	
	private void doLoop()
	{
		while (!signalQue.isEmpty() && is(COND.RUNNING))
		{
			savedSignal = doInput(signalQue.getFirst());
			if (is(COND.RUNNING, COND.PAUSED))
			{
				signalQue.pollFirst();
				if (savedSignal != null)
				{
					signalQue.addFirst(savedSignal);
				}
			}
			else
			{
				return;
			}
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
	
	private Deque<Object> newSignalQue()
	{
		return new LinkedList<>();
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