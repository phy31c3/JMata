package test;

import kr.co.plasticcity.jmata.*;
import test.TestMachine.Signal.*;
import test.TestMachine.State.*;

public class TestMachine
{
	public TestMachine()
	{
		JMata.initialize();
		JMata.setLogFunction(log -> System.out.println(log));
		JMata.buildMachine(TestMachine.class, builder ->
		{
			builder.ifPresentThenIgnoreThis(mbdr ->
			{
				mbdr.defineStartState(A.class)
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
					
					.build();
			});
		});
		
		JMata.runMachine(TestMachine.class);
	}
	
	public void input(String s)
	{
		System.out.println(">> input : " + s);
		switch (s)
		{
		case "0":
			JMata.inputTo(TestMachine.class, new S0());
			break;
		case "1":
			JMata.inputTo(TestMachine.class, new S1());
			break;
		case "2":
			JMata.inputTo(TestMachine.class, new S2());
			break;
		case "3":
			JMata.inputTo(TestMachine.class, new S3());
			break;
		case "4":
			JMata.inputTo(TestMachine.class, new S4());
			break;
		case "5":
			JMata.inputTo(TestMachine.class, new S5());
			break;
		default:
			System.out.println("잘못된 입력");
		}
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
				System.out.println(A.class.getSimpleName() + " : enter(" + idx + ")");
			}
			
			public static void enter(S5 signal)
			{
				System.out.println(A.class.getSimpleName() + " : enter(" + signal.getClass().getSimpleName() + ")");
			}
			
			public static void exit()
			{
				System.out.println(A.class.getSimpleName() + " : exit()");
			}
		}
		
		public static class B
		{
			public static void enter()
			{
				System.out.println(B.class.getSimpleName() + " : enter()");
			}
			
			public static void exit()
			{
				System.out.println(B.class.getSimpleName() + " : exit()");
			}
		}
		
		public static class C
		{
			public static void enter()
			{
				System.out.println(C.class.getSimpleName() + " : enter()");
			}
			
			public static void exit(S1 signal)
			{
				System.out.println(C.class.getSimpleName() + " : exit(" + signal.getClass().getSimpleName() + ")");
			}
			
			public static void exit()
			{
				System.out.println(C.class.getSimpleName() + " : exit()");
			}
		}
		
		public static class D
		{
			public static void enter()
			{
				System.out.println(D.class.getSimpleName() + " : enter()");
			}
			
			public static void enter(S2 signal)
			{
				System.out.println(D.class.getSimpleName() + " : enter(" + signal.getClass().getSimpleName() + ")");
			}
			
			public static void enter(S3 signal)
			{
				System.out.println(D.class.getSimpleName() + " : enter(" + signal.getClass().getSimpleName() + ")");
			}
			
			public static void enter(S4 signal)
			{
				System.out.println(D.class.getSimpleName() + " : enter(" + signal.getClass().getSimpleName() + ")");
			}
			
			public static void exit()
			{
				System.out.println(D.class.getSimpleName() + " : exit()");
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
}