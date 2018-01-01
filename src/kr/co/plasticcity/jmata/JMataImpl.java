package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import kr.co.plasticcity.jmata.function.Consumer;

class JMataImpl
{
	/* ================================== ↓ Static Part ================================== */
	
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private static final Lock readLock = lock.readLock();
	private static final Lock writeLock = lock.writeLock();
	private static volatile JMataImpl instance;
	private static volatile STATE state = STATE.NOT_INIT;
	
	private enum STATE
	{
		NOT_INIT, RUNNING, RELEASED
	}
	
	static void initialize(final Consumer<String> debugLogger, final Consumer<String> errorLogger)
	{
		writeLock.lock();
		
		try
		{
			if (state != STATE.RUNNING)
			{
				state = STATE.RUNNING;
				instance = new JMataImpl();
				JMLog.setLogger(debugLogger, errorLogger);
				JMLog.debug(out -> out.print(JMLog.JMATA_INITIALIZED));
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	static void release(final Runnable releaseWork)
	{
		if (state == STATE.RUNNING)
		{
			writeLock.lock();
			
			try
			{
				if (state == STATE.RUNNING && instance != null)
				{
					instance.destroy(releaseWork);
				}
			}
			finally
			{
				writeLock.unlock();
			}
		}
	}
	
	static void post(final Consumer<JMataImpl> func)
	{
		try
		{
			readLock.lock();
			
			try
			{
				if (instance != null)
				{
					func.accept(instance);
				}
				else
				{
					switch (state)
					{
					case NOT_INIT:
						JMLog.error(out -> out.print(JMLog.JMATA_ERROR_IN_NOT_INIT));
						break;
					case RUNNING:
						JMLog.error(out -> out.print(JMLog.JMATA_ERROR_IN_RUNNING));
						break;
					case RELEASED:
						JMLog.debug(out -> out.print(JMLog.JMATA_ERROR_IN_RELEASED));
						break;
					default:
						JMLog.error(out -> out.print(JMLog.JMATA_ERROR_IN_UNDEFINED, state.name()));
						break;
					}
				}
			}
			finally
			{
				readLock.unlock();
			}
		}
		catch (RejectedExecutionException e)
		{
			JMLog.error(out -> out.print(JMLog.JMATA_REJECTED_EXECUTION_EXCEPTION));
		}
	}
	
	/* ================================== ↑ Static Part ================================== */
	
	private final Map<Object, JMMachine> machineMap;
	private final ExecutorService globalQue;
	
	private JMataImpl()
	{
		this.machineMap = new HashMap<>();
		this.globalQue = Executors.newSingleThreadExecutor(r ->
		{
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName("JMataGlobalThread");
			return t;
		});
	}
	
	private void destroy(final Runnable releaseWork)
	{
		globalQue.execute(() ->
		{
			writeLock.lock();
			
			try
			{
				state = STATE.RELEASED;
				
				for (JMMachine machine : instance.machineMap.values())
				{
					machine.terminate();
				}
				
				if (releaseWork != null)
				{
					releaseWork.run();
				}
				
				instance.globalQue.shutdownNow();
				instance = null;
				
				JMLog.debug(out -> out.print(JMLog.JMATA_RELEASED));
				JMLog.setLogger(null, null);
			}
			finally
			{
				writeLock.unlock();
			}
		});
	}
	
	void buildMachine(final Object machineTag, final String machineName, final Consumer<JMBuilder.Builder> builder)
	{
		globalQue.execute(() ->
		{
			builder.accept(JMBuilder.Constructor.getNew(machineName, machineMap.containsKey(machineTag), machine ->
			{
				final JMMachine oldMachine = machineMap.put(machineTag, machine);
				if (oldMachine != null)
				{
					oldMachine.terminate();
				}
			}));
		});
	}
	
	void runMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).run();
			}
		});
	}
	
	void pauseMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).pause();
			}
		});
	}
	
	void stopMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).stop();
			}
		});
	}
	
	void terminateMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				final JMMachine machineToTerminate = machineMap.remove(machineTag);
				if (machineToTerminate != null)
				{
					machineToTerminate.terminate();
				}
			}
		});
	}
	
	<S> void input(final Object machineTag, final S signal)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).input(signal);
			}
		});
	}
	
	void setMachineLogEnabled(final Object machineTag, final boolean enabled)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).setLogEnabled(enabled);
			}
		});
	}
}