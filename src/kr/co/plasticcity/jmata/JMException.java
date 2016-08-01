package kr.co.plasticcity.jmata;

class JMException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	JMException(String format, Object... args)
	{
		this.msg = String.format(format, args);
	}
	
	public void printJMLog()
	{
		JMLog.out(msg);
	}
}