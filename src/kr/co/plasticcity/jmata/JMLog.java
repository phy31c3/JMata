package kr.co.plasticcity.jmata;

import java.util.function.Consumer;

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
	static final String STATE_DEFINITION_DUPLICATED = "[%s] definition of state [%s] duplicated";
	
	/* log in JMMachineImpl */
	static final String MACHINE_BUILT = "[%s] machine has been built";
	static final String MACHINE_STATE_CHANGED = "[%s] machine state changed : [%s] -> [%s]";
	static final String STATE_SWITCHED_BY_CLASS = "[%s] switched from [%s] to [%s] by [%s]";
	static final String STATE_SWITCHED_BY_STRING = "[%s] switched from [%s] to [%s] by [\"%s\"]";
	static final String TERMINATION_WORK_FAILED_AS_TIMEOUT = "[%s] termination work failed because the machine shutdown took too long (over 1 second)";
	static final String TERMINATION_WORK_FAILED_AS_INTERRUPT = "[%s] termination work failed because this thread interrupted during termination";
	
	/* log in JMStateImpl */
	static final String ENTER_FUNC_DUPLICATED = "[%s] definition of default entry function duplicated in state [%s]";
	static final String ENTER_BY_CLASS_FUNC_DUPLICATED = "[%s] definition of entry function for input [%s] duplicated in state [%s]";
	static final String ENTER_BY_STRING_FUNC_DUPLICATED = "[%s] definition of entry function for input [\"%s\"] duplicated in state [%s]";
	static final String EXIT_FUNC_DUPLICATED = "[%s] definition of default exit function duplicated in state [%s]";
	static final String EXIT_BY_CLASS_FUNC_DUPLICATED = "[%s] definition of exit function for input [%s] duplicated in state [%s]";
	static final String EXIT_BY_STRING_FUNC_DUPLICATED = "[%s] definition of exit function for input [\"%s\"] duplicated in state [%s]";
	static final String SWITCH_RULE_BY_CLASS_DUPLICATED = "[%s] definition of switch rule for input [%s] duplicated in state [%s]";
	static final String SWITCH_RULE_BY_STRING_DUPLICATED = "[%s] definition of switch rule for input [\"%s\"] duplicated in state [%s]";
	static final String SWITCH_TO_UNDEFINED_STATE_BY_CLASS = "[%s] tried to switch from [%s] to [%s] by [%s], but machine has no definition for [%s]";
	static final String SWITCH_TO_UNDEFINED_STATE_BY_STRING = "[%s] tried to switch from [%s] to [%s] by [\"%s\"], but machine has no definition for [%s]";
	
	private static Consumer<String> debug;
	private static Consumer<String> error;
	
	private JMLog()
	{
		/* do nothing */
	}
	
	static void setLogger(final Consumer<String> debugLogger, final Consumer<String> errorLogger)
	{
		JMLog.debug = debugLogger;
		JMLog.error = errorLogger;
	}
	
	static void debug(final Consumer<Out> consumer)
	{
		if (debug != null)
		{
			consumer.accept((format, args) -> debug.accept(String.format(format, args)));
		}
	}
	
	static void error(final Consumer<Out> consumer)
	{
		if (error != null)
		{
			consumer.accept((format, args) -> error.accept(String.format(format, args)));
		}
	}
	
	/**
	 * for printing the log cleanly
	 */
	static String getPackagelessName(final Object object)
	{
		return object.toString().substring(object.toString().lastIndexOf(".") + 1);
	}
	
	@FunctionalInterface
	interface Out
	{
		void print(final String format, final Object... args);
	}
}