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
		JMata.initialize();
//		JMata.initialize(log -> System.out.println(log));
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
		case 0:
			return new Noise();
		default:
			return null;
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
				builder.ifPresentThenIgnoreThis(definer ->
				{
					definer.defineStartState(Start.class)
					       .whenEnter(Start::enter)
					       .whenInput("start").switchTo(EnumTest.class)
					       .apply()
					
					       .defineState(EnumTest.class)
					       .whenEnterBy("start").doThis(EnumTest::enter)
					       .whenInput(EnumSignal.SIGNAL).doThis(EnumTest::enumSignal).switchToSelf()
					       .whenInput(EnumSignal.class).doThis(EnumTest::enumClassSignal).switchToSelf()
					       .whenInput("enum test finished").switchTo(StringTest.class)
					       .apply()
					
					       .defineState(StringTest.class)
					       .whenEnterBy("enum test finished").doThis(StringTest::enter)
					       .whenInput("string").doThis(StringTest::stringSignal).switchToSelf()
					       .whenInput(String.class).doThis(StringTest::stringClassSignal).switchToSelf()
					       .whenInput("string test finished").switchTo(CommonEnterTest.class)
					       .apply()
					
					       .defineState(CommonEnterTest.class)
					       .whenEnterBy("string test finished").doThis(CommonEnterTest::enter)
					       .whenEnterBy("string").doThis(CommonEnterTest::enterString)
					       .whenEnterBy(EnumSignal.SIGNAL).doThis(CommonEnterTest::enterEnum)
					       .whenEnterBy(ClassSignal.class).doThis(CommonEnterTest::enterClass)
					       .whenInput("string").switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).switchToSelf()
					       .whenInput(ClassSignal.class).switchToSelf()
					       .whenInput("common enter test finished").switchTo(VoidEnterTest.class)
					       .apply()
					
					       .defineState(VoidEnterTest.class)
					       .whenEnterBy("common enter test finished").doThis(VoidEnterTest::enter)
					       .whenEnterBy("string").doThis(VoidEnterTest::enterString)
					       .whenEnterBy(EnumSignal.SIGNAL).doThis(VoidEnterTest::enterEnum)
					       .whenInput("string").switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).switchToSelf()
					       .whenInput("void enter test finished").switchTo(CommonExitTest.class)
					       .apply()
					
					       .defineState(CommonExitTest.class)
					       .whenEnterBy("void enter test finished").doThis(CommonExitTest::enter)
					       .whenInput("string").doThis(CommonExitTest::exitString).switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).doThis(CommonExitTest::exitEnum).switchToSelf()
					       .whenInput(ClassSignal.class).doThis(CommonExitTest::exitClass).switchToSelf()
					       .whenInput("common exit test finished").switchTo(VoidExitTest.class)
					       .whenExit(CommonExitTest::exit)
					       .apply()
					
					       .defineState(VoidExitTest.class)
					       .whenEnterBy("common exit test finished").doThis(VoidExitTest::enter)
					       .whenInput("string").doThis(VoidExitTest::exitString).switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).doThis(VoidExitTest::exitEnum).switchToSelf()
					       .whenInput("void exit test finished").switchTo(SignalsInputTest.class)
					       .apply()
					
					       .defineState(SignalsInputTest.class)
					       .whenEnterBy("void exit test finished").doThis(SignalsInputTest::enter)
					       .whenEnterBy(Classes.class).doThis(SignalsInputTest::enterClasses)
					       .whenEnterBy(EnumSignal.SIGNAL, EnumSignal.ENUMS).doThis(SignalsInputTest::enterEnums)
					       .whenEnterBy("string", "strings").doThis(SignalsInputTest::enterStrings)
					       .whenInput(ClassSignal.class, Classes.class).doThis(SignalsInputTest::exitClasses).switchToSelf()
					       .whenInput(EnumSignal.SIGNAL, EnumSignal.ENUMS).doThis(SignalsInputTest::exitEnums).switchToSelf()
					       .whenInput("string", "strings").doThis(SignalsInputTest::exitStrings).switchToSelf()
					       .whenInput("signals input test finished").switchTo(RapidSwitchTest.class)
					       .apply()
					
					       .defineState(RapidSwitchTest.class)
					       .whenEnterBy("signals input test finished").doThis(RapidSwitchTest::enter)
					       .whenEnterBy(ClassSignal.class).doThis(RapidSwitchTest::enterClass)
					       .whenEnterBy(EnumSignal.SIGNAL).doThis(RapidSwitchTest::enterEnum)
					       .whenEnterBy("string").doThis(RapidSwitchTest::enterString)
					       .whenInput(ClassSignal.class).switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).switchToSelf()
					       .whenInput("string").switchToSelf()
					       .whenInput("rapid switch test finished").switchTo(DoNothingTest.class)
					       .apply()
					
					       .defineState(DoNothingTest.class)
					       .whenEnter(DoNothingTest::defaultEnter)
					       .whenEnterBy("rapid switch test finished").doThis(DoNothingTest::enter)
					       .whenEnterBy(ClassSignal.class).doNothing()
					       .whenEnterBy(EnumSignal.SIGNAL).doNothing()
					       .whenEnterBy("string").doNothing()
					       .whenEnterBy("complete").doThis(DoNothingTest::enterComplete)
					       .whenInput(ClassSignal.class).doNothing().switchToSelf()
					       .whenInput(EnumSignal.SIGNAL).doNothing().switchToSelf()
					       .whenInput("string").doNothing().switchToSelf()
					       .whenInput("complete").doNothing().switchToSelf()
					       .whenInput("do nothing test finished").doNothing().switchTo(Finish.class)
					       .whenExit(DoNothingTest::defaultExit)
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
	enum EnumSignal
	{
		SIGNAL,
		UNKNOWN,
		ENUMS,
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
			input("start");
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
			System.out.println("17. 다중 class 입력 정의의 퇴장동작 확인");
		}
		
		static void enterClasses(Classes s)
		{
			System.out.println("18. 다중 class 입력 정의의 전이 및 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		static void exitEnums(EnumSignal s)
		{
			System.out.println("19. 다중 enum 입력 정의의 퇴장동작 확인");
		}
		
		static void enterEnums(EnumSignal s)
		{
			System.out.println("20. 다중 enum 입력 정의의 전이 및 진입동작 확인");
			input("strings");
		}
		
		static void exitStrings(String s)
		{
			System.out.println("21. 다중 string 입력 정의의 퇴장동작 확인");
		}
		
		static void enterStrings(String s)
		{
			System.out.println("22. 다중 string 입력 정의의 전이 및 진입동작 확인");
			input("signals input test finished");
		}
	}
	
	@State
	public static class RapidSwitchTest
	{
		static Object enter()
		{
			return new ClassSignal();
		}
		
		static Object enterClass(ClassSignal s)
		{
			System.out.println("23. class 입력 기민한 전이 확인");
			return EnumSignal.SIGNAL;
		}
		
		static Object enterEnum(EnumSignal s)
		{
			System.out.println("24. enum 입력 기민한 전이 확인");
			return "string";
		}
		
		static void enterString(String s)
		{
			System.out.println("25. string 입력 기민한 전이 확인");
			input("rapid switch test finished");
		}
	}
	
	@State
	public static class DoNothingTest
	{
		static void enter()
		{
			input(new ClassSignal());
			input(EnumSignal.SIGNAL);
			input("string");
			input("complete");
		}
		
		static void enterComplete()
		{
			System.out.println("26. doNothing 확인");
			input("do nothing test finished");
		}
		
		static void defaultEnter()
		{
			System.out.println("실행되면 안되는 진입동작!!");
		}
		
		static void defaultExit()
		{
			System.out.println("실행되면 안되는 퇴장동작!!");
		}
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