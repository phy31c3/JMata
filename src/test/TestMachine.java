package test;

import javax.management.remote.*;

import kr.co.plasticcity.jmata.*;
import test.TestMachine.Group.*;
import test.TestMachine.Signal.*;
import test.TestMachine.State.*;

public class TestMachine
{
	public TestMachine()
	{
		JMata.initialize();
		JMata.setLogFunction(log -> System.out.println(log));
		JMata.buildMachine(TestMachine.class, builder -> {
			builder.ifPresentThenIgnoreThis(mchb -> {
				mchb.defineState(A.class)
					.whenEnter(A::enter)
					.whenEnterFrom(S5.class).doThis(A::enter)
					.whenInput(S0.class).justSwitchTo(B.class)
					.whenInput(S1.class).justSwitchTo(C.class)
					.whenExit(A::exit)
					.apply()
					
					.defineState(B.class)
					.whenEnter(B::enter)
					.whenInput(S1.class).justSwitchTo(C.class)
					.whenInput(S2.class).justSwitchTo(D.class)
					.whenExit(B::exit)
					.apply()
					
					.defineState(C.class)
					.whenEnter(C::enter)
					.whenInput(S1.class).switchTo(C.class).AndDo(C::exit)
					.whenInput(S3.class, S4.class).justSwitchTo(D.class)
					.whenExit(C::exit)
					.apply()
					
					.defineState(D.class)
					.whenEnter(D::enter)
					.whenEnterFrom(S2.class).doThis(D::enter)
					.whenEnterFrom(S3.class).doThis(D::enter)
					.whenEnterFrom(S4.class).doThis(D::enter)
					.whenExit(D::exit)
					.apply()
					
					.defineGroup(B.class, C.class, D.class)
					.whenInput(S5.class).justSwitchTo(A.class)
					.apply()
					
					.build();
			});
		});
		
		JMata.runMachine(TestMachine.class);
	}
	
	/******************************************
	 * ↓ States
	 ******************************************/
	
	public interface State
	{
		public static class A
		{
			public static void enter(int idx)
			{
			}
			
			public static void enter(S5 signal)
			{
			}
			
			public static void exit()
			{
			}
		}
		
		public static class B
		{
			public static void enter()
			{
			}
			
			public static void exit()
			{
			}
		}
		
		public static class C
		{
			public static void enter()
			{
			}
			
			public static void exit(S1 signal)
			{
			}
			
			public static void exit()
			{
			}
		}
		
		public static class D
		{
			public static void enter()
			{
			}
			
			public static void enter(S2 signal)
			{
			}
			
			public static void enter(S3 signal)
			{
			}
			
			public static void enter(S4 signal)
			{
			}
			
			public static void exit()
			{
			}
		}
	}
	
	/******************************************
	 * ↓ Signals
	 ******************************************/
	
	public interface Signal
	{
		public static class S0
		{
		}
		
		public static class S1
		{
		}
		
		public static class S2
		{
		}
		
		public static class S3
		{
		}
		
		public static class S4
		{
		}
		
		public static class S5
		{
		}
	}
	
	/******************************************
	 * ↓ Groups
	 ******************************************/
	
	public interface Group
	{
		public static class G0
		{
		}
	}
}