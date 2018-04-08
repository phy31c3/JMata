package kr.co.plasticcity.jmata;

/**
 * Created by JongsunYu on 2018-04-07.
 */

public interface JMInstantMachine
{
	void setLogEnabled(final boolean enabled);
	
	void run();
	
	/**
	 * In an instant machine, pause is virtually identical to stop.
	 */
	void pause();
	
	/**
	 * In an instant machine, stop is virtually identical to pause.
	 */
	void stop();
	
	void terminate();
	
	<S> void input(final S signal);
}
