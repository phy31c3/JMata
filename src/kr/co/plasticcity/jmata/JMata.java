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
	
	public static void buildMachine(Class<?> machineTag, JMConsumer<JMBuilder> builder)
	{
		JMataImpl.get().buildMachine(machineTag, builder);
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 가동
	 */
	public static void runMachine(Class<?> machineTag)
	{
		JMataImpl.get().runMachine(machineTag);
	}
	
	public static void runMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().runMachine(machineTag, machineIdx);
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 정지
	 */
	public static void stopMachine(Class<?> machineTag)
	{
		JMataImpl.get().stopMachine(machineTag);
	}
	
	public static void stopMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().stopMachine(machineTag, machineIdx);
	}
	
	/**
	 * 해당 머신의 모든 인스턴스를 종료
	 */
	public static void terminateMachine(Class<?> machineTag)
	{
		JMataImpl.get().terminateMachine(machineTag);
	}
	
	public static void terminateMachine(Class<?> machineTag, int machineIdx)
	{
		JMataImpl.get().terminateMachine(machineTag, machineIdx);
	}
	
	public static <S> void inputTo(Class<?> machineTag, S signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static <S extends Enum<S>> void inputTo(Class<?> machineTag, Enum<S> signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static void inputTo(Class<?> machineTag, String signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static void inputTo(Class<?> machineTag, Integer signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static <S> void inputTo(Class<?> machineTag, int machineIdx, S signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static <S extends Enum<S>> void inputTo(Class<?> machineTag, int machineIdx, Enum<S> signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static void inputTo(Class<?> machineTag, int machineIdx, String signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
	
	public static void inputTo(Class<?> machineTag, int machineIdx, Integer signal)
	{
		JMataImpl.get().inputTo(machineTag, signal);
	}
}