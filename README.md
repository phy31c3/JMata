# JMata
----------
자바 상태 기반 프로그래밍 라이브러리</br>
Java State-based Programming Library</br>
</br>
`#Java Automata #자바 오토마타

## 적용 하기
----------

### Gradle
```groovy
dependencies {
	compile 'kr.co.plasticcity:jmata:v.0.5.4'
}
```

### Maven
```xml
<dependency>
	<groupId>kr.co.plasticcity</groupId>
	<artifactId>jmata</artifactId>
	<version>v.0.5.4</version>
	<type>pom</type>
</dependency>
```

### 예시 코드
```java
JMata.initialize();
JMata.setLogFunction(log -> System.out.println(log));

JMata.buildMachine(LoginMachine.class, builder ->
{
	builder.ifPresentThenIgnoreThis(definer ->
	{
		definer.defineStartState(InputIdPw.class)
			.whenEnter(InputIdPw::enter)
			.whenInput(IdPwPair.class).justSwitchTo(CheckIdPw.class)
			.whenExit(InputIdPw::exit)
			.apply()
			
			...
			
			.defineState(Finish.class)
			.whenEnter(Finish::finishMachine)
			.apply()
			
			.build();
	});
});

JMata.runMachine(LoginMachine.class);

JMata.inputTo(LoginMachine.class, new IdPwPair("myId", "myPassword"));
```

## 사용법
----------

### JMata 초기화 및 로그 출력 함수 정의
JMata를 사용하기 위해 우선 다음 메소드를 호출해 초기화를 수행합니다.
```java
JMata.initialize();
```

JMata에서 발생한 로그를 받아보고 싶다면 다음처럼 로그 출력 함수를 정의해줍니다.
```java
JMata.setLogFunction(log -> System.out.println(log));
```
로그 출력 함수는 개발 플랫폼에 맞게 지정해주면 됩니다. (위 코드는 `System.out.println()`을 사용했습니다.)

### 샘플 상태도
다음은 간단한 로그인 머신의 상태도입니다. 라이브러리 사용법은 아래 상태도를 구현하는 방식으로 진행됩니다.
 
![enter image description here](https://i.imgur.com/BpDcxc8.png)

### 머신 생성 및 정의
LoginMachine이라는 이름의 머신을 생성하기 위해 최초로 호출하는 메소드는 다음과 같습니다.
```java
JMata.buildMachine(LoginMachine.class, builder ->
{
	// TODO
});
```
`JMata`는 `Class` 자료형을 머신의 태그(식별자)로 사용합니다. 따라서 LoginMachine이라는 이름의 머신을 생성하기 위해서는 동일한 이름의 클래스를 어딘가 정의해줘야 됩니다. LoginMachine 클래스가 정의되면 그 이후로는 해당 머신을 생성하거나 제어하기 위해 `LoginMachine.class`를 태그로서 사용할 수 있습니다. LoginMachine 클래스(머신 태그로 사용되는 클래스)는 빈 클래스라도 상관 없으며 특별히 상속 받거나 구현해야 되는 메소드 또한 없습니다.
머신 태그의 자료형으로 `String`이나 `Integer`와 같은 간단한 자료형을 사용하지 않고 `Class` 자료형을 사용한 이유는, 머신 이름을 변경하거나 코드상에서 머신이 참조된 위치를 찾을 때 IDE의 기능을 활용할 수 있도록 하기 위함입니다.
위 코드는 JMata에게 LoginMachine이라는 이름의 머신을 생성할 것이라고 알려주지만, 이전에 이미 동일한 태그의 머신이 생성되어 있을 수도 있습니다. 때문에 위의 TODO 위치에 다음과 같은 코드를 작성해줍니다.
```java
builder.ifPresentThenIgnoreThis(definer ->
{
	// TODO
});
```
`ifPresentThenIgnoreThis()`메소드는 Java 8의 `Optional`과 비슷한 역할을 하는 것으로서, 만약 동일한 이름의 머신이 이미 생성되어 있다면 이어지는 정의를 무시하도록 합니다. 위 람다식의 `definer` 매개변수를 통해 머신에 대한 본격적인 정의(상태와 전이에 대한 정의)를 할 수 있습니다. 아래 코드는 지금까지 작성한 모든 코드에 `definer` 사용을 추가한 모습입니다.
```java
JMata.buildMachine(LoginMachine.class, builder ->
{
	builder.ifPresentThenIgnoreThis(definer ->
	{
		definer.defineStartState(InputIdPw.class)
			
			// TODO InputIdPw 상태에 대한 정의
			
			.apply()
			
			// TODO CheckIdPw 등의 다른 상태에 대한 정의
			
			.build();
	});
});
```
`definer`의 `defineStartState()` 메소드 호출을 통해 머신의 시작 상태를 선언합니다. LoginMachine 상태도의 좌측 상단을 보면 InputIdPw라는 상태가 존재하는데, 이를 LoginMachine의 시작 상태로 설정하겠다는 뜻입니다. 각 상태를 식별하기 위한 태그는 머신과 동일하게 `Class` 자료형을 사용하며, InputIdPw라는 상태를 정의하기 위해 동일한 이름의 클래스를 어딘가에 정의하여 `InputIdPw.class`를 태그로서 사용합니다. `defineStartState(InputIdPw.class)`를 호출하면 이후 부터는 InputIdPw 상태에 대한 정의를 수행하며, 모든 정의가 끝나면 `apply()` 메소드를 호출해줍니다. 이에 대한 코드는 다음과 같습니다.
```java
definer.defineStartState(InputIdPw.class)
	.whenEnter(InputIdPw::enter)
	.whenInput(IdPwPair.class).justSwitchTo(CheckIdPw.class)
	.whenExit(InputIdPw::exit)
	.apply()
	
