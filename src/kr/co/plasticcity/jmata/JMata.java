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
	
	static <M> void buildMachine(Class<M> machineTag, Consumer<JMMachineBuilder> builder)
	{
		JMataImpl.get().buildMachine(machineTag, builder);
	}
	
	static <M> void runMachine(Class<M> machineTag)
	{
		JMataImpl.get().runMachine(machineTag);
	}
	
	static <M> void runMachine(Class<M> machineTag, int machineIdx)
	{
		JMataImpl.get().runMachine(machineTag, machineIdx);
	}
	
	static <M> void stopMachine(Class<M> machineTag)
	{
		JMataImpl.get().stopMachine(machineTag);
	}
	
	static <M> void stopMachine(Class<M> machineTag, int machineIdx)
	{
		JMataImpl.get().stopMachine(machineTag, machineIdx);
	}
	
	static <M> void terminateMachine(Class<M> machineTag)
	{
		JMataImpl.get().terminateMachine(machineTag);
	}
	
	static <M> void terminateMachine(Class<M> machineTag, int machineIdx)
	{
		JMataImpl.get().terminateMachine(machineTag, machineIdx);
	}
}