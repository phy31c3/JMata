package kr.co.plasticcity.jmata;

import java.util.Map;

interface JMMachine
{
	class Constructor
	{
		static JMMachine getNew(final Object tag, final Class startState, final Map<Class, ? extends JMState> stateMap,
		                        final Runnable onPause, final Runnable onResume, final Runnable onStop, final Runnable onRestart, final Runnable onTerminate)
		{
			return new JMMachineImpl(tag, startState, stateMap, onPause, onResume, onStop, onRestart, onTerminate);
		}
	}
	
	void run();
	
	void pause();
	
	void stop();
	
	void terminate();
	
	<S> void input(final S signal);
}