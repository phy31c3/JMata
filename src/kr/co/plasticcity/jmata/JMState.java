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
	
	<S> void runEnterFunctionC(int machineIdx, S signal);
	
	/**
	 * @return 전달된 신호와 연결된 전이 규칙이 없는 경우 == false
	 */
	<S extends Enum<S>> boolean runEnterFunction(int machineIdx, Enum<S> signal);
	
	/**
	 * @return 전달된 신호와 연결된 전이 규칙이 없는 경우 == false
	 */
	boolean runEnterFunction(int machineIdx, String signal);
	
	<S> void runExitFunctionC(int machineIdx, S signal, JMConsumer<Class<?>> nextState);
	
	/**
	 * @return 전달된 신호와 연결된 전이 규칙이 없는 경우 == false
	 */
	<S extends Enum<S>> boolean runExitFunction(int machineIdx, Enum<S> signal, JMConsumer<Class<?>> nextState);
	
	/**
	 * @return 전달된 신호와 연결된 전이 규칙이 없는 경우 == false
	 */
	boolean runExitFunction(int machineIdx, String signal, JMConsumer<Class<?>> nextState);
}