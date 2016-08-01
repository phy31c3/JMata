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
	
	<S> void putEnterFunction(Class<S> signal, JMBiConsumer<S, Integer> func);
	
	void putExitFunction(JMVoidConsumer func);
	
	void putExitFunction(JMConsumer<Integer> func);
	
	<S> void putExitFunction(Class<S> signal, JMConsumer<S> func);
	
	<S> void putExitFunction(Class<S> signal, JMBiConsumer<S, Integer> func);
	
	void putSwitchRule(Class<?> signal, Class<?> stateTag);
}