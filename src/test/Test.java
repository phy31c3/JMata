package test;

public class Test
{
	public static void main(String[] args)
	{
		TestMachine testMachine = new TestMachine();
		synchronized (TestMachine.class)
		{
			try
			{
				TestMachine.class.wait();
			}
			catch (InterruptedException e)
			{
				
			}
			finally
			{
				
			}
		}
	}
}