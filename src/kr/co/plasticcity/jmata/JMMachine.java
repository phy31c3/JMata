package kr.co.plasticcity.jmata;

import java.util.*;

interface JMMachine
{
	class Constructor
	{
		static JMMachine getNew(Class<?> tag, int numInstances, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap)
		{
			return new JMMachineImpl(tag, numInstances, startState, stateMap);
		}
	}
	
	void runAll();
	
	void run(int idx) throws JMException;
	
	void stopAll();
	
	void stop(int idx) throws JMException;
	
	void terminateAll();
	
	void terminate(int idx) throws JMException;
	
	<S> void inputToAll(S signal) throws JMException;
	
	<S extends Enum<S>> void inputToAll(Enum<S> signal) throws JMException;
	
	void inputToAll(String signal) throws JMException;
	
	<S> void input(int idx, S signal) throws JMException;
	
	<S extends Enum<S>> void input(int idx, Enum<S> signal) throws JMException;
	
	void input(int idx, String signal) throws JMException;
}