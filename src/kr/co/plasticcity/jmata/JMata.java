package kr.co.plasticcity.jmata;

import java.util.function.*;

public interface JMata
{
	/**
	 * 머신이 이미 존재 할 경우 'getMachine()'과 동일한 역할을 함
	 */
	default <T> JMMachine createMachine(Class<T> tag)
	{
		// TODO
		return null;
	}
	
	default <T> MachineOptional getMachine(Class<T> tag)
	{
		// TODO
		return null;
	}
	
	public interface MachineOptional
	{
		/**
		 * @return null 일 수 있음.
		 */
		JMMachine takeAnyway();
		
		void ifPresent(Consumer<JMMachine> func);
	}
}