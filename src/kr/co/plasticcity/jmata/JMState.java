package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

interface JMState
{
	class Constructor
	{
		static JMState getNew(Class<?> tag)
		{
			return new JMStateImpl(tag);
		}
	}
	
	/**
	 * 머신 생성 후 처음 Run 할 시에만 호출
	 */
	void runEnterFunction(int machineIdx);
	
	<S> void runEnterFunction(int machineIdx, S signal);
	
	<S extends Enum<S>> void runEnterFunction(int machineIdx, Enum<S> signal);
	
	void runEnterFunction(int machineIdx, String signal);
	
	void runEnterFunction(int machineIdx, Integer signal);
	
	<S> void runExitFunction(int machineIdx, S signal, JMConsumer<Class<?>> nextState);
	
	<S extends Enum<S>> void runExitFunction(int machineIdx, Enum<S> signal, JMConsumer<Class<?>> nextState);
	
	void runExitFunction(int machineIdx, String signal, JMConsumer<Class<?>> nextState);
	
	void runExitFunction(int machineIdx, Integer signal, JMConsumer<Class<?>> nextState);
}