...

public static class InputIdPw
{
	public static void enter()
	{
		// TODO InputIdPw 진입 시 동작
	}
	
	public static void exit()
	{
		// TODO InputIdPw 퇴장 시 동작
	}
}
```
상태 정의는 크게 **진입 동작**, **퇴장 동작**, **전이 규칙**으로 이루어지며 이는 위 코드에서 각각 `whenEnter()`, `whenExit()`, `whenInput()` 메소드로 구현 됩니다.
**진입 동작**은 상태 진입 시 실행하는 동작을 뜻하며 **퇴장 동작**은 반대로 다른 상태로 전이될 때 해당 상태를 떠나기 전 마지막으로 실행하는 동작을 뜻합니다. 가령 위 코드의 경우 InputIdPw가 LoginMachine의 시작 상태이므로 머신 구동 시 InputIdPw 상태로 진입하게 되고, 이때 InputIdPw 클래스의 `enter()` 메소드가 자동으로 실행됩니다. 위 코드에는 코드 가독성 및 디자인 측면에서 상태의 태그 역할을 하는 클래스(InputIdPw)에 정적 메소드(enter)를 정의해 사용 하였으나, `whenEnter()` 메소드의 파라미터로는 어떠한 메소드를 전달해도 상관 없으며 람다식을 전달해도 무방합니다.
**전이 규칙**은 입력 신호와 목적지 상태로 구성되며, 특정 입력이 들어왔을 때 현재 상태가 어떤 상태로 전이 될 것인지에 대한 규칙을 의미합니다. LoginMachine의 상태도를 보면 InputIdPw 상태에 IdPwPair라는 입력이 들어왔을 경우 CheckIdPw라는 상태로 전이하도록 되어 있습니다. 코드에서는 이 정의를 `whenInput()`과 `justSwitchTo()` 메소드를 이용해 구현하고 있습니다.
`apply()` 메소드를 호출하면 현재 상태에 대한 정의를 확정하고 다음 정의로 넘어갈 수 있습니다. 다음 코드는 InputIdPw 상태에 이어 CheckIdPw 상태를 정의하는 코드입니다.
```java
definer.defineStartState(InputIdPw.class)
	.whenEnter(InputIdPw::enter)
	.whenExit(InputIdPw::exit)
	.whenInput(IdPwPair.class).justSwitchTo(CheckIdPw.class)
	.apply()
	
	.defineState(CheckIdPw.class)
	.whenEnterFrom(IdPwPair.class).doThis(CheckIdPw::checkIdPw)
	.whenInput(IdPwPair.class).switchTo(CheckIdPw.class).AndDoNothing()
	.whenInput(SIGNAL.VALID).justSwitchTo(LoginEnabled.class)
	.whenInput(SIGNAL.INVALID).justSwitchTo(InputIdPw.class)
	.whenExit(CheckIdPw::exit)
	.apply()
