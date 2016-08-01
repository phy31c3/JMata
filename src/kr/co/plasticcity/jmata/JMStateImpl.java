package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	private Class<?> tag;
	
	private JMVoidConsumer enter;
	private JMConsumer<Integer> enterIdx;
	private Map<Class<?>, JMConsumer<?>> enterSig;
	private Map<Class<?>, JMBiConsumer<?, Integer>> enterSigIdx;
	
	private JMVoidConsumer exit;
	private JMConsumer<Integer> exitIdx;
	private Map<Class<?>, JMConsumer<?>> exitSig;
	private Map<Class<?>, JMBiConsumer<?, Integer>> exitSigIdx;
	
	private Map<Class<?>, Class<?>> switchRule;
	
	public JMStateImpl(Class<?> tag)
	{
		this.tag = tag;
		
		this.enter = null;
		this.enterIdx = null;
		this.enterSig = new HashMap<>();
		this.enterSigIdx = new HashMap<>();
		
		this.exit = null;
		this.exitIdx = null;
		this.exitSig = new HashMap<>();
		this.exitSigIdx = new HashMap<>();
		
		this.switchRule = new HashMap<>();
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
		if (enterSigIdx.containsKey(signal.getClass()))
		{
			((JMBiConsumer<S, Integer>)enterSigIdx.get(signal.getClass())).accept(signal, machineIdx);
		}
		else if (enterSig.containsKey(signal.getClass()))
		{
			((JMConsumer<S>)enterSig.get(signal.getClass())).accept(signal);
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
		if (switchRule.containsKey(signal.getClass()))
		{
			if (exitSigIdx.containsKey(signal.getClass()))
			{
				((JMBiConsumer<S, Integer>)exitSigIdx.get(signal.getClass())).accept(signal, machineIdx);
			}
			else if (exitSig.containsKey(signal.getClass()))
			{
				((JMConsumer<S>)exitSig.get(signal.getClass())).accept(signal);
			}
			else if (exitIdx != null)
			{
				exitIdx.accept(machineIdx);
			}
			else if (exit != null)
			{
				exit.accept();
			}
			
			nextState.accept(switchRule.get(signal.getClass()));
		}
	}
	
	@Override
	public void putEnterFunction(JMVoidConsumer func)
	{
		if (enter != null)
		{
			JMLog.out("State '%s'의 enter() 함수 중복 정의", tag.getSimpleName());
		}
		
		enter = func;
	}
	
	@Override
	public void putEnterFunction(JMConsumer<Integer> func)
	{
		if (enterIdx != null)
		{
			JMLog.out("State '%s'의 enter(idx) 함수 중복 정의", tag.getSimpleName());
		}
		
		enterIdx = func;
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, JMConsumer<S> func)
	{
		if (enterSig.containsKey(signal))
		{
			JMLog.out("State '%s'의 enter(signal) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		enterSig.put(signal, func);
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, JMBiConsumer<S, Integer> func)
	{
		if (enterSigIdx.containsKey(signal))
		{
			JMLog.out("State '%s'의 enter(signal, idx) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		enterSigIdx.put(signal, func);
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
		if (exit != null)
		{
			JMLog.out("State '%s'의 exit() 함수 중복 정의", tag.getSimpleName());
		}
		
		exit = func;
	}
	
	@Override
	public void putExitFunction(JMConsumer<Integer> func)
	{
		if (exitIdx != null)
		{
			JMLog.out("State '%s'의 exit(idx) 함수 중복 정의", tag.getSimpleName());
		}
		
		exitIdx = func;
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, JMConsumer<S> func)
	{
		if (exitSig.containsKey(signal))
		{
			JMLog.out("State '%s'의 exit(signal) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		exitSig.put(signal, func);
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, JMBiConsumer<S, Integer> func)
	{
		if (exitSigIdx.containsKey(signal))
		{
			JMLog.out("State '%s'의 exit(signal, idx) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		exitSigIdx.put(signal, func);
	}
	
	@Override
	public void putSwitchRule(Class<?> signal, Class<?> stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.out("State '%s'의 switch rule 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		switchRule.put(signal, stateTag);
	}
}