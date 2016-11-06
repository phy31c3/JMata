package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

class JMLog
{
	private static JMConsumer<String> debug;
	private static JMConsumer<String> error;
	
	static void setLogger(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		JMLog.debug = debugLogger;
		JMLog.error = errorLogger;
	}
	
	static void debug(String format, Object... args)
	{
		if (debug != null)
		{
			debug.accept(String.format(format, args));
		}
	}
	
	static void error(String format, Object... args)
	{
		if (error != null)
		{
			error.accept(String.format(format, args));
		}
	}
}