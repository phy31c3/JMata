package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

class JMStateImpl implements JMStateCreater
{
	@Override
	public void putEnterFunction(JMVoidConsumer func)
	{
	}
	
	@Override
	public void putEnterFunction(Consumer<Integer> func)
	{
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, Consumer<S> func)
	{
	}
	
	@Override
	public <S> void putEnterFunction(Class<S> signal, BiConsumer<S, Integer> func)
	{
	}
	
	@Override
	public void putExitFunction(JMVoidConsumer func)
	{
	}
	
	@Override
	public void putExitFunction(Consumer<Integer> func)
	{
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, Consumer<S> func)
	{
	}
	
	@Override
	public <S> void putExitFunction(Class<S> signal, BiConsumer<S, Integer> func)
	{
	}
	
	@Override
	public void putSwitchRule(Class<?> signal, Class<?> stateTag)
	{
	}
}