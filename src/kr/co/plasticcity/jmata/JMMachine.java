package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.JMata.*;

interface JMMachine
{
	static JMMachine getNew(Class<?> tag, int numInstances, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap)
	{
		return new JMMachineImpl(tag, numInstances, startState, stateMap);
	}
	
	void run(int idx) throws JMException;
	
	void runAll();
	
	void stop(int idx) throws JMException;
	
	void stopAll();
	
	void terminate(int idx) throws JMException;
	
	void terminateAll();
	
	<S> void input(int idx, S signal) throws JMException;
	
	<S> void inputAll(S signal) throws JMException;
}