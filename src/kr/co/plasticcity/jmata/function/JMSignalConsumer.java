package kr.co.plasticcity.jmata.function;

import kr.co.plasticcity.jmata.*;

public interface JMSignalConsumer<S>
{
	void func(JMMachine machine, S signal);
}