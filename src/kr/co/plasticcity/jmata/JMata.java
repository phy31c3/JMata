package kr.co.plasticcity.jmata;

import java.util.function.*;

public interface JMata
{
	/**
	 * 어플리케이션 시작 시 반드시 호출.
	 */
	default void initialize()
	{
		JMataImpl.init();
	}
	
	default MachineBuilderOptional buildMachine(Class<?> tag)
	{
		return JMataImpl.get().buildMachine(tag);
	}
	
	public interface MachineBuilderOptional
	{
		void ifCreated(Consumer<JMMachineBuilder> func);
		
		void ifPresent(Consumer<JMMachineBuilder> func);
		
		void ifAnyway(Consumer<JMMachineBuilder> func);
	}
}