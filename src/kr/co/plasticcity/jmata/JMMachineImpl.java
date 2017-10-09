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
		this.machineName = tag.toString().substring(tag.toString().lastIndexOf(".") + 1);
		this.startState = startState;
		this.stateMap = stateMap;
		this.terminateWork = terminateWork;
		this.curState = startState;
		this.cond = COND.CREATED;
		
		JMLog.debug(out -> out.print(JMLog.MACHINE_BUILT, machineName));
	}
	
	@Override
	public synchronized void run()
	{
		if (cond == COND.CREATED)
		{
			switchCond(COND.RUNNING);
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
		else if (cond == COND.STOPPED)
		{
			switchCond(COND.RUNNING);
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
				terminateWork.run();
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
	
	private <S> Object doInput(final S signal)
	{
		if (cond == COND.RUNNING && !Thread.interrupted())
		{
			if (signal instanceof String)
			{
				return stateMap.get(curState).runExitFunction((String)signal, stateMap::containsKey, nextState ->
				{
					if (cond == COND.RUNNING && !Thread.interrupted())
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
					if (cond == COND.RUNNING && !Thread.interrupted())
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
					if (cond == COND.RUNNING && !Thread.interrupted())
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
	
	private void switchCond(final COND next)
	{
		final COND prev = cond;
		this.cond = next;
		JMLog.debug(out -> out.print(JMLog.MACHINE_STATE_CHANGED, machineName, prev.name(), next.name()));
	}
}