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
	
	void putEnterFunction(Class<?> signal, JMConsumer<? super Object> func);
	
	void putEnterFunction(Enum<?> signal, JMConsumer<Enum<?>> func);
	
	void putEnterFunction(String signal, JMConsumer<String> func);
	
	void putExitFunction(JMVoidConsumer func);
	
	void putExitFunction(Class<?> signal, JMConsumer<? super Object> func);
	
	void putExitFunction(Enum<?> signal, JMConsumer<Enum<?>> func);
	
	void putExitFunction(String signal, JMConsumer<String> func);
	
	void putSwitchRule(Class<?> signal, Class<?> stateTag);
	
	void putSwitchRule(Enum<?> signal, Class<?> stateTag);
	
	void putSwitchRule(String signal, Class<?> stateTag);
}