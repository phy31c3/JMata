package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMPredicate;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

class JMStateImpl implements JMState
{
	private static class FuncSet<C, E, S>
	{
		private C classFunc;
		private E enumFunc;
		private S stringFunc;
		
		private FuncSet setClassFunc(final C classFunc)
		{
			this.classFunc = classFunc;
			return this;
		}
		
		private FuncSet setEnumFunc(final E enumFunc)
		{
			this.enumFunc = enumFunc;
			return this;
		}
		
		private FuncSet setStringFunc(final S stringFunc)
		{
			this.stringFunc = stringFunc;
			return this;
		}
	}
	
	private Object machineTag;
	private Class<?> stateTag;
	
	private JMSupplier<Object> enter;
	private JMVoidConsumer exit;
	
	private Map<Object, FuncSet<
			JMFunction<? super Object, Object>,
			JMFunction<Enum<?>, Object>,
			JMFunction<String, Object>>> enterMap;
	
	private Map<Object, FuncSet<
			JMConsumer<? super Object>,
			JMConsumer<Enum<?>>,
			JMConsumer<String>>> exitMap;
	
	private Map<Object, Class<?>> switchRule;
	
	JMStateImpl(Object machineTag, Class<?> stateTag)
	{
		this.machineTag = machineTag;
		this.stateTag = stateTag;
		
		this.enterMap = new HashMap<>();
		this.exitMap = new HashMap<>();
		this.switchRule = new HashMap<>();
	}
	
	@Override
	public Object runEnterFunction()
	{
		if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S> Object runEnterFunctionC(S signal)
	{
		if (enterMap.containsKey(signal.getClass()))
		{
			return enterMap.get(signal.getClass()).classFunc.apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S extends Enum<S>> Object runEnterFunction(Enum<S> signal)
	{
		if (enterMap.containsKey(signal))
		{
			return enterMap.get(signal).enumFunc.apply(signal);
		}
		else if (enterMap.containsKey(signal.getClass()))
		{
			return enterMap.get(signal.getClass()).classFunc.apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Object runEnterFunction(String signal)
	{
		if (enterMap.containsKey(signal))
		{
			return enterMap.get(signal).stringFunc.apply(signal);
		}
		else if (enterMap.containsKey(signal.getClass()))
		{
			return enterMap.get(signal.getClass()).classFunc.apply(signal);
		}
		else if (enter != null)
		{
			return enter.get();
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void runExitFunction()
	{
		if (exit != null)
		{
			exit.accept();
		}
	}
	
	@Override
	public <S> Object runExitFunctionC(S signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter)
	{
		if (switchRule.containsKey(signal.getClass()))
		{
			final Class<?> nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.accept();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineTag, stateTag.getSimpleName(), nextState.getSimpleName(), signal, nextState.getSimpleName());
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public <S extends Enum<S>> Object runExitFunction(Enum<S> signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter)
	{
		if (switchRule.containsKey(signal))
		{
			final Class<?> nextState = switchRule.get(signal);
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal))
				{
					exitMap.get(signal).enumFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.accept();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineTag, stateTag.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + signal, nextState.getSimpleName());
				return null;
			}
		}
		else if (switchRule.containsKey(signal.getClass()))
		{
			final Class<?> nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.accept();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineTag, stateTag.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + signal, nextState.getSimpleName());
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Object runExitFunction(String signal, JMPredicate<Class<?>> hasState, JMFunction<Class<?>, Object> nextEnter)
	{
		if (switchRule.containsKey(signal))
		{
			final Class<?> nextState = switchRule.get(signal);
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal))
				{
					exitMap.get(signal).stringFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.accept();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_STRING, machineTag, stateTag.getSimpleName(), nextState.getSimpleName(), signal, nextState.getSimpleName());
				return null;
			}
		}
		else if (switchRule.containsKey(signal.getClass()))
		{
			final Class<?> nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.accept();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineTag, stateTag.getSimpleName(), nextState.getSimpleName(), signal, nextState.getSimpleName());
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void putEnterFunction(JMSupplier<Object> func)
	{
		if (enter != null)
		{
			JMLog.error(JMLog.ENTER_FUNC_DUPLICATED, machineTag, stateTag.getSimpleName());
		}
		
		enter = func;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(Class<?> signal, JMFunction<? super Object, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(JMLog.ENTER_BY_CLASS_FUNC_DUPLICATED, machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		enterMap.put(signal, new FuncSet<>().setClassFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(Enum<?> signal, JMFunction<Enum<?>, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(JMLog.ENTER_BY_CLASS_FUNC_DUPLICATED, machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		enterMap.put(signal, new FuncSet<>().setEnumFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(String signal, JMFunction<String, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(JMLog.ENTER_BY_STRING_FUNC_DUPLICATED, machineTag, signal, stateTag.getSimpleName());
		}
		
		enterMap.put(signal, new FuncSet<>().setStringFunc(func));
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
		if (exit != null)
		{
			JMLog.error(JMLog.EXIT_FUNC_DUPLICATED, machineTag, stateTag.getSimpleName());
		}
		
		exit = func;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(Class<?> signal, JMConsumer<? super Object> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(JMLog.EXIT_BY_CLASS_FUNC_DUPLICATED, machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		exitMap.put(signal, new FuncSet<>().setClassFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(Enum<?> signal, JMConsumer<Enum<?>> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(JMLog.EXIT_BY_CLASS_FUNC_DUPLICATED, machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		exitMap.put(signal, new FuncSet<>().setEnumFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(String signal, JMConsumer<String> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(JMLog.EXIT_BY_STRING_FUNC_DUPLICATED, machineTag, signal, stateTag.getSimpleName());
		}
		
		exitMap.put(signal, new FuncSet<>().setStringFunc(func));
	}
	
	@Override
	public void putSwitchRule(Class<?> signal, Class<?> stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(JMLog.SWITCH_RULE_BY_CLASS_DUPLICATED, machineTag, signal.getSimpleName(), stateTag.getSimpleName());
		}
		
		switchRule.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(Enum<?> signal, Class<?> stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(JMLog.SWITCH_RULE_BY_CLASS_DUPLICATED, machineTag, signal.name(), stateTag.getSimpleName());
		}
		
		switchRule.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(String signal, Class<?> stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(JMLog.SWITCH_RULE_BY_STRING_DUPLICATED, machineTag, signal, stateTag.getSimpleName());
		}
		
		switchRule.put(signal, stateTag);
	}
}