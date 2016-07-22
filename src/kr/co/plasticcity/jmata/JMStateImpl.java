package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	private Class<?> tag;
	
	private JMVoidConsumer enter;
	private Consumer<Integer> enterIdx;
	private Map<Class<?>, Consumer<?>> enterSig;
	private Map<Class<?>, BiConsumer<?, Integer>> enterSigIdx;
	
	private JMVoidConsumer exit;
	private Consumer<Integer> exitIdx;
	private Map<Class<?>, Consumer<?>> exitSig;
	private Map<Class<?>, BiConsumer<?, Integer>> exitSigIdx;
	
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
	public void putEnterFunction(JMVoidConsumer func)
	{
		if (enter != null)
		{
			JMLog.out("State '%s'의 enter() 함수 중복 정의", tag.getSimpleName());
		}
		
		enter = func;
	}
	
	@Override
	public void putEnterFunction(Consumer<Integer> func)
	{
		if (enterIdx != null)
		{
			JMLog.out("State '%s'의 enter(idx) 함수 중복 정의", tag.getSimpleName());
		}
		
		enterIdx = func;
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, Consumer<S> func)
	{
		if (enterSig.containsKey(signal))
		{
			JMLog.out("State '%s'의 enter(signal) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		enterSig.put(signal, func);
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, BiConsumer<S, Integer> func)
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
	public void putExitFunction(Consumer<Integer> func)
	{
		if (exitIdx != null)
		{
			JMLog.out("State '%s'의 exit(idx) 함수 중복 정의", tag.getSimpleName());
		}
		
		exitIdx = func;
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, Consumer<S> func)
	{
		if (exitSig.containsKey(signal))
		{
			JMLog.out("State '%s'의 exit(signal) 함수 중복 정의 : signal = '%s'", tag.getSimpleName(), signal.getSimpleName());
		}
		
		exitSig.put(signal, func);
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, BiConsumer<S, Integer> func)
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