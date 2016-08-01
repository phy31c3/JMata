package kr.co.plasticcity.jmata;

import java.util.function.*;

interface JMState
{
	static JMState getNew(Class<?> tag)
	{
		return new JMStateImpl(tag);
	}
	
	/**
	 * 머신 생성 후 처음 Run 할 시에만 호출
	 */
	void runEnterFunction(int machineIdx);
	
	<S> void runEnterFunction(int machineIdx, S signal);
	
	<S> void runExitFunction(int machineIdx, S signal, Consumer<Class<?>> nextState);
}