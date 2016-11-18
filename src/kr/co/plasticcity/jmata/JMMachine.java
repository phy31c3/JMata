package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

interface JMMachine
{
	class Constructor
	{
		static JMMachine getNew(Object tag, int numInstances, Class<?> startState, Map<Class<?>, ? extends JMState> stateMap, JMVoidConsumer terminateWork)
		{
			return new JMMachineImpl(tag, numInstances, startState, stateMap, terminateWork);
		}
	}
	
	void runAll();
	
	void run(int idx) throws JMException;
	
	void stopAll();
	
	void stop(int idx) throws JMException;
	
	void terminateAll();
	
	/**
	 * @return 모든 머신 인스턴스가 종료 된 경우 true
	 */
	boolean terminate(int idx) throws JMException;
	
	<S> void inputToAll(S signal) throws JMException;
	
	<S extends Enum<S>> void inputToAll(Enum<S> signal) throws JMException;
	
	void inputToAll(String signal) throws JMException;
	
	<S> void input(int idx, S signal) throws JMException;
	
	<S extends Enum<S>> void input(int idx, Enum<S> signal) throws JMException;
	
	void input(int idx, String signal) throws JMException;
}