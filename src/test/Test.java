package test;

import java.io.*;

public class Test
{
	public static void main(String[] args)
	{
		TestMachine testMachine = new TestMachine();
		try
		{
			while (true)
			{
				byte[] buf = new byte[256];
				System.in.read(buf);
				testMachine.input(new String(buf).trim());
			}
		}
		catch (IOException e)
		{
		
		}
	}
}