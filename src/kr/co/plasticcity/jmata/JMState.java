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
		static JMState getNew(final Object machineTag, final Class<?> stateTag)
		{
			return new JMStateImpl(machineTag, stateTag);
		}
	}
	
	/*########################### basic operation ###########################*/
	
	/**
	 * Call only on first run after machine creation
	 */
	Object runEnterFunction();
	
	<S> Object runEnterFunctionC(final S signal);
	
	<S extends Enum<S>> Object runEnterFunction(final Enum<S> signal);
	
	Object runEnterFunction(final String signal);
	
	/**
	 * Call only on machine shutdown
	 */
	void runExitFunction();
	
	<S> Object runExitFunctionC(final S signal, final JMPredicate<Class<?>> hasState, final JMFunction<Class<?>, Object> nextEnter);
	
	<S extends Enum<S>> Object runExitFunction(final Enum<S> signal, final JMPredicate<Class<?>> hasState, final JMFunction<Class<?>, Object> nextEnter);
	
	Object runExitFunction(final String signal, final JMPredicate<Class<?>> hasState, final JMFunction<Class<?>, Object> nextEnter);
	
	/*########################### for modify ###########################*/
	
	void putEnterFunction(final JMSupplier<Object> func);
	
	void putEnterFunction(final Class<?> signal, final JMFunction<? super Object, Object> func);
	
	void putEnterFunction(final Enum<?> signal, final JMFunction<Enum<?>, Object> func);
	
	void putEnterFunction(final String signal, final JMFunction<String, Object> func);
	
	void putExitFunction(final JMVoidConsumer func);
	
	void putExitFunction(final Class<?> signal, final JMConsumer<? super Object> func);
	
	void putExitFunction(final Enum<?> signal, final JMConsumer<Enum<?>> func);
	
	void putExitFunction(final String signal, final JMConsumer<String> func);
	
	void putSwitchRule(final Class<?> signal, final Class<?> stateTag);
	
	void putSwitchRule(final Enum<?> signal, final Class<?> stateTag);
	
	void putSwitchRule(final String signal, final Class<?> stateTag);
}