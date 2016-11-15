package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

public class JMata
{
	public static void initialize()
	{
		initialize(null, null);
	}
	
	public static void initialize(JMConsumer<String> debugLogger)
	{
		initialize(debugLogger, null);
	}
	
	public static void initialize(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		JMataImpl.initialize(debugLogger, errorLogger);
	}
	
	public static void release()
	{
		JMataImpl.release();
	}
	
	public static void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.buildMachine(machineTag, builder);
			}
		});
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 가동
	 */
	public static void runMachine(final Object machineTag)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.runMachine(machineTag);
			}
		});
	}
	
	public static void runMachine(final Object machineTag, final int machineIdx)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.runMachine(machineTag, machineIdx);
			}
		});
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 정지
	 */
	public static void stopMachine(final Object machineTag)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.stopMachine(machineTag);
			}
		});
	}
	
	public static void stopMachine(final Object machineTag, final int machineIdx)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.stopMachine(machineTag, machineIdx);
			}
		});
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 종료
	 */
	public static void terminateMachine(final Object machineTag)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.terminateMachine(machineTag);
			}
		});
	}
	
	public static void terminateMachine(final Object machineTag, final int machineIdx)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.terminateMachine(machineTag, machineIdx);
			}
		});
	}
	
	public static <S> void inputTo(final Object machineTag, final S signal)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.inputTo(machineTag, signal);
			}
		});
	}
	
	public static <S> void inputTo(final Object machineTag, final int machineIdx, final S signal)
	{
		JMataImpl.post(new JMConsumer<JMataImpl>()
		{
			@Override
			public void accept(JMataImpl jmata)
			{
				jmata.inputTo(machineTag, machineIdx, signal);
			}
		});
	}
}