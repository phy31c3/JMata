package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

interface JMState
{
	/**
	 * 머신 생성 후 처음 Run 할 시에만 호출
	 */
	Object runEnterFunction();
	
	<S> Object runEnterFunctionC(S signal);
	
	<S extends Enum<S>> Object runEnterFunction(Enum<S> signal);
	
	Object runEnterFunction(String signal);
	
	/**
	 * 머신 종료 시에만 호출
	 */
	void runExitFunction();
	
	<S> Object runExitFunctionC(S signal, JMFunction<Class<?>, Object> nextState);
	
	<S extends Enum<S>> Object runExitFunction(Enum<S> signal, JMFunction<Class<?>, Object> nextState);
	
	Object runExitFunction(String signal, JMFunction<Class<?>, Object> nextState);
}