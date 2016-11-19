package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;

import kr.co.plasticcity.jmata.function.*;

class JMataImpl
{
	/* ================================== ↓ Static Part ================================== */
	
	private static final int NUM_PERMITS = Runtime.getRuntime().availableProcessors();
	private static final Semaphore permit = new Semaphore(NUM_PERMITS, true);
	private static volatile JMataImpl instance;
	private static volatile STATE state = STATE.NOT_INIT;
	
	private enum STATE
	{
		NOT_INIT, RUNNING, RELEASED;
	}
	
	static void initialize(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		try
		{
			permit.acquire(NUM_PERMITS);
			
			try
			{
				JMLog.setLogger(debugLogger, errorLogger);
				clearInstance();
				instance = new JMataImpl();
				state = STATE.RUNNING;
			}
			finally
			{
				permit.release(NUM_PERMITS);
			}
		}
		catch (InterruptedException e)
		{
			initialize(debugLogger, errorLogger);
		}
	}
	
	static void release()
	{
		try
		{
			permit.acquire(NUM_PERMITS);
			
			try
			{
				if (state == STATE.RUNNING)
				{
					JMLog.setLogger(null, null);
					clearInstance();
					state = STATE.RELEASED;
				}
			}
			finally
			{
				permit.release(NUM_PERMITS);
			}
		}
		catch (InterruptedException e)
		{
			release();
		}
	}
	
	static void post(JMConsumer<JMataImpl> func)
	{
		try
		{
			permit.acquire();
			
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
						JMLog.error("JMata 초기화 오류 : 최초 JMata.initialize()를 호출해주세요.");
						break;
					case RUNNING:
						JMLog.error("알 수 없는 오류 발생 : JMata가 RUNNIG 상태이나 instance == null");
						break;
					case RELEASED:
						JMLog.debug("JMata가 이미 해제 : JMata가 해제 되었으나 JMata관련 명령(JMata의 static method)을 호출함");
						break;
					}
				}
			}
			finally
			{
				permit.release();
			}
		}
		catch (InterruptedException e)
		{
			/* do nothing */
		}
	}
	
	private static void clearInstance()
	{
		if (instance != null)
		{
			instance.globalQue.shutdownNow();
			for (JMMachine machine : instance.machineMap.values())
			{
				machine.terminate();
			}
			instance = null;
		}
	}
	
	/* ================================== ↑ Static Part ================================== */
	
	private final Map<Object, JMMachine> machineMap;
	private final ExecutorService globalQue;
	
	private JMataImpl()
	{
		this.machineMap = new ConcurrentHashMap<>();
		this.globalQue = Executors.newSingleThreadExecutor();
		this.globalQue.execute(() -> Thread.currentThread().setName("JMataGlobalThread"));
	}
	
	void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
	{
		globalQue.execute(() ->
		{
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
				machineMap.get(machineTag).terminate();
				machineMap.remove(machineTag);
			}
		});
	}
	
	<S> void inputTo(final Object machineTag, final S signal)
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