package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

class JMLog
{
	private static JMConsumer<String> logFunc;
	
	static void setLogFunction(JMConsumer<String> logFunc)
	{
		JMLog.logFunc = logFunc;
	}
	
	static void out(String format, Object... args)
	{
		if (logFunc != null)
		{
			logFunc.accept(String.format(format, args));
		}
	}
}