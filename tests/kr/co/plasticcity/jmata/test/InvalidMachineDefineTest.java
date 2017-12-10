package kr.co.plasticcity.jmata.test;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import kr.co.plasticcity.jmata.JMata;
import kr.co.plasticcity.jmata.annotation.Enter;
import kr.co.plasticcity.jmata.annotation.Exit;
import kr.co.plasticcity.jmata.annotation.Signal;
import kr.co.plasticcity.jmata.annotation.State;
import kr.co.plasticcity.jmata.annotation.Terminate;

/**
 * Created by JongsunYu on 2017-04-02.
 */
public class InvalidMachineDefineTest
{
	static volatile CountDownLatch latch = new CountDownLatch(1);
	
	/*#########################################
	 * Test Main
	 #########################################*/
	@Test
	public void testMain()
	{
		JMata.initialize(null, System.err::println);
		new TestMachine();
		input(new ToUndefinedState());
		input(S.TO_UNDEFINED_STATE);
		input("to undefined state");
		input(S.TO_FINISH_STATE);
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("- 테스트 끝 -");
	}
	
	private static void input(Object s)
	{
		JMata.input(TestMachine.class, s);
	}
	
	/*#########################################
	 * Machine Def
	 #########################################*/
	public class TestMachine
	{
		public TestMachine()
		{
			JMata.buildMachine(TestMachine.class, builder ->
			{
				builder.ifPresentThenIgnoreThis(definer ->
				{
					definer.defineStartState(Start.class)
					       .whenInput(ToUndefinedState.class).doThis(Start::onToUndefinedStateC).switchTo(UndefinedState.class)
					       .whenInput(S.TO_UNDEFINED_STATE).doThis(Start::onToUndefinedState).switchTo(UndefinedState.class)
					       .whenInput("to undefined state").doThis(Start::onToUndefinedState).switchTo(UndefinedState.class)
					       .whenInput(S.TO_FINISH_STATE).doThis(Start::onToFinishState).switchTo(Finish.class)
					       .apply()
					
					       .defineState(Finish.class)
					       .whenEnter(Finish::enter)
					       .apply()
					
					       .onTerminate(InvalidMachineDefineTest::onTerminate)
					
					       .buildAndRun();
				});
			});
		}
	}
	
	@Terminate
	public static void onTerminate()
	{
		System.out.println("머신이 종료 됨");
		JMata.release(() ->
		{
			System.out.println("JMata 해제 됨");
			new Thread(() ->
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					/* do nothing */
				}
				finally
				{
					latch.countDown();
				}
			}).start();
		});
	}
	
	/*#########################################
	 * Signals
	 #########################################*/
	@Signal
	private static class ToUndefinedState
	{
		/* empty */
	}
	
	@Signal
	enum S
	{
		TO_UNDEFINED_STATE,
		TO_FINISH_STATE,
	}
	
	/*#########################################
	 * States
	 #########################################*/
	@State
	public static class Start
	{
		@Exit
		static void onToUndefinedStateC(ToUndefinedState s)
		{
			System.out.println("onToUndefinedState : 호출되면 안됨");
		}
		
		@Exit
		static void onToUndefinedState()
		{
			System.out.println("onToUndefinedState : 호출되면 안됨");
		}
		
		@Exit
		static void onToFinishState()
		{
			System.out.println("onToFinishState : Finish 상태로");
		}
	}
	
	@State
	public static class UndefinedState
	{
		/* empty */
	}
	
	@State
	public static class Finish
	{
		@Enter
		static void enter()
		{
			System.out.println("Finish 상태에 들어옴");
			JMata.terminateMachine(TestMachine.class);
		}
	}
}