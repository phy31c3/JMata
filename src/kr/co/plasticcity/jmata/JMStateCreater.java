package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

interface JMStateCreater extends JMState
{
	interface Constructor
	{
		static JMStateCreater getNew(Object machineTag, Class<?> stateTag)
		{
			return new JMStateImpl(machineTag, stateTag);
		}
	}
	
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