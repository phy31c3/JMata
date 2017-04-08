package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

public interface JMata
{
	static void initialize()
	{
		JMataImpl.initialize(null, null);
	}
	
	static void initialize(JMConsumer<String> debugLogger)
	{
		JMataImpl.initialize(debugLogger, null);
	}
	
	static void initialize(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		JMataImpl.initialize(debugLogger, errorLogger);
	}
	
	static void release()
	{
		JMataImpl.release(null);
	}
	
	static void release(final JMVoidConsumer releaseWork)
	{
		JMataImpl.release(releaseWork);
	}
	
	static void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
	{
		JMataImpl.post(jmata -> jmata.buildMachine(machineTag, builder));
	}
	
	static void runMachine(final Object machineTag)
	{
		JMataImpl.post(jmata -> jmata.runMachine(machineTag));
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
}