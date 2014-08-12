package com.intechua;

import com.intechua.db.JournalTable;
import com.intechua.db.OperatorTable;
import com.intechua.db.PacketsTable;
import com.intechua.db.SettingsTable;

public class Server
{
	private static HDatabase hdb;
	public static HDatabase getHdb()
	{
		return hdb;
	}

	public static void main(String[] argv) throws Exception
	{

		try
		{
			hdb = new HDatabase("db/db");

			new OperatorTable();
			new SettingsTable();
			new JournalTable();
			new PacketsTable();

			Face.init();
	
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
