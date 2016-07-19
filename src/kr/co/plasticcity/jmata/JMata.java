package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

public interface JMata
{
	/**
	 * 어플리케이션 시작 시 반드시 호출.
	 */
	static void initialize()
	{
		JMataImpl.initialize();
	}
	
	static void buildMachine(Class<?> machineTag, Consumer<JMMachineBuilder> builder)
	{
		JMataImpl.get().buildMachine(machineTag, builder);
	}
	
	static void runMachine(Class<?> machineTag)
	{
		JMataImpl.get().runMachine(machineTag, 0);
	}
	
	static void runMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().runMachine(machineTag, machineIdx);
	}
	
	static void stopMachine(Class<?> machineTag)
	{
		JMataImpl.get().stopMachine(machineTag, 0);
	}
	
	static void stopMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().stopMachine(machineTag, machineIdx);
	}
	
	static void terminateMachine(Class<?> machineTag)
	{
		JMataImpl.get().terminateMachine(machineTag, 0);
	}
	
	static void terminateMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().terminateMachine(machineTag, machineIdx);
	}
}