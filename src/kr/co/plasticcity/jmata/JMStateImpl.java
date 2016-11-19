package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	private Class<?> tag;
	
	private JMVoidConsumer enter;
	private JMVoidConsumer exit;
	
	private Map<Class<?>, JMConsumer<? super Object>> enterSignalC;
	private Map<Enum<?>, JMConsumer<Enum<?>>> enterSignalE;
	private Map<String, JMConsumer<String>> enterSignalS;
	
	private Map<Class<?>, JMConsumer<? super Object>> exitSignalC;
	private Map<Enum<?>, JMConsumer<Enum<?>>> exitSignalE;
	private Map<String, JMConsumer<String>> exitSignalS;
	
	private Map<Class<?>, Class<?>> switchRuleC;
	private Map<Enum<?>, Class<?>> switchRuleE;
	private Map<String, Class<?>> switchRuleS;
	
	public JMStateImpl(Class<?> tag)
	{
		this.tag = tag;
	}
	
	@Override
	public void runEnterFunction()
	{
		if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	public <S> void runEnterFunctionC(S signal)
	{
		if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			enterSignalC.get(signal.getClass()).accept(signal);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	public <S extends Enum<S>> void runEnterFunction(Enum<S> signal)
	{
		if (enterSignalE != null && enterSignalE.containsKey(signal))
		{
			enterSignalE.get(signal).accept(signal);
		}
		else if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			enterSignalC.get(signal.getClass()).accept(signal);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	public void runEnterFunction(String signal)
	{
		if (enterSignalS != null && enterSignalS.containsKey(signal))
		{
			enterSignalS.get(signal).accept(signal);
		}
		else if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			enterSignalC.get(signal.getClass()).accept(signal);
		}
		else if (enter != null)
		{
			enter.accept();
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
	public void putEnterFunction(JMVoidConsumer func)
	{
		if (enter != null)
		{
			JMLog.error("State '%s'의 default enter 함수 중복 정의", tag.getSimpleName());
		}
		
		enter = func;
	}
	
	@Override
	public void putEnterFunction(Class<?> signal, JMConsumer<? super Object> func)
	{
		if (enterSignalC == null)
		{
			enterSignalC = new HashMap<>();
		}
		else if (enterSignalC.containsKey(signal))
		{
			JMLog.error("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		enterSignalC.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(Enum<?> signal, JMConsumer<Enum<?>> func)
	{
		if (enterSignalE == null)
		{
			enterSignalE = new HashMap<>();
		}
		else if (enterSignalE.containsKey(signal))
		{
			JMLog.error("State '%s'의 signal enter  함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		enterSignalE.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(String signal, JMConsumer<String> func)
	{
		if (enterSignalS == null)
		{
			enterSignalS = new HashMap<>();
		}
		else if (enterSignalS.containsKey(signal))
		{
			JMLog.error("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		enterSignalS.put(signal, func);
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
		if (exit != null)
		{
			JMLog.error("State '%s'의 default exit 함수 중복 정의", tag.getSimpleName());
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
			JMLog.error("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
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
			JMLog.error("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
			JMLog.error("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
			JMLog.error("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
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
			JMLog.error("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
			JMLog.error("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		switchRuleS.put(signal, stateTag);
	}
}