```
CheckIdPw 상태는 InputIdPw 상태보다 다소 복잡합니다. CheckIdPw 상태는 IdPwPair 신호에 의해 자신으로 전이가 발생한 경우 IdPwPair 신호(IdPwPair 인스턴스)를 파라미터로 받아 id와 password의 유효성을 검사하는 진입 동작을 수행합니다. 이와 같이 **특정 신호에 특화된 진입 동작**을 정의하기 위해 `whenEnterFrom()` 메소드를 사용할 수 있습니다. `whenEnterFrom()`으로 정의된 진입 동작은 `whenEnter()`로 정의된 기본 진입 동작을 오버라이드 합니다. `whenEnterFrom()` 메소드의 파라미터로 전달 된 checkIdPw 메소드 레퍼런스는 CheckIdPw 클래스에 다음과 같이 정의되어 있습니다.
```java
public static class CheckIdPw
{
	public static void checkIdPw(IdPwPair pair)
	{
		// TODO id, password 유효성 검사
	}
	
	public static void exit()
	{
		// TODO
	}
}
```
CheckIdPw 상태 정의에는 `IdPwPair.class`, `SIGNAL.VALID`, `SIGNAL.INVALID` 세 종류의 입력 신호가 등장합니다. JMata에서 입력 신호로 사용할 수 있는 자료형은 **1**. `<? extends Object>`의 인스턴스, **2**. `Enum`, **3**. `String` 입니다. **1**의 경우 상태 정의때는 Class 자료형을 사용하지만 실제 입력으로 전달할때는 해당 클래스의 인스턴스를 전달해야되고 **2**와 **3**은 정의/전달 둘다 인스턴스를 사용합니다. 위 코드를 보면 상태를 정의하는 `whenInput(IdPwPair.class)`에는 IdPwPair의 Class 자료형을 사용했지만 실제로 입력 신호가 전달되는`checkIdPw(IdPwPair pair)` 메소드를 보면 IdPwPair의 인스턴스가 전달 됨을 확인할 수 있습니다. 다음은 `String` 입력 신호가 정의되어 있는 Certify 상태의 정의 코드입니다.
```java
.defineState(Certify.class)
.whenEnter(Certify::loginToServer)
.whenInput("fail").switchTo(InputIdPw.class).AndDo(Certify::showErrorDialog)
.whenInput(LoginInfo.class).justSwitchTo(Finish.class)
.apply()

...

public static class Certify
{
	public static void loginToServer()
	{
		// TODO 서버에 로그인 시도
	}
	
