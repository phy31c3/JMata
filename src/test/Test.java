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
				System.out.print("Input Signal: ");
				byte[] buf = new byte[256];
				System.in.read(buf);
			}
		}
		catch (IOException e)
		{
		
		}
	}
}