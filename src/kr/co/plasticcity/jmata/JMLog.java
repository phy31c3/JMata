package kr.co.plasticcity.jmata;

import java.util.function.*;

class JMLog
{
	private static Consumer<String> logFunc;
	
	static void setLogFunction(Consumer<String> logFunc)
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