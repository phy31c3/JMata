package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.Consumer;

public interface JMata
{
	static void initialize()
	{
		JMataImpl.initialize(null, null);
	}
	
	/**
	 * @param debugLogger nullable
	 */
	static void initialize(final Consumer<String> debugLogger)
	{
		JMataImpl.initialize(debugLogger, null);
	}
	
	/**
	 * @param debugLogger nullable
	 * @param errorLogger nullable
	 */
	static void initialize(final Consumer<String> debugLogger, final Consumer<String> errorLogger)
	{
		JMataImpl.initialize(debugLogger, errorLogger);
	}
	
	static void release()
	{
		JMataImpl.release(null);
	}
	
	static void release(final Runnable releaseWork)
	{
		JMataImpl.release(releaseWork);
	}
	
	static void buildMachine(final Object machineTag, final Consumer<JMBuilder.Builder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, JMLog.getPackagelessName(machineTag), builder));
	}
	
	static void buildMachine(final Object machineTag, final String machineName, final Consumer<JMBuilder.Builder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, machineName, builder));
	}
	
	static void runMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.runMachine(machineTag));
	}
	
	static void pauseMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.pauseMachine(machineTag));
	}
	
	static void stopMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.stopMachine(machineTag));
	}
	
	static void terminateMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.terminateMachine(machineTag));
	}
	
	static <S> void input(final Object machineTag, final S signal)
	{
		JMataImpl.post(jmata -> jmata.input(machineTag, signal));
	}
	
	static void setMachineLogEnabled(final Object machineTag, final boolean enabled)
	{
		JMataImpl.post(jmata -> jmata.setMachineLogEnabled(machineTag, enabled));
	}
}