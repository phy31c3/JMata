package kr.co.plasticcity.jmata.test;

import org.junit.Test;

import kr.co.plasticcity.jmata.JMata;
import kr.co.plasticcity.jmata.annotation.Enter;
import kr.co.plasticcity.jmata.annotation.Exit;
import kr.co.plasticcity.jmata.annotation.Signal;
import kr.co.plasticcity.jmata.annotation.State;
import kr.co.plasticcity.jmata.annotation.Terminate;

/**
 * Created by JongsunYu on 2017-04-02.
 */
public class BasicTest
{
	static volatile boolean isFinish = false;
	
	/*#########################################
	 * Test Main
	 #########################################*/
	@Test
	public void testMain()
	{
		JMata.initialize(System.out::println, System.err::println);
		new TestMachine();
		while (!isFinish)
		{
			try
			{
				Thread.sleep(0, (int)(Math.random() * 50) + 50);
				JMata.input(TestMachine.class, getNoiseInput());
			}
			catch (InterruptedException e)
			{
				isFinish = true;
			}
		}
		System.out.println("- 테스트 끝 -");
	}
	
	private Object getNoiseInput() throws InterruptedException
	{
		int rand = (int)(Math.random() * 3);
		switch (rand)
		{
		case 0:
			return new Noise();
		case 1:
			JMata.runMachine(TestMachine.class);
			return null;
		case 2:
			JMata.pauseMachine(TestMachine.class);
			Thread.sleep(100);
			return null;
		case 3:
			JMata.stopMachine(TestMachine.class);
			Thread.sleep(300);
			return null;
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
					
					       .onCreate(() -> System.out.println("////////////////////////// [머신이 생성 됨] //////////////////////////"))
					       .onPause(() -> System.out.println("////////////////////////// [머신이 일시 정지 됨] //////////////////////////"))
					       .onResume(() -> System.out.println("////////////////////////// [머신이 재개 됨] //////////////////////////"))
					       .onStop(() -> System.out.println("////////////////////////// [머신이 정지 됨] //////////////////////////"))
					       .onRestart(() -> System.out.println("////////////////////////// [머신이 재시작 됨] //////////////////////////"))
					       .onTerminate(BasicTest::onTerminate)
					
					       .build();
				});
			});
		}
	}
	
	@Terminate
	public static void onTerminate()
	{
		System.out.println("////////////////////////// [머신이 종료 됨] //////////////////////////");
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
		/* empty */
	}
	
	@Signal
	public static class Classes
	{
		/* empty */
	}
	
	@Signal
	public static class Noise
	{
		/* empty */
	}
	
	/*#########################################
	 * States
	 #########################################*/
	@State
	public static class Start
	{
		@Enter
		static void enter()
		{
			input("start");
		}
	}
	
	@State
	public static class EnumTest
	{
		@Enter
		static void enter()
		{
			input(EnumSignal.SIGNAL);
		}
		
		@Exit
		static void enumSignal(EnumSignal s)
		{
			System.out.println("1. enum 신호 확인");
			input(EnumSignal.UNKNOWN);
		}
		
		@Exit
		static void enumClassSignal(EnumSignal s)
		{
			System.out.println("2. enum 클래스형 신호 확인");
			input("enum test finished");
		}
	}
	
	@State
	public static class StringTest
	{
		@Enter
		static void enter()
		{
			input("string");
		}
		
		@Exit
		static void stringSignal(String s)
		{
			System.out.println("3. string 신호 확인");
			input("unknown string");
		}
		
		@Exit
		static void stringClassSignal(String s)
		{
			System.out.println("4. string 클래스형 신호 확인");
			input("string test finished");
		}
	}
	
	@State
	public static class CommonEnterTest
	{
		@Enter
		static void enter()
		{
			System.out.println("5. 기본 진입동작 확인");
			input("string");
		}
		
		@Enter
		static void enterString(String s)
		{
			System.out.println("6. string 신호 일반 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		@Enter
		static void enterEnum(EnumSignal s)
		{
			System.out.println("7. enum 신호 일반 진입동작 확인");
			input(new ClassSignal());
		}
		
		@Enter
		static void enterClass(ClassSignal s)
		{
			System.out.println("8. class 신호 일반 진입동작 확인");
			input("common enter test finished");
		}
	}
	
	@State
	public static class VoidEnterTest
	{
		@Enter
		static void enter()
		{
			input("string");
		}
		
		@Enter
		static void enterString()
		{
			System.out.println("9. string 신호 void 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		@Enter
		static void enterEnum()
		{
			System.out.println("10. enum 신호 void 진입동작 확인");
			input("void enter test finished");
		}
	}
	
	@State
	public static class CommonExitTest
	{
		@Enter
		static void enter()
		{
			input("string");
		}
		
		@Exit
		static void exitString(String s)
		{
			System.out.println("11. string 신호 일반 퇴장동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		@Exit
		static void exitEnum(EnumSignal s)
		{
			System.out.println("12. enum 신호 일반 퇴장동작 확인");
			input(new ClassSignal());
		}
		
		@Exit
		static void exitClass(ClassSignal s)
		{
			System.out.println("13. class 신호 일반 퇴장동작 확인");
			input("common exit test finished");
		}
		
		@Exit
		static void exit()
		{
			System.out.println("14. 기본 퇴장동작 확인");
		}
	}
	
	@State
	public static class VoidExitTest
	{
		@Enter
		static void enter()
		{
			input("string");
		}
		
		@Exit
		static void exitString()
		{
			System.out.println("15. string 신호 void 퇴장동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		@Exit
		static void exitEnum()
		{
			System.out.println("16. enum 신호 void 퇴장동작 확인");
			input("void exit test finished");
		}
	}
	
	@State
	public static class SignalsInputTest
	{
		@Enter
		static void enter()
		{
			input(new Classes());
		}
		
		@Exit
		static void exitClasses()
		{
			System.out.println("17. 다중 class 입력 정의의 퇴장동작 확인");
		}
		
		@Enter
		static void enterClasses(Classes s)
		{
			System.out.println("18. 다중 class 입력 정의의 전이 및 진입동작 확인");
			input(EnumSignal.SIGNAL);
		}
		
		@Exit
		static void exitEnums(EnumSignal s)
		{
			System.out.println("19. 다중 enum 입력 정의의 퇴장동작 확인");
		}
		
		@Enter
		static void enterEnums(EnumSignal s)
		{
			System.out.println("20. 다중 enum 입력 정의의 전이 및 진입동작 확인");
			input("strings");
		}
		
		@Exit
		static void exitStrings(String s)
		{
			System.out.println("21. 다중 string 입력 정의의 퇴장동작 확인");
		}
		
		@Enter
		static void enterStrings(String s)
		{
			System.out.println("22. 다중 string 입력 정의의 전이 및 진입동작 확인");
			input("signals input test finished");
		}
	}
	
	@State
	public static class RapidSwitchTest
	{
		@Enter
		static Object enter()
		{
			return new ClassSignal();
		}
		
		@Enter
		static Object enterClass(ClassSignal s)
		{
			System.out.println("23. class 입력 기민한 전이 확인");
			return EnumSignal.SIGNAL;
		}
		
		@Enter
		static Object enterEnum(EnumSignal s)
		{
			System.out.println("24. enum 입력 기민한 전이 확인");
			return "string";
		}
		
		@Enter
		static void enterString(String s)
		{
			System.out.println("25. string 입력 기민한 전이 확인");
			input("rapid switch test finished");
		}
	}
	
	@State
	public static class DoNothingTest
	{
		@Enter
		static void enter()
		{
			input(new ClassSignal());
			input(EnumSignal.SIGNAL);
			input("string");
			input("complete");
		}
		
		@Enter
		static void enterComplete()
		{
			System.out.println("26. doNothing 확인");
			input("do nothing test finished");
		}
		
		@Enter
		static void defaultEnter()
		{
			System.out.println("실행되면 안되는 진입동작!!");
		}
		
		@Exit
		static void defaultExit()
		{
			System.out.println("실행되면 안되는 퇴장동작!!");
		}
	}
	
	@State
	public static class Finish
	{
		@Enter
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
		
		@Exit
		static void exit()
		{
			System.out.println("Finish 상태의 exit()가 호출 됐다");
			JMata.terminateMachine(TestMachine.class);
		}
	}
}