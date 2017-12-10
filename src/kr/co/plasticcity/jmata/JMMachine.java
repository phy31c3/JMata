package kr.co.plasticcity.jmata;

import java.util.Map;

interface JMMachine
{
	class Constructor
	{
		static JMMachine getNew(final Object tag, final Class startState, final Map<Class, ? extends JMState> stateMap, final Runnable terminateWork)
		{
			return new JMMachineImpl(tag, startState, stateMap, terminateWork);
		}
	}
	
	void run();
	
	void pause();
	
	void stop();
	
	void terminate();
	
	<S> void input(final S signal);
}