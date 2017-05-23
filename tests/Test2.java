import org.junit.Test;

import kr.co.plasticcity.jmata.JMata;
import kr.co.plasticcity.jmata.annotation.EnterFunc;
import kr.co.plasticcity.jmata.annotation.Signal;
import kr.co.plasticcity.jmata.annotation.State;

/**
 * Created by JongsunYu on 2017-04-02.
 */
public class Test2
{
	static volatile boolean isFinish = false;
	
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
			JMata.input(TestMachine.class, getNoiseInput());
			try
			{
				Thread.sleep((long)(Math.random() * 900) + 100);
			}
			catch (InterruptedException e)
			{
				isFinish = true;
			}
		}
		System.out.println("- 테스트 끝 -");
	}
	
	private Object getNoiseInput()
	{
		int rand = (int)(Math.random() * 2);
		switch (rand)
		{
		case 0: return new Noise();
		default: return null;
		}
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
				builder.ifPresentThenReplaceWithThis(definer ->
				{
					definer.defineStartState(Start.class)
					       .whenEnter(Start::enter)
					       .whenInput("enum test").doThis(this::print).switchTo(EnumTest.class)
					       .apply()
					
					       .defineState(EnumTest.class)
					       .whenEnterBy("enum test").doThis(EnumTest::enter)
					       .whenInput(EnumSignal.SIGNAL).doThis(EnumTest::enumSignal).switchToSelf()
					       .whenInput(EnumSignal.class).doThis(EnumTest::enumClassSignal).switchToSelf()
					       .whenInput("enum test finished").doThis(this::print).switchTo(StringTest.class)
					       .apply()
					
					       .defineState(StringTest.class)
					       .whenEnterBy("enum test finished").doThis(StringTest::enter)
					       .whenInput("string").doThis(StringTest::stringSignal).switchToSelf()
					       .whenInput(String.class).doThis(StringTest::stringClassSignal).switchToSelf()
					       .whenInput("string test finished").doThis(this::print).switchTo(CommonEnterTest.class)
					       .apply()
					
					       .defineState(CommonEnterTest.class)
					       .apply()
					
					       .defineState(VoidEnterTest.class)
					       .apply()
					
					       .defineState(CommonExitTest.class)
					       .apply()
					
					       .defineState(VoidExitTest.class)
					       .apply()
					
					       .defineState(SignalsInputTest.class)
					       .apply()
					
					       .defineState(RapidSwitchTest.class)
					       .apply()
					
					       .defineState(DoNothingTest.class)
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
		
		private void print(String s)
		{
			System.out.println("#################### " + s);
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
	enum EnumSignal
	{
		SIGNAL,
		UNKNOWN,
		ENUMS,
	}
	
	@Signal
	enum AnotherEnumSignal
	{
		ANOTHER_ENUM_SIGNAL,
	}
	
	@Signal
	public static class ClassSignal
	{
	
	}
	
	@Signal
	public static class Classes
	{
	
	}
	
	@Signal
	public static class Noise
	{
	
	}
	
	/*#########################################
	 * States
	 #########################################*/
	@State
	public static class Start
	{
		static void enter()
		{
			input("enum test");
		}
	}
	
	@State
	public static class EnumTest
	{
		static void enter()
		{
			input(EnumSignal.SIGNAL);
		}
		
		static void enumSignal(EnumSignal s)
		{
			System.out.println("1. enum 신호 확인");
			input(EnumSignal.UNKNOWN);
		}
		
		static void enumClassSignal(EnumSignal s)
		{
			System.out.println("2. enum 클래스형 신호 확인");
			input("enum test finished");
		}
	}
	
	@State
	public static class StringTest
	{
		static void enter()
		{
			input("string");
		}
		
		static void stringSignal(String s)
		{
			System.out.println("3. string 신호 확인");
			input("unknown string");
		}
		
		static void stringClassSignal(String s)
		{
			System.out.println("4. string 클래스형 신호 확인");
			input("string test finished");
		}
	}
	
	@State
	public static class CommonEnterTest
	{
		static void enter()
		{
			System.out.println("5. 기본 진입동작 확인");
			input("string");
		}
		
		static void enterString(String s)
		{
			System.out.println("6. string 신호 일반 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void enterEnum(EnumSignal s)
		{
			System.out.println("7. enum 신호 일반 진입동작 확인");
			input(new ClassSignal());
		}
		
		static void enterClass(ClassSignal s)
		{
			System.out.println("8. class 신호 일반 진입동작 확인");
			input("common enter test finished");
		}
	}
	
	@State
	public static class VoidEnterTest
	{
		static void enter()
		{
			input("string");
		}
		
		static void enterString()
		{
			System.out.println("9. string 신호 void 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void enterEnum()
		{
			System.out.println("10. enum 신호 void 진입동작 확인");
			input("void enter test finished");
		}
	}
	
	@State
	public static class CommonExitTest
	{
		static void enter()
		{
			input("string");
		}
		
		static void exitString(String s)
		{
			System.out.println("11. string 신호 일반 퇴장동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void exitEnum(EnumSignal s)
		{
			System.out.println("12. enum 신호 일반 퇴장동작 확인");
			input(new ClassSignal());
		}
		
		static void exitClass(ClassSignal s)
		{
			System.out.println("13. class 신호 일반 퇴장동작 확인");
			input("common exit test finished");
		}
		
		static void exit()
		{
			System.out.println("14. 기본 퇴장동작 확인");
		}
	}
	
	@State
	public static class VoidExitTest
	{
		static void enter()
		{
			input("string");
		}
		
		static void exitString()
		{
			System.out.println("15. string 신호 void 퇴장동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void exitEnum()
		{
			System.out.println("16. enum 신호 void 퇴장동작 확인");
			input("void exit test finished");
		}
	}
	
	@State
	public static class SignalsInputTest
	{
		static void enter()
		{
			input(new Classes());
		}
		
		static void exitClasses()
		{
		
		}
		
		static void enterClasses(Classes s)
		{
			System.out.println("17. 다중 class 입력 정의의 전이 및 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void exitEnums(EnumSignal s)
		{
			System.out.println("18. 다중 enum 입력 정의의 퇴장동작 확인");
		}
		
		static void enterEnums(EnumSignal s)
		{
			System.out.println("19. 다중 enum 입력 정의의 전이 및 진입동작 확인");
			input("strings");
		}
		
		static void exitStrings(String s)
		{
			System.out.println("20. 다중 string 입력 정의의 퇴장동작 확인");
		}
		
		static void enterStrings(String s)
		{
			System.out.println("21. 다중 string 입력 정의의 전이 및 진입동작 확인");
			input("signals input test finished");
		}
	}
	
	@State
	public static class RapidSwitchTest
	{
	
	}
	
	@State
	public static class DoNothingTest
	{
	
	}
	
	@State
	public static class Finish
	{
		@EnterFunc
		static void enter()
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
		
		@EnterFunc
		static void exit()
		{
			System.out.println("Finish 상태의 exit()가 호출 됐다");
			JMata.terminateMachine(TestMachine.class);
		}
	}
}