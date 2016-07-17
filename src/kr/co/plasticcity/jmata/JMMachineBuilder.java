package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

public interface JMMachineBuilder
{
	StateBuilder defineState(Class<?> state);
	
	GroupBuilder defineGroup(Class<?> group);
	
	JMMachineBuilder ifPresentThenReplace(JMVoidConsumer funcForLog);
	
	JMMachineBuilder ifPresentThenModify(JMVoidConsumer funcForLog);
	
	void build();
	
	void buildAndRun();
	
	// TODO think - 동일한 머신을 여러개 fork 시키고 싶을 때 어떤 인터페이스로 할 지.
	// TODO think - State lambda에 머신 파라미터를 추가할 건지.
	// TODO think - 동적 상태 추가 및 수정 허용 할 건지.
	
	public interface StateBuilder
	{
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		WhenEnter<?> whenEnterFrom(Class<?>... signals);
		
		<S> SwitchTo<S, StateBuilder> whenInput(Class<S> signal);
		
		SwitchTo<?, StateBuilder> whenInput(Class<?>... signals);
		
		JMMachineBuilder apply();
	}
	
	public interface GroupBuilder
	{
		GroupBuilder putStates(Class<?>... states);
		
		<S> SwitchTo<S, GroupBuilder> whenInput(Class<S> signal);
		
		JMMachineBuilder apply();
	}
	
	public interface WhenEnter<S>
	{
		StateBuilder doThis(Consumer<S> workOnEnter);
		
		StateBuilder doNothing();
	}
	
	public interface SwitchTo<S, T>
	{
		StateBuilder justSwitchTo(Class<?> state);
		
		WhenExit<S, T> switchTo(Class<?> state);
	}
	
	public interface WhenExit<S, T>
	{
		T AndDo(Consumer<S> workOnExit);
		
		T AndDoNothing();
	}
}