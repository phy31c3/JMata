package kr.co.plasticcity.jmata;

interface JMState
{
	static JMState getNew()
	{
		return new JMStateImpl();
	}
}