package com.intechua;


public class Server
{
	private static DatabaseServer hdb;
	public static DatabaseServer getHdb()
	{
		return hdb;
	}

	public static void main(String[] argv) throws Exception
	{

		try
		{
			hdb = new DatabaseServer("db/db");

			WebServer.init();
	
			while(true)
			{
				Thread.sleep(100);
				// wait for application termination
			}
		}
		finally
		{
			hdb.shutdown();
		}
	}
}
