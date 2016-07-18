package kr.co.plasticcity.jmata;

import java.util.function.*;

import javax.swing.undo.*;

import kr.co.plasticcity.jmata.JMata.*;

public interface JMMachine
{
	enum MachineState
	{
		RUNNING, STOPPING, TERMINATED
	}
	
	MachineState getState();
	
	<S> void input(S signal);
	
	/**
	 * run() 메소드 호출 전까지 머신은 아무 일도 하지 않음. (게으른 실행)
	 */
	void run();
	
	/**
	 * 머신을 정지 시킴. 이 메소드가 호출 된 뒤에는 입력 신호에 대한 어떠한 반응도 하지 않음.
	 */
	void stop();
	
	/**
	 * 머신 완전 종료 후 폐기. 이 메소드가 호출 된 뒤에는 머신의 재사용이 불가능하며 getMachine()을 통해 얻을수도 없음.
	 */
	void terminate();
}