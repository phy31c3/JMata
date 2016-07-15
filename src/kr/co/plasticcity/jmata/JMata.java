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
	
	static JMMachineBuilder buildMachine(Class<?> machineTag)
	{
		return JMataImpl.get().buildMachine(machineTag);
	}
}