package kr.co.plasticcity.jmata;

import java.util.function.Consumer;

public class JMata
{
	public static void initialize()
	{
		JMataImpl.initialize(null, null);
	}
	
	/**
	 * @param debugLogger nullable
	 */
	public static void initialize(final Consumer<String> debugLogger)
	{
		JMataImpl.initialize(debugLogger, null);
	}
	
	/**
	 * @param debugLogger nullable
	 * @param errorLogger nullable
	 */
	public static void initialize(final Consumer<String> debugLogger, final Consumer<String> errorLogger)
	{
		JMataImpl.initialize(debugLogger, errorLogger);
	}
	
	public static void release()
	{
		JMataImpl.release(null);
	}
	
	public static void release(final Runnable releaseWork)
	{
		JMataImpl.release(releaseWork);
	}
	
	public static void buildMachine(final Object machineTag, final Consumer<JMBuilder.Builder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, JMLog.getPackagelessName(machineTag), builder));
	}
	
	public static void buildMachine(final Object machineTag, final String machineName, final Consumer<JMBuilder.Builder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, machineName, builder));
	}
	
	public static void runMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.runMachine(machineTag));
	}
	
	public static void pauseMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.pauseMachine(machineTag));
	}
	
	public static void stopMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.stopMachine(machineTag));
	}
	
	public static void terminateMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.terminateMachine(machineTag));
	}
	
	public static <S> void input(final Object machineTag, final S signal)
	{
		JMataImpl.post(jmata -> jmata.input(machineTag, signal));
	}
}