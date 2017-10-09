package kr.co.plasticcity.jmata;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

interface JMState
{
	class Constructor
	{
		static JMState getNew(final Object machineTag, final Class stateTag)
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
	
	Object runEnterFunction(final Enum signal);
	
	Object runEnterFunction(final String signal);
	
	/**
	 * Call only on machine shutdown
	 */
	void runExitFunction();
	
	<S> Object runExitFunctionC(final S signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter);
	
	Object runExitFunction(final Enum signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter);
	
	Object runExitFunction(final String signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter);
	
	/*########################### for modify ###########################*/
	
	void putEnterFunction(final Supplier<Object> func);
	
	void putEnterFunction(final Class signal, final Function<? super Object, Object> func);
	
	void putEnterFunction(final Enum signal, final Function<Enum, Object> func);
	
	void putEnterFunction(final String signal, final Function<String, Object> func);
	
	void putExitFunction(final Runnable func);
	
	void putExitFunction(final Class signal, final Consumer<? super Object> func);
	
	void putExitFunction(final Enum signal, final Consumer<Enum> func);
	
	void putExitFunction(final String signal, final Consumer<String> func);
	
	void putSwitchRule(final Class signal, final Class stateTag);
	
	void putSwitchRule(final Enum signal, final Class stateTag);
	
	void putSwitchRule(final String signal, final Class stateTag);
}