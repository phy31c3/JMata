package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

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
	
	static void initialize(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		writeLock.lock();
		
		try
		{
			if (state != STATE.RUNNING)
			{
				state = STATE.RUNNING;
				instance = new JMataImpl();
				JMLog.setLogger(debugLogger, errorLogger);
				JMLog.debug(JMLog.JMATA_INITIALIZED);
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	static void release(final JMVoidConsumer releaseWork)
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
	
	static void post(JMConsumer<JMataImpl> func)
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
						JMLog.error(JMLog.JMATA_ERROR_IN_NOT_INIT);
						break;
					case RUNNING:
						JMLog.error(JMLog.JMATA_ERROR_IN_RUNNING);
						break;
					case RELEASED:
						JMLog.debug(JMLog.JMATA_ERROR_IN_RELEASED);
						break;
					default:
						JMLog.error(JMLog.JMATA_ERROR_IN_UNDEFINED, state.name());
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
			JMLog.error(JMLog.JMATA_REJECTED_EXECUTION_EXCEPTION);
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
	
	private void destroy(final JMVoidConsumer releaseWork)
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
					releaseWork.accept();
				}
				
				instance.globalQue.shutdownNow();
				instance = null;
				
				JMLog.debug(JMLog.JMATA_RELEASED);
				JMLog.setLogger(null, null);
			}
			finally
			{
				writeLock.unlock();
			}
		});
	}
	
	void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
	{
		globalQue.execute(() ->
		{
			JMLog.debug(JMLog.MACHINE_BUILD_STARTED, machineTag);
			builder.accept(JMBuilder.Constructor.getNew(machineTag, machineMap.containsKey(machineTag), machine ->
			{
				JMMachine oldMachine = machineMap.put(machineTag, machine);
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
				JMMachine machineToTerminate = machineMap.remove(machineTag);
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
}