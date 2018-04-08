package kr.co.plasticcity.jmata.test;

import org.junit.Test;

import kr.co.plasticcity.jmata.JMInstantMachine;
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
		final JMInstantMachine machine = JMata.buildInstantMachine("TestMachine", builder ->
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
		machine.stop();
		machine.input("B");
		machine.run();
		machine.input("B");
		machine.stop();
		machine.input(new Terminate(machine));
		machine.run();
		machine.input(new Terminate(machine));
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
		private final JMInstantMachine machine;
		
		private Terminate(final JMInstantMachine machine)
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