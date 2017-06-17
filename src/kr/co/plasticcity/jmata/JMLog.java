package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.JMConsumer;

class JMLog
{
	/* log in JMataImpl */
	static final String JMATA_INITIALIZED = "** JMata has been initialized";
	static final String JMATA_RELEASED = "** JMata has been released";
	static final String JMATA_ERROR_IN_NOT_INIT = "** JMata initialization error : Call JMata.initialize() first";
	static final String JMATA_ERROR_IN_RUNNING = "** JMata unknown error : JMata is in RUNNIG state, but instance == null";
	static final String JMATA_ERROR_IN_RELEASED = "** JMata already released : JMata is released, but JMata command is called";
	static final String JMATA_ERROR_IN_UNDEFINED = "** JMata undefined state : %s";
	static final String JMATA_REJECTED_EXECUTION_EXCEPTION = "** JMata RejectedExecutionException occurred";
	static final String MACHINE_BUILD_STARTED = "[%s] machine build started";
	
	/* log in JMBuilderImpl */
	static final String IGNORE_MACHINE_BUILD = "[%s] machine already exists, ignoring build";
	static final String REPLACE_MACHINE = "[%s] machine already exists and will be replaced with a new machine";
	static final String STATE_DEFINITION_DUPLICATED = "[%s] machine : Definition of state [%s] duplicated";
	
	/* log in JMMachineImpl */
	static final String MACHINE_BUILT = "[%s] machine has been built";
	static final String MACHINE_STATE_CHANGED = "[%s] machine state changed : [%s] -> [%s]";
	static final String STATE_SWITCHED_BY_CLASS = "[%s] machine : Switched from [%s] to [%s] by [%s]";
	static final String STATE_SWITCHED_BY_STRING = "[%s] machine : Switched from [%s] to [%s] by [\"%s\"]";
	static final String TERMINATION_WORK_FAILED_AS_TIMEOUT = "[%s] machine : The termination work failed because the machine shutdown took too long (over 1 second)";
	static final String TERMINATION_WORK_FAILED_AS_INTERRUPT = "[%s] machine : The termination work failed because this thread interrupted during termination";
	static final String SWITCH_TO_UNDEFINED_STATE_BY_CLASS = "[%s] machine : Tried to switch from [%s] to [%s] by [%s], but machine has no definition for [%s]";
	static final String SWITCH_TO_UNDEFINED_STATE_BY_STRING = "[%s] machine : Tried to switch from [%s] to [%s] by [\"%s\"], but machine has no definition for [%s]";
	
	/* log in JMStateImpl */
	static final String ENTER_FUNC_DUPLICATED = "[%s] machine : Definition of default entry function duplicated in state [%s]";
	static final String ENTER_BY_CLASS_FUNC_DUPLICATED = "[%s] machine : Definition of entry function for input [%s] duplicated in state [%s]";
	static final String ENTER_BY_STRING_FUNC_DUPLICATED = "[%s] machine : Definition of entry function for input [\"%s\"] duplicated in state [%s]";
	static final String EXIT_FUNC_DUPLICATED = "[%s] machine : Definition of default exit function duplicated in state [%s]";
	static final String EXIT_BY_CLASS_FUNC_DUPLICATED = "[%s] machine : Definition of exit function for input [%s] duplicated in state [%s]";
	static final String EXIT_BY_STRING_FUNC_DUPLICATED = "[%s] machine : Definition of exit function for input [\"%s\"] duplicated in state [%s]";
	static final String SWITCH_RULE_BY_CLASS_DUPLICATED = "[%s] machine : Definition of switch rule for input [%s] duplicated in state [%s]";
	static final String SWITCH_RULE_BY_STRING_DUPLICATED = "[%s] machine : Definition of switch rule for input [\"%s\"] duplicated in state [%s]";
	
	private static JMConsumer<String> debug;
	private static JMConsumer<String> error;
	
	private JMLog()
	{
		/* do nothing */
	}
	
	static void setLogger(final JMConsumer<String> debugLogger, final JMConsumer<String> errorLogger)
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