package com.intechua;


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Server
{
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	private static DatabaseServer hdb;
	public static DatabaseServer getHdb()
	{
		return hdb;
	}

	public static void main(String[] args) throws Exception
	{
		try
		{
			URL logFile = Thread.currentThread().getContextClassLoader()
					.getResource("log4j.properties");
			if(logFile == null)
			{
				throw new RuntimeException("Failed to find log4j.properties file");
			}

			PropertyConfigurator.configure(logFile);

			hdb = new DatabaseServer("db/db");	

			try
			{
				hdb.init();
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
		catch(Throwable th)
		{
			LOG.error("Failed to start server", th);
		}
	}
}
