package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

interface JMStateCreater extends JMState
{
	void putEnterFunction(JMVoidConsumer func);
	
	void putEnterFunction(Consumer<Integer> func);
	
	<S> void putEnterFunction(Class<S> signal, Consumer<S> func);
	
	<S> void putEnterFunction(Class<S> signal, BiConsumer<S, Integer> func);
	
	void putExitFunction(JMVoidConsumer func);
	
	void putExitFunction(Consumer<Integer> func);
	
	<S> void putExitFunction(Class<S> signal, Consumer<S> func);
	
	<S> void putExitFunction(Class<S> signal, BiConsumer<S, Integer> func);
	
	void putSwitchRule(Class<?> signal, Class<?> stateTag);
}