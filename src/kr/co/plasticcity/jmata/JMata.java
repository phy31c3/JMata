package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

public class JMata
{
	/**
	 * 어플리케이션 시작 시 반드시 호출.
	 */
	public static void initialize()
	{
		JMataImpl.initialize();
	}
	
	public static void setLogFunction(JMConsumer<String> logFunc)
	{
		JMLog.setLogFunction(logFunc);
	}
	
	public static void release()
	{
		JMataImpl.release();
		JMLog.setLogFunction(null);
	}
	
	public static void buildMachine(final Class<?> machineTag, final JMConsumer<JMBuilder> builder)
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
	public static void runMachine(final Class<?> machineTag)
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
	
	public static void runMachine(final Class<?> machineTag, final int machineIdx)
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
	public static void stopMachine(final Class<?> machineTag)
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
	
	public static void stopMachine(final Class<?> machineTag, final int machineIdx)
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
	public static void terminateMachine(final Class<?> machineTag)
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
	
	public static void terminateMachine(final Class<?> machineTag, final int machineIdx)
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
	
	public static <S> void inputTo(final Class<?> machineTag, final S signal)
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
	
	public static <S extends Enum<S>> void inputTo(final Class<?> machineTag, final Enum<S> signal)
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
	
	public static void inputTo(final Class<?> machineTag, final String signal)
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
	
	public static <S> void inputTo(final Class<?> machineTag, final int machineIdx, final S signal)
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
	
	public static <S extends Enum<S>> void inputTo(final Class<?> machineTag, final int machineIdx, final Enum<S> signal)
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
	
	public static void inputTo(final Class<?> machineTag, final int machineIdx, final String signal)
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
}