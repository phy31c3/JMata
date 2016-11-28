package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	private Object machineTag;
	private Class<?> stateTag;
	
	private JMSupplier<Object> enter;
	private JMVoidConsumer exit;
	
	private Map<Class<?>, JMFunction<? super Object, Object>> enterSignalC;
	private Map<Enum<?>, JMFunction<Enum<?>, Object>> enterSignalE;
	private Map<String, JMFunction<String, Object>> enterSignalS;
	
	private Map<Class<?>, JMConsumer<? super Object>> exitSignalC;
	private Map<Enum<?>, JMConsumer<Enum<?>>> exitSignalE;
	private Map<String, JMConsumer<String>> exitSignalS;
	
	private Map<Class<?>, Class<?>> switchRuleC;
	private Map<Enum<?>, Class<?>> switchRuleE;
	private Map<String, Class<?>> switchRuleS;
	
	public JMStateImpl(Object machineTag, Class<?> stateTag)
	{
		this.machineTag = machineTag;
		this.stateTag = stateTag;
	}
	
	@Override
	public Object runEnterFunction()
	{
		if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S> Object runEnterFunctionC(S signal)
	{
		if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			return enterSignalC.get(signal.getClass()).apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S extends Enum<S>> Object runEnterFunction(Enum<S> signal)
	{
		if (enterSignalE != null && enterSignalE.containsKey(signal))
		{
			return enterSignalE.get(signal).apply(signal);
		}
		else if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			return enterSignalC.get(signal.getClass()).apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Object runEnterFunction(String signal)
	{
		if (enterSignalS != null && enterSignalS.containsKey(signal))
		{
			return enterSignalS.get(signal).apply(signal);
		}
		else if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			return enterSignalC.get(signal.getClass()).apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S> void runExitFunctionC(S signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleC != null && switchRuleC.containsKey(signal.getClass()))
		{
			if (exitSignalC != null && exitSignalC.containsKey(signal.getClass()))
			{
				exitSignalC.get(signal.getClass()).accept(signal);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleC.get(signal.getClass()));
		}
	}
	
	@Override
	public <S extends Enum<S>> void runExitFunction(Enum<S> signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleE != null && switchRuleE.containsKey(signal))
		{
			if (exitSignalE != null && exitSignalE.containsKey(signal))
			{
				exitSignalE.get(signal).accept(signal);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleE.get(signal));
		}
		else if (switchRuleC != null && switchRuleC.containsKey(signal.getClass()))
		{
			if (exitSignalC != null && exitSignalC.containsKey(signal.getClass()))
			{
				exitSignalC.get(signal.getClass()).accept(signal);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleC.get(signal.getClass()));
		}
	}
	
	@Override
	public void runExitFunction(String signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleS != null && switchRuleS.containsKey(signal))
		{
			if (exitSignalS != null && exitSignalS.containsKey(signal))
			{
				exitSignalS.get(signal).accept(signal);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleS.get(signal));
		}
		else if (switchRuleC != null && switchRuleC.containsKey(signal.getClass()))
		{
			if (exitSignalC != null && exitSignalC.containsKey(signal.getClass()))
			{
				exitSignalC.get(signal.getClass()).accept(signal);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleC.get(signal.getClass()));
		}
	}
	
	@Override
	public void putEnterFunction(JMSupplier<Object> func)
	{
		if (enter != null)
		{
			JMLog.error("[%s] machine : Definition of default entry function redundancy in state [%s]", machineTag, stateTag.getSimpleName());
		}
		
		enter = func;
	}
	
	@Override
	public void putEnterFunction(Class<?> signal, JMFunction<? super Object, Object> func)
	{
		if (enterSignalC == null)
		{
			enterSignalC = new HashMap<>();
		}
		else if (enterSignalC.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of entry function for input [%s] redundancy in state [%s]", machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		enterSignalC.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(Enum<?> signal, JMFunction<Enum<?>, Object> func)
	{
		if (enterSignalE == null)
		{
			enterSignalE = new HashMap<>();
		}
		else if (enterSignalE.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of entry function for input [%s] redundancy in state [%s]", machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		enterSignalE.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(String signal, JMFunction<String, Object> func)
	{
		if (enterSignalS == null)
		{
			enterSignalS = new HashMap<>();
		}
		else if (enterSignalS.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of entry function for input [\"%s\"] redundancy in state [%s]", machineTag, signal, stateTag.getSimpleName());
		}
		
		enterSignalS.put(signal, func);
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
		if (exit != null)
		{
			JMLog.error("[%s] machine : Definition of default exit function redundancy in state [%s]", machineTag, stateTag.getSimpleName());
		}
		
		exit = func;
	}
	
	@Override
	public void putExitFunction(Class<?> signal, JMConsumer<? super Object> func)
	{
		if (exitSignalC == null)
		{
			exitSignalC = new HashMap<>();
		}
		else if (exitSignalC.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of exit function for input [%s] redundancy in state [%s]", machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		exitSignalC.put(signal, func);
	}
	
	@Override
	public void putExitFunction(Enum<?> signal, JMConsumer<Enum<?>> func)
	{
		if (exitSignalE == null)
		{
			exitSignalE = new HashMap<>();
		}
		else if (exitSignalE.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of exit function for input [%s] redundancy in state [%s]", machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		exitSignalE.put(signal, func);
	}
	
	@Override
	public void putExitFunction(String signal, JMConsumer<String> func)
	{
		if (exitSignalS == null)
		{
			exitSignalS = new HashMap<>();
		}
		else if (exitSignalS.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of exit function for input [\"%s\"] redundancy in state [%s]", machineTag, signal, stateTag.getSimpleName());
		}
		
		exitSignalS.put(signal, func);
	}
	
	@Override
	public void putSwitchRule(Class<?> signal, Class<?> stateTag)
	{
		if (switchRuleC == null)
		{
			switchRuleC = new HashMap<>();
		}
		else if (switchRuleC.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of switch rule for input [%s] redundancy in state [%s]", machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		switchRuleC.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(Enum<?> signal, Class<?> stateTag)
	{
		if (switchRuleE == null)
		{
			switchRuleE = new HashMap<>();
		}
		else if (switchRuleE.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of switch rule for input [%s] redundancy in state [%s]", machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		switchRuleE.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(String signal, Class<?> stateTag)
	{
		if (switchRuleS == null)
		{
			switchRuleS = new HashMap<>();
		}
		else if (switchRuleS.containsKey(signal))
		{
			JMLog.error("[%s] machine : Definition of switch rule for input [\"%s\"] redundancy in state [%s]", machineTag, signal, stateTag.getSimpleName());
		}
		
		switchRuleS.put(signal, stateTag);
	}
}