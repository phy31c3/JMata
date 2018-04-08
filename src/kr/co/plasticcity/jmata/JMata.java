package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.Consumer;

public interface JMata
{
	/* ================================== Basic Machine ================================== */
	
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
	
	/**
	 * @param machineTag not null
	 * @param builder    not null
	 */
	static void buildMachine(final Object machineTag, final Consumer<JMBuilder.Builder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, JMLog.getPackagelessName(machineTag), builder));
	}
	
	/**
	 * @param machineTag  not null
	 * @param machineName not null
	 * @param builder     not null
	 */
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
	
	/* ================================== Instant Machine ================================== */
	
	static JMMachine buildInstantMachine(final Consumer<JMBuilder.BaseDefiner> builder)
	{
		return buildInstantMachine("Instant Machine#" + new Object().hashCode(), builder);
	}
	
	static JMMachine buildInstantMachine(final String machineName, final Consumer<JMBuilder.BaseDefiner> builder)
	{
		final JMMachine[] m = new JMMachine[1];
		final JMBuilder jmBuilder = new JMBuilderImpl.InstantBuilderImpl(machineName, machine -> m[0] = machine);
		jmBuilder.ifPresentThenIgnoreThis(builder);
		return m[0];
	}
}