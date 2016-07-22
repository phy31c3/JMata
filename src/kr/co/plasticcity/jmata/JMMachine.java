package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import javax.swing.undo.*;

import kr.co.plasticcity.jmata.JMata.*;

interface JMMachine
{
	static JMMachine getNew(int numInstances, Map<Class<?>, JMStateCreater> stateMap)
	{
		return new JMMachineImpl(numInstances, stateMap);
	}
	
	enum MachineState
	{
		RUNNING, STOPPING, TERMINATED
	}
	
	MachineState getState(int idx);
	
	/**
	 * run() 메소드 호출 전까지 머신은 아무 일도 하지 않음. (게으른 실행)
	 */
	void run(int idx);
	
	/**
	 * 머신을 정지 시킴. 이 메소드가 호출 된 뒤에는 입력 신호에 대한 어떠한 반응도 하지 않음.
	 */
	void stop(int idx);
	
	/**
	 * 머신 완전 종료 후 폐기. 이 메소드가 호출 된 뒤에는 머신의 재사용이 불가능하며 머신 풀에서 완전히 삭제 됨.
	 */
	void terminate(int idx);
	
	<S> void input(int idx, S signal);
}