package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMPredicate;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

interface JMState
{
	class Constructor
	{
		static JMState getNew(Object machineTag, Class<?> stateTag)
		{
			return new JMStateImpl(machineTag, stateTag);
		}
	}
	
	/*########################### basic operation ###########################*/
	
	/**
	 * Call only on first run after machine creation
	 */
	Object runEnterFunction();
	
	<S> Object runEnterFunctionC(S signal);
	
	<S extends Enum<S>> Object runEnterFunction(Enum<S> signal);
	
	Object runEnterFunction(String signal);
	
	/**
	 * Call only on machine shutdown
	 */
	void runExitFunction();
	
	<S> Object runExitFunctionC(S signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter);
	
	<S extends Enum<S>> Object runExitFunction(Enum<S> signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter);
	
	Object runExitFunction(String signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter);
	
	/*########################### for modify ###########################*/
	
	void putEnterFunction(JMSupplier<Object> func);
	
	void putEnterFunction(Class<?> signal, JMFunction<? super Object, Object> func);
	
	void putEnterFunction(Enum<?> signal, JMFunction<Enum<?>, Object> func);
	
	void putEnterFunction(String signal, JMFunction<String, Object> func);
	
	void putExitFunction(JMVoidConsumer func);
	
	void putExitFunction(Class<?> signal, JMConsumer<? super Object> func);
	
	void putExitFunction(Enum<?> signal, JMConsumer<Enum<?>> func);
	
	void putExitFunction(String signal, JMConsumer<String> func);
	
	void putSwitchRule(Class<?> signal, Class<?> stateTag);
	
	void putSwitchRule(Enum<?> signal, Class<?> stateTag);
	
	void putSwitchRule(String signal, Class<?> stateTag);
}