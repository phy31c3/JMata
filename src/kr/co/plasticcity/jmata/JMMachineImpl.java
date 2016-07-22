package kr.co.plasticcity.jmata;

import java.util.*;

class JMMachineImpl implements JMMachine
{
	private int numInstances;
	private Map<Class<?>, JMStateCreater> stateMap;
	
	JMMachineImpl(int numInstances, Map<Class<?>, JMStateCreater> stateMap)
	{
		this.numInstances = numInstances;
		this.stateMap = stateMap;
	}
	
	@Override
	public MachineState getState(int idx)
	{
		return null;
	}
	
	@Override
	public void run(int idx)
	{
	}
	
	@Override
	public void stop(int idx)
	{
	}
	
	@Override
	public void terminate(int idx)
	{
	}
	
	@Override
	public <S> void input(int idx, S signal)
	{
	}
}