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
	void runEnterFunction();
	
	<S> void runEnterFunctionC(S signal);
	
	<S extends Enum<S>> void runEnterFunction(Enum<S> signal);
	
	void runEnterFunction(String signal);
	
	<S> void runExitFunctionC(S signal, JMConsumer<Class<?>> nextState);
	
	<S extends Enum<S>> void runExitFunction(Enum<S> signal, JMConsumer<Class<?>> nextState);
	
	void runExitFunction(String signal, JMConsumer<Class<?>> nextState);
}