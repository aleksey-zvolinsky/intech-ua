package com.intechua.db;

import com.intechua.HDatabase;
import com.intechua.Server;

public abstract class AbstractTable implements Table
{

	protected final HDatabase db;

	public AbstractTable()
	{
		super();
		db = Server.getHdb();
		create();
	}
}