package test;

import kr.co.plasticcity.jmata.*;
import test.MyMachine.Group.*;
import test.MyMachine.Signal.*;
import test.MyMachine.State.*;

public class MyMachine
{
	public MyMachine()
	{
		JMata.initialize();
		JMata.buildMachine(MyMachine.class)
			
			.defineState(A.class)
			.whenEnter(A::enter)
			.whenEnterFrom(S5.class).doThis(A::enter)
			.whenInput(S0.class).justSwitchTo(B.class)
			.whenInput(S1.class).justSwitchTo(B.class)
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
			.whenInput(S3.class).switchTo(D.class).AndDo(C::exit)
			.whenInput(S4.class).switchTo(D.class).AndDo(C::exit)
			.whenExit(C::exit)
			.apply()
			
			.defineState(D.class)
			.apply()
			
			.defineGroup(G0.class)
			.putStates(B.class, C.class, D.class)
			.apply()
			
			.commit()
			.ifReplaced(() -> System.out.println(MyMachine.class.getSimpleName() + " 수정 됨"));
	}
	
	/******************************************
	 * ↓ States
	 ******************************************/
	
	public interface State
	{
		public static class A
		{
			public static void enter()
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
			
			public static void exit()
			{
			}
			
			public static void exit(S3 signal)
			{
			}
			
			public static void exit(S4 signal)
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