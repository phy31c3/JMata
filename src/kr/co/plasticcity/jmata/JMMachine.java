package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

interface JMMachine
{
	class Constructor
	{
		static JMMachine getNew(Object tag, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap, JMVoidConsumer terminateWork)
		{
			return new JMMachineImpl(tag, startState, stateMap, terminateWork);
		}
	}
	
	void run();
	
	void stop();
	
	void terminate();
	
	<S> void input(S signal);
}