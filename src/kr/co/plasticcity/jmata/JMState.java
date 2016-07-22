package kr.co.plasticcity.jmata;

interface JMState
{
	static JMState getNew(Class<?> tag)
	{
		return new JMStateImpl(tag);
	}
}