	public static void showErrorDialog(String signal)
	{
		// TODO 로그인 실패 다이얼로그 출력
	}
}
```
Certify 상태는 `"fail"`이라는 `String`형 신호에 대해 정의하고 있으며 `switchTo(...).AndDo(...)` 메소드를 통해 **특정 신호에 특화된 퇴장 동작**을 정의하고 있습니다. 이는 위에서 살펴본 '특정 신호에 특화된 진입 동작'의 퇴장 버전이라고 보면 됩니다. Certify 상태는 기본 진입 동작으로 서버에 로그인 하는 동작을 수행하며 `"fail"` 신호가 입력된 경우 로그인 실패 다이얼로그를 띄우며 InputIdPw 상태로 전이합니다.

머신의 각 상태들에 대한 정의가 끝나면 다음 메소드를 호출하여 실제로 머신을 생성해줍니다.
```java
.build();
```

지금까지 살펴본 LoginMachine의 생성과 정의 코드를 모두 종합해보면 다음과 같습니다.
```java
JMata.buildMachine(LoginMachine.class, builder ->
{
	builder.ifPresentThenIgnoreThis(definer ->
	{
		definer.defineStartState(InputIdPw.class)
			.whenEnter(InputIdPw::enter)
			.whenInput(IdPwPair.class).justSwitchTo(CheckIdPw.class)
			.whenExit(InputIdPw::exit)
			.apply()
			
			.defineState(CheckIdPw.class)
			.whenEnterFrom(IdPwPair.class).doThis(CheckIdPw::checkIdPw)
			.whenInput(IdPwPair.class).switchTo(CheckIdPw.class).AndDoNothing()
			.whenInput(SIGNAL.VALID).justSwitchTo(LoginEnabled.class)
			.whenInput(SIGNAL.INVALID).justSwitchTo(InputIdPw.class)
			.whenExit(CheckIdPw::exit)
			.apply()
			
			.defineState(LoginEnabled.class)
			.whenEnter(LoginEnabled::enableLoginButton)
			.whenInput(SIGNAL.CLICK_LOGIN).justSwitchTo(Certify.class)
			.whenInput(IdPwPair.class).justSwitchTo(CheckIdPw.class)
			.apply()
			
			.defineState(Certify.class)
			.whenEnter(Certify::loginToServer)
			.whenInput("fail").switchTo(InputIdPw.class).AndDo(Certify::showErrorDialog)
			.whenInput(LoginInfo.class).justSwitchTo(Finish.class)
			.apply()
			
			.defineState(Finish.class)
			.whenEnter(Finish::finishMachine)
			.apply()
			
			.build();
	});
});
```

### 머신 실행
위에서 생성한 LoginMachine을 실행하기 위해 다음의 메소드를 호출해줍니다.
```java
JMata.runMachine(LoginMachine.class);
```

### 신호 입력
실행 중인 머신에 신호를 입력하는 메소드는 다음과 같습니다.
```java
JMata.inputTo(LoginMachine.class, "fail");
```
위 코드는 LoginMachine에 `"fail"` 이라는 `String`형 신호를 입력하는 코드입니다.

### 머신 정지 및 종료
머신 정지 및 종료는 다음 메소드를 사용합니다.
```java
JMata.stopMachine(LoginMachine.class);
JMata.terminateMachine(LoginMachine.class);
```
`JMata.stopMachine()` 메소드는 머신을 일시 정지시켜 입력 큐에 쌓여있는 신호들을 삭제하고 이후 입력되는 신호들 또한 무시합니다. 다시 실행 상태로 머신을 재개하고 싶다면 `JMata.runMachine()` 메소드를 호출해 줍니다.
`JMata.terminateMachine()` 메소드는 머신을 완전히 종료시켜 재실행이 불가능한 상태로 만들고 폐기 시킵니다.


## 기타
----------

### 자바 버전
JMata의 인터페이스는 Java 8의 람다 및 메소드 레퍼런스 사용시 유리하도록 설계되어 있습니다. 따라서 권장 자바 버전은 8 입니다. 하지만 JMata 라이브러리 코드 자체는 Java 5 기준으로 작성되어 있으므로 Java 5 이상의 환경에서도 사용 가능합니다.

### 안드로이드에 적용
안드로이드는 아직 Java 8 지원이 미흡하므로 Retrolambda를 통해 람다 및 메소드 레퍼런스 문법을 사용 가능한 환경으로 만든 뒤 JMata 사용을 권장합니다.

### Thread
JMata의 각 머신은 서로 다른 Thread에서 구동되며 머신 당 하나의 입력 신호 큐가 할당 됩니다. 따라서 각 머신들은 병렬로 실행되지만, 한 머신에 대한 진입/퇴장 동작 및 입력 신호 획득은 순차성이 보장됩니다.

### 버전 코드
**major.minor.fix** (ex : 1.0.2)
- **major** 인터페이스 수정 (이전 버전과의 호환되지 않음)
- **minor** 인터페이스 추가 혹은 내부 구현 수정 (이전 버전과 호환 됨)
- **fix** 버그, 오류 등의 수정

## License
----------
```
The MIT License (MIT)

Copyright (c) 2016 Jongsun Yu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```