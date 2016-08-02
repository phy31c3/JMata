package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

interface JMStateCreater extends JMState
{
	class Constructor
	{
		static JMStateCreater getNew(Class<?> tag)
		{
			return new JMStateImpl(tag);
		}
	}
	
	void putEnterFunction(JMVoidConsumer func);
	
	void putEnterFunction(JMConsumer<Integer> func);
	
	<S> void putEnterFunction(Class<S> signal, JMConsumer<S> func);
	
	void putEnterFunction(Enum<?> signal, JMConsumer<Enum<?>> func);
	
	void putEnterFunction(String signal, JMConsumer<String> func);
	
	void putEnterFunction(Integer signal, JMConsumer<Integer> func);
	
	<S> void putEnterFunction(Class<S> signal, JMBiConsumer<S, Integer> func);
	
	void putEnterFunction(Enum<?> signal, JMBiConsumer<Enum<?>, Integer> func);
	
	void putEnterFunction(String signal, JMBiConsumer<String, Integer> func);
	
	void putEnterFunction(Integer signal, JMBiConsumer<Integer, Integer> func);
	
	void putExitFunction(JMVoidConsumer func);
	
	void putExitFunction(JMConsumer<Integer> func);
	
	<S> void putExitFunction(Class<S> signal, JMConsumer<S> func);
	
	void putExitFunction(Enum<?> signal, JMConsumer<Enum<?>> func);
	
	void putExitFunction(String signal, JMConsumer<String> func);
	
	void putExitFunction(Integer signal, JMConsumer<Integer> func);
	
	<S> void putExitFunction(Class<S> signal, JMBiConsumer<S, Integer> func);
	
	void putExitFunction(Enum<?> signal, JMBiConsumer<Enum<?>, Integer> func);
	
	void putExitFunction(String signal, JMBiConsumer<String, Integer> func);
	
	void putExitFunction(Integer signal, JMBiConsumer<Integer, Integer> func);
	
	void putSwitchRule(Class<?> signal, Class<?> stateTag);
	
	<S extends Enum<S>> void putSwitchRule(Enum<S> signal, Class<?> stateTag);
	
	void putSwitchRule(String signal, Class<?> stateTag);
	
	void putSwitchRule(Integer signal, Class<?> stateTag);
}