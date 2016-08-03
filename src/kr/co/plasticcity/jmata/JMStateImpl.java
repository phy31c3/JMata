package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	private Class<?> tag;
	
	private JMVoidConsumer enter;
	private JMConsumer<Integer> enterIdx;
	private Map<Class<?>, JMConsumer<?>> enterSignalC;
	private Map<Enum<?>, JMConsumer<?>> enterSignalE;
	private Map<String, JMConsumer<?>> enterSignalS;
	private Map<Class<?>, JMBiConsumer<?, Integer>> enterSignalCIdx;
	private Map<Enum<?>, JMBiConsumer<?, Integer>> enterSignalEIdx;
	private Map<String, JMBiConsumer<?, Integer>> enterSignalSIdx;
	
	private JMVoidConsumer exit;
	private JMConsumer<Integer> exitIdx;
	private Map<Class<?>, JMConsumer<?>> exitSignalC;
	private Map<Enum<?>, JMConsumer<?>> exitSignalE;
	private Map<String, JMConsumer<?>> exitSignalS;
	private Map<Class<?>, JMBiConsumer<?, Integer>> exitSignalCIdx;
	private Map<Enum<?>, JMBiConsumer<?, Integer>> exitSignalEIdx;
	private Map<String, JMBiConsumer<?, Integer>> exitSignalSIdx;
	
	private Map<Class<?>, Class<?>> switchRuleC;
	private Map<Enum<?>, Class<?>> switchRuleE;
	private Map<String, Class<?>> switchRuleS;
	
	public JMStateImpl(Class<?> tag)
	{
		this.tag = tag;
	}
	
	@Override
	public void runEnterFunction(int machineIdx)
	{
		if (enterIdx != null)
		{
			enterIdx.accept(machineIdx);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <S> void runEnterFunction(int machineIdx, S signal)
	{
		if (enterSignalCIdx != null && enterSignalCIdx.containsKey(signal.getClass()))
		{
			((JMBiConsumer<S, Integer>)enterSignalCIdx.get(signal.getClass())).accept(signal, machineIdx);
		}
		else if (enterSignalC != null && enterSignalC.containsKey(signal.getClass()))
		{
			((JMConsumer<S>)enterSignalC.get(signal.getClass())).accept(signal);
		}
		else if (enterIdx != null)
		{
			enterIdx.accept(machineIdx);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <S extends Enum<S>> void runEnterFunction(int machineIdx, Enum<S> signal)
	{
		if (enterSignalEIdx != null && enterSignalEIdx.containsKey(signal))
		{
			((JMBiConsumer<Enum<S>, Integer>)enterSignalEIdx.get(signal)).accept(signal, machineIdx);
		}
		else if (enterSignalE != null && enterSignalE.containsKey(signal))
		{
			((JMConsumer<Enum<S>>)enterSignalE.get(signal)).accept(signal);
		}
		else if (enterIdx != null)
		{
			enterIdx.accept(machineIdx);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void runEnterFunction(int machineIdx, String signal)
	{
		if (enterSignalSIdx != null && enterSignalSIdx.containsKey(signal))
		{
			((JMBiConsumer<String, Integer>)enterSignalSIdx.get(signal)).accept(signal, machineIdx);
		}
		else if (enterSignalS != null && enterSignalS.containsKey(signal))
		{
			((JMConsumer<String>)enterSignalS.get(signal)).accept(signal);
		}
		else if (enterIdx != null)
		{
			enterIdx.accept(machineIdx);
		}
		else if (enter != null)
		{
			enter.accept();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <S> void runExitFunction(int machineIdx, S signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleC != null && switchRuleC.containsKey(signal.getClass()))
		{
			if (exitSignalCIdx != null && exitSignalCIdx.containsKey(signal.getClass()))
			{
				((JMBiConsumer<S, Integer>)exitSignalCIdx.get(signal.getClass())).accept(signal, machineIdx);
			}
			else if (exitSignalC != null && exitSignalC.containsKey(signal.getClass()))
			{
				((JMConsumer<S>)exitSignalC.get(signal.getClass())).accept(signal);
			}
			else if (exitIdx != null)
			{
				exitIdx.accept(machineIdx);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleC.get(signal.getClass()));
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <S extends Enum<S>> void runExitFunction(int machineIdx, Enum<S> signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleE != null && switchRuleE.containsKey(signal))
		{
			if (exitSignalEIdx != null && exitSignalEIdx.containsKey(signal))
			{
				((JMBiConsumer<Enum<?>, Integer>)exitSignalEIdx.get(signal)).accept(signal, machineIdx);
			}
			else if (exitSignalE != null && exitSignalE.containsKey(signal))
			{
				((JMConsumer<Enum<?>>)exitSignalE.get(signal)).accept(signal);
			}
			else if (exitIdx != null)
			{
				exitIdx.accept(machineIdx);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleE.get(signal));
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void runExitFunction(int machineIdx, String signal, JMConsumer<Class<?>> nextState)
	{
		if (switchRuleS != null && switchRuleS.containsKey(signal))
		{
			if (exitSignalSIdx != null && exitSignalSIdx.containsKey(signal))
			{
				((JMBiConsumer<String, Integer>)exitSignalSIdx.get(signal)).accept(signal, machineIdx);
			}
			else if (exitSignalS != null && exitSignalS.containsKey(signal))
			{
				((JMConsumer<String>)exitSignalS.get(signal)).accept(signal);
			}
			else if (exitIdx != null)
			{
				exitIdx.accept(machineIdx);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRuleS.get(signal));
		}
	}
	
	@Override
	public void putEnterFunction(JMVoidConsumer func)
	{
		if (enter != null || enterIdx != null)
		{
			JMLog.out("State '%s'의 default enter 함수 중복 정의", tag.getSimpleName());
		}
		
		enter = func;
		enterIdx = null;
	}
	
	@Override
	public void putEnterFunction(JMConsumer<Integer> func)
	{
		if (enter != null || enterIdx != null)
		{
			JMLog.out("State '%s'의 default enter 함수 중복 정의", tag.getSimpleName());
		}
		
		enter = null;
		enterIdx = func;
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, JMConsumer<S> func)
	{
		if (enterSignalC == null)
		{
			enterSignalC = new HashMap<>();
		}
		else if (enterSignalC.containsKey(signal) || (enterSignalCIdx != null && enterSignalCIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
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
		else if (enterSignalE.containsKey(signal) || (enterSignalEIdx != null && enterSignalEIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter  함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
		else if (enterSignalS.containsKey(signal) || (enterSignalSIdx != null && enterSignalSIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		enterSignalS.put(signal, func);
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, JMBiConsumer<S, Integer> func)
	{
		if (enterSignalCIdx == null)
		{
			enterSignalCIdx = new HashMap<>();
		}
		else if (enterSignalCIdx.containsKey(signal) || (enterSignalC != null && enterSignalC.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		enterSignalCIdx.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(Enum<?> signal, JMBiConsumer<Enum<?>, Integer> func)
	{
		if (enterSignalEIdx == null)
		{
			enterSignalEIdx = new HashMap<>();
		}
		else if (enterSignalEIdx.containsKey(signal) || (enterSignalE != null && enterSignalE.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		enterSignalEIdx.put(signal, func);
	}
	
	@Override
	public void putEnterFunction(String signal, JMBiConsumer<String, Integer> func)
	{
		if (enterSignalSIdx == null)
		{
			enterSignalSIdx = new HashMap<>();
		}
		else if (enterSignalSIdx.containsKey(signal) || (enterSignalS != null && enterSignalS.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal enter 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		enterSignalSIdx.put(signal, func);
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
		if (exit != null || exitIdx != null)
		{
			JMLog.out("State '%s'의 default exit 함수 중복 정의", tag.getSimpleName());
		}
		
		exit = func;
		exitIdx = null;
	}
	
	@Override
	public void putExitFunction(JMConsumer<Integer> func)
	{
		if (exit != null || exitIdx != null)
		{
			JMLog.out("State '%s'의 default exit 함수 중복 정의", tag.getSimpleName());
		}
		
		exit = null;
		exitIdx = func;
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, JMConsumer<S> func)
	{
		if (exitSignalC == null)
		{
			exitSignalC = new HashMap<>();
		}
		else if (exitSignalC.containsKey(signal) || (exitSignalCIdx != null && exitSignalCIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
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
		else if (exitSignalE.containsKey(signal) || (exitSignalEIdx != null && exitSignalEIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
		else if (exitSignalS.containsKey(signal) || (exitSignalSIdx != null && exitSignalSIdx.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		exitSignalS.put(signal, func);
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, JMBiConsumer<S, Integer> func)
	{
		if (exitSignalCIdx == null)
		{
			exitSignalCIdx = new HashMap<>();
		}
		else if (exitSignalCIdx.containsKey(signal) || (exitSignalC != null && exitSignalC.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		exitSignalCIdx.put(signal, func);
	}
	
	@Override
	public void putExitFunction(Enum<?> signal, JMBiConsumer<Enum<?>, Integer> func)
	{
		if (exitSignalEIdx == null)
		{
			exitSignalEIdx = new HashMap<>();
		}
		else if (exitSignalEIdx.containsKey(signal) || (exitSignalE != null && exitSignalE.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		exitSignalEIdx.put(signal, func);
	}
	
	@Override
	public void putExitFunction(String signal, JMBiConsumer<String, Integer> func)
	{
		if (exitSignalSIdx == null)
		{
			exitSignalSIdx = new HashMap<>();
		}
		else if (exitSignalSIdx.containsKey(signal) || (exitSignalS != null && exitSignalS.remove(signal) != null))
		{
			JMLog.out("State '%s'의 signal exit 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		exitSignalSIdx.put(signal, func);
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
			JMLog.out("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		switchRuleC.put(signal, stateTag);
	}
	
	@Override
	public <S extends Enum<S>> void putSwitchRule(Enum<S> signal, Class<?> stateTag)
	{
		if (switchRuleE == null)
		{
			switchRuleE = new HashMap<>();
		}
		else if (switchRuleE.containsKey(signal))
		{
			JMLog.out("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
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
			JMLog.out("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal);
		}
		
		switchRuleS.put(signal, stateTag);
	}
}