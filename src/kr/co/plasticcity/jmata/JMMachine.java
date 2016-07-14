package kr.co.plasticcity.jmata;

import java.util.function.*;

import javax.swing.undo.*;

import kr.co.plasticcity.jmata.JMata.*;

public interface JMMachine
{
	/************************** ↓ Basic Control **************************/
	
	/**
	 * run() 메소드 호출 전까지 머신은 아무 일도 하지 않음. (게으른 실행)
	 */
	void run();
	
	/**
	 * 머신을 정지 시킴. 이 메소드가 호출 된 뒤에는 입력 신호에 대한 어떠한 반응도 하지 않음.
	 */
	void stop();
	
	/**
	 * 머신 완전 종료. 이 메소드가 호출 된 뒤에는 머신 조작 불가능.
	 */
	void terminate();
	
	<S> void input(S signal);
	
	/************************** ↑ Basic Control **************************/
	
	/************************** ↓ State-related **************************/
	
	/**
	 * 머신이 running 상태일 때 호출하면 완전히 무시됨. (주의)
	 */
	<T> void defineState(Class<T> state, Consumer<StateDefiner> func);
	
	public interface StateDefiner
	{
		<S> EnterWork<S> whenFrom(Class<S> signal);
		
		<S> SwitchTo<S> whenInput(Class<S> signal);
		
		JMMachine commit();
	}
	
	public interface EnterWork<S>
	{
		StateDefiner doThat(Consumer<S> func);
		
		StateDefiner doNothing();
	}
	
	public interface SwitchTo<S>
	{
		<T> ExitWork<S> switchTo(Class<T> state);
	}
	
	public interface ExitWork<S>
	{
		StateDefiner AndDo(Consumer<S> func);
		
		StateDefiner AndDoNothing();
	}
	
	/************************** ↑ State-related **************************/
}