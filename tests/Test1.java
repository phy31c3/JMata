import org.junit.Test;

import kr.co.plasticcity.jmata.JMata;
import kr.co.plasticcity.jmata.annotation.Enter;
import kr.co.plasticcity.jmata.annotation.Exit;
import kr.co.plasticcity.jmata.annotation.Signal;
import kr.co.plasticcity.jmata.annotation.State;

/**
 * Created by JongsunYu on 2017-04-02.
 */
public class Test1
{
	public static volatile boolean isFinish = false;
	
	/*#########################################
	 * Test Main
	 #########################################*/
	@Test
	public void testMain()
	{
		JMata.initialize(log -> System.out.println(log));
		new TestMachine();
		while (!isFinish)
		{
			JMata.input(TestMachine.class, getRandomInput());
			try
			{
				Thread.sleep((long)(Math.random() * 100));
			}
			catch (InterruptedException e)
			{
				isFinish = true;
			}
		}
		System.out.println("- 테스트 끝 -");
	}
	
	private Object getRandomInput()
	{
		int rand = (int)(Math.random() * 7);
		switch (rand)
		{
		case 0: return "string";
		case 1: return "any string";
		case 2: return Enum.ENUM;
		case 3: return Enum.ELSE;
		case 4: return AnotherEnum.ENUM;
		case 5: return new Class();
		case 6:
		default: return null;
		}
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
				builder.ifPresentThenReplaceWithThis(definer ->
				{
					definer.defineStartState(Start.class)
					       .whenEnter(Start::enter)
					       .whenInput("string").doThis(Start::exit).switchTo(A.class)
					       .apply()
					
					       .defineState(A.class)
					       .whenInput(Enum.ENUM).doThis(A::exitBy).switchTo(B.class)
					       .whenInput(Enum.class, AnotherEnum.class).switchTo(C.class)
					       .whenExit(A::exit)
					       .apply()
					
					       .defineState(B.class)
					       .whenEnter(B::enter)
					       .whenInput("string").switchTo(A.class)
					       .whenInput(Percent10.class).doThis(B::exitBy).switchTo(Finish.class)
					       .whenInput(String.class).doThis(B::exitBy).switchTo(C.class)
					       /* 의도적인 머신 정의 오류 */
					       //					       .whenInput(Enum.ENUM).doThis(B::exitBy).switchTo(Unknown.class)
					       .whenExit(B::exit)
					       .apply()
					
					       .defineState(C.class)
					       .whenEnter(C::enter)
					       .whenEnterBy(Percent25.class).doThis(C::enterBy)
					       .whenInput("5percent").doThis(C::exitBy).switchTo(Finish.class)
					       .whenInput(Percent25.class).switchToSelf()
					       .whenInput(Class.class).switchTo(A.class)
					       .apply()
					
					       .defineState(Finish.class)
					       .whenEnter(Finish::enter)
					       .whenExit(Finish::exit)
					       .apply()
					
					       .defineTerminateWork(Test2::onTerminate)
					
					       .buildAndRun();
				});
			});
		}
	}
	
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
					isFinish = true;
				}
			}).start();
		});
	}
	
	/*#########################################
	 * Signals
	 #########################################*/
	@Signal
	public enum Enum
	{
		ENUM, ELSE
	}
	
	@Signal
	public enum AnotherEnum
	{
		ENUM
	}
	
	@Signal
	public static class Class
	{
	
	}
	
	@Signal
	public static class Percent10
	{
	
	}
	
	@Signal
	public static class Percent25
	{
	
	}
	
	/*#########################################
	 * States
	 #########################################*/
	@State
	public static class Start
	{
		@Enter
		public static String enter()
		{
			return "string";
		}
		
		@Exit
		public static void exit(String s)
		{
			System.out.printf("Start 상태는 \"%s\"로 물러난다...\n", s);
		}
	}
	
	@State
	public static class A
	{
		@Exit
		public static void exitBy()
		{
			System.out.println("Enum 전이 퇴장 동작인데 파라미터가 없음");
		}
		
		@Exit
		public static void exit()
		{
			System.out.println("난 A의 기본 퇴장 동작");
		}
	}
	
	@State
	public static class B
	{
		@Enter
		public static Percent10 enter()
		{
			if (Math.random() < 0.1)
			{
				return new Percent10();
			}
			else
			{
				return null;
			}
		}
		
		@Exit
		public static void exitBy(String s)
		{
			System.out.println("B가 이상한 String으로 전이하면 이게 불려지겠지");
		}
		
		@Exit
		public static void exitBy(Percent10 s)
		{
			System.out.println("10프로에 걸렸다... Finish로 간다");
		}
		
		@Exit
		public static void exitBy(Enum s)
		{
			System.out.println("의도적으로 잘못 정의한 루틴을 탔다. 과연 스무스하게 넘어가는가?");
		}
		
		@Exit
		public static void exit()
		{
			System.out.println("이건 B의 기본 퇴장 동작인데 \"string\"으로 전이하면 불려져야 됨");
		}
	}
	
	@State
	public static class C
	{
		@Enter
		public static Object enter()
		{
			if (Math.random() < 0.05)
			{
				return "5percent";
			}
			else if (Math.random() < 0.3)
			{
				return new Percent25();
			}
			else
			{
				return new Class();
			}
		}
		
		@Enter
		public static void enterBy(Percent25 s)
		{
			System.out.println("내 자신으로 전이된거면 인간적으로 랜덤 돌리지 말자고..");
		}
		
		@Exit
		public static void exitBy()
		{
			System.out.println("5%에 걸렸다... Finish로 간다");
		}
	}
	
	@State
	public static class Finish
	{
		@Enter
		public static void enter()
		{
			System.out.println("끝낸다...");
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
						isFinish = true;
					}
				}).start();
			});
		}
		
		@Exit
		public static void exit()
		{
			System.out.println("Finish 상태의 exit()가 호출 됐다");
			JMata.terminateMachine(TestMachine.class);
		}
	}
	
	@State
	public static class Unknown
	{
		/* empty */
	}
}