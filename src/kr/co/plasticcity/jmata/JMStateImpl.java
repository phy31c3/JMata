package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
	
	private final String machineName;
	private final Class stateTag;
	
	private Supplier<Object> enter;
	private Runnable exit;
	
	private final Map<Object, FuncSet<
			Function<? super Object, Object>,
			Function<Enum, Object>,
			Function<String, Object>>> enterMap;
	
	private final Map<Object, FuncSet<
			Consumer<? super Object>,
			Consumer<Enum>,
			Consumer<String>>> exitMap;
	
	private final Map<Object, Class> switchRule;
	
	JMStateImpl(final String machineName, final Class stateTag)
	{
		this.machineName = machineName;
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
	public <S> Object runEnterFunctionC(final S signal)
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
	public Object runEnterFunction(final Enum signal)
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
	public Object runEnterFunction(final String signal)
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
			exit.run();
		}
	}
	
	@Override
	public <S> Object runExitFunctionC(final S signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter)
	{
		if (switchRule.containsKey(signal.getClass()))
		{
			final Class nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.run();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(out -> out.print(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineName, stateTag.getSimpleName(), nextState.getSimpleName(), JMLog.getPackagelessName(signal), nextState.getSimpleName()));
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Object runExitFunction(final Enum signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter)
	{
		if (switchRule.containsKey(signal))
		{
			final Class nextState = switchRule.get(signal);
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal))
				{
					exitMap.get(signal).enumFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.run();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(out -> out.print(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineName, stateTag.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal), nextState.getSimpleName()));
				return null;
			}
		}
		else if (switchRule.containsKey(signal.getClass()))
		{
			final Class nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.run();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(out -> out.print(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineName, stateTag.getSimpleName(), nextState.getSimpleName(), signal.getClass().getSimpleName() + "." + JMLog.getPackagelessName(signal), nextState.getSimpleName()));
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Object runExitFunction(final String signal, final Predicate<Class> hasState, final Function<Class, Object> nextEnter)
	{
		if (switchRule.containsKey(signal))
		{
			final Class nextState = switchRule.get(signal);
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal))
				{
					exitMap.get(signal).stringFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.run();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(out -> out.print(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_STRING, machineName, stateTag.getSimpleName(), nextState.getSimpleName(), signal, nextState.getSimpleName()));
				return null;
			}
		}
		else if (switchRule.containsKey(signal.getClass()))
		{
			final Class nextState = switchRule.get(signal.getClass());
			if (hasState.test(nextState))
			{
				if (exitMap.containsKey(signal.getClass()))
				{
					exitMap.get(signal.getClass()).classFunc.accept(signal);
				}
				else if (exit != null)
				{
					exit.run();
				}
				
				return nextEnter.apply(nextState);
			}
			else
			{
				JMLog.error(out -> out.print(JMLog.SWITCH_TO_UNDEFINED_STATE_BY_CLASS, machineName, stateTag.getSimpleName(), nextState.getSimpleName(), JMLog.getPackagelessName(signal), nextState.getSimpleName()));
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void putEnterFunction(final Supplier<Object> func)
	{
		if (enter != null)
		{
			JMLog.error(out -> out.print(JMLog.ENTER_FUNC_DUPLICATED, machineName, stateTag.getSimpleName()));
		}
		
		enter = func;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(final Class signal, final Function<? super Object, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.ENTER_BY_CLASS_FUNC_DUPLICATED, machineName, signal.getSimpleName(), stateTag.getSimpleName()));
		}
		
		enterMap.put(signal, new FuncSet<>().setClassFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(final Enum signal, final Function<Enum, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.ENTER_BY_CLASS_FUNC_DUPLICATED, machineName, signal.name(), stateTag.getSimpleName()));
		}
		
		enterMap.put(signal, new FuncSet<>().setEnumFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putEnterFunction(final String signal, final Function<String, Object> func)
	{
		if (enterMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.ENTER_BY_STRING_FUNC_DUPLICATED, machineName, signal, stateTag.getSimpleName()));
		}
		
		enterMap.put(signal, new FuncSet<>().setStringFunc(func));
	}
	
	@Override
	public void putExitFunction(final Runnable func)
	{
		if (exit != null)
		{
			JMLog.error(out -> out.print(JMLog.EXIT_FUNC_DUPLICATED, machineName, stateTag.getSimpleName()));
		}
		
		exit = func;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(final Class signal, final Consumer<? super Object> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.EXIT_BY_CLASS_FUNC_DUPLICATED, machineName, signal.getSimpleName(), stateTag.getSimpleName()));
		}
		
		exitMap.put(signal, new FuncSet<>().setClassFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(final Enum signal, final Consumer<Enum> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.EXIT_BY_CLASS_FUNC_DUPLICATED, machineName, signal.name(), stateTag.getSimpleName()));
		}
		
		exitMap.put(signal, new FuncSet<>().setEnumFunc(func));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void putExitFunction(final String signal, final Consumer<String> func)
	{
		if (exitMap.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.EXIT_BY_STRING_FUNC_DUPLICATED, machineName, signal, stateTag.getSimpleName()));
		}
		
		exitMap.put(signal, new FuncSet<>().setStringFunc(func));
	}
	
	@Override
	public void putSwitchRule(final Class signal, final Class stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.SWITCH_RULE_BY_CLASS_DUPLICATED, machineName, signal.getSimpleName(), stateTag != null ? stateTag.getSimpleName() : "null"));
		}
		
		switchRule.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(final Enum signal, final Class stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.SWITCH_RULE_BY_CLASS_DUPLICATED, machineName, signal.name(), stateTag != null ? stateTag.getSimpleName() : "null"));
		}
		
		switchRule.put(signal, stateTag);
	}
	
	@Override
	public void putSwitchRule(final String signal, final Class stateTag)
	{
		if (switchRule.containsKey(signal))
		{
			JMLog.error(out -> out.print(JMLog.SWITCH_RULE_BY_STRING_DUPLICATED, machineName, signal, stateTag != null ? stateTag.getSimpleName() : "null"));
		}
		
		switchRule.put(signal, stateTag);
	}
}