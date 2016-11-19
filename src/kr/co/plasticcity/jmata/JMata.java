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
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, builder));
	}
	
	public static void runMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.runMachine(machineTag));
	}
	
	public static void stopMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.stopMachine(machineTag));
	}
	
	public static void terminateMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.terminateMachine(machineTag));
	}
	
	public static <S> void inputTo(final Object machineTag, final S signal)
	{
		JMataImpl.post(jmata -> jmata.inputTo(machineTag, signal));
	}
}