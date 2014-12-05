package com.intechua.db.managers;

import com.intechua.DatabaseServer;
import com.intechua.Server;

public abstract class AbstractTable implements Table
{

	protected final DatabaseServer db;

	public AbstractTable()
	{
		super();
		db = Server.getHdb();
		create();
	}
}