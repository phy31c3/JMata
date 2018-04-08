package kr.co.plasticcity.jmata;

public interface JMMachine
{
	void setLogEnabled(final boolean enabled);
	
	void run();
	
	void pause();
	
	void stop();
	
	void terminate();
	
	<S> void input(final S signal);
}