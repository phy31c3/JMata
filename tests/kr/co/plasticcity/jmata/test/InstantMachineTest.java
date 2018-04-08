package kr.co.plasticcity.jmata.test;

import org.junit.Test;

import kr.co.plasticcity.jmata.JMMachine;
import kr.co.plasticcity.jmata.JMata;
import kr.co.plasticcity.jmata.annotation.Enter;
import kr.co.plasticcity.jmata.annotation.Signal;
import kr.co.plasticcity.jmata.annotation.State;

/**
 * Created by JongsunYu on 2017-04-02.
 */
public class InstantMachineTest
{
	/*#########################################
	 * Test Main
	 #########################################*/
	@Test
	public void testMain()
	{
		JMata.initialize(System.out::println, System.err::println);
		final JMMachine machine = JMata.buildInstantMachine(builder ->
		{
			builder.defineStartState(A.class)
			       .whenEnter(A::enter)
			       .whenInput("B").switchTo(B.class)
			       .apply()
			
			       .defineState(B.class)
			       .whenEnter(B::enter)
			       .whenInput(Terminate.class).switchTo(C.class)
			       .apply()
			
			       .defineState(C.class)
			       .whenEnterBy(Terminate.class).doThis(C::enter)
			       .apply()
			
			       .onTerminate(() -> System.out.println("- 머신 종료 -"))
			
			       .build();
		});
		
		machine.input("B");
		machine.pause();
		machine.input("B");
		machine.input(new Terminate(machine));
		machine.run();
		System.out.println("- 테스트 끝 -");
		
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Signal
	private static class Terminate
	{
		private final JMMachine machine;
		
		private Terminate(final JMMachine machine)
		{
			this.machine = machine;
		}
	}
	
	@State
	private static class A
	{
		@Enter
		private static void enter()
		{
			System.out.println("enter A");
		}
	}
	
	@State
	private static class B
	{
		@Enter
		private static void enter()
		{
			System.out.println("enter B");
		}
	}
	
	@State
	private static class C
	{
		@Enter
		private static void enter(final Terminate s)
		{
			System.out.println("enter C");
			s.machine.terminate();
		}
	}
}