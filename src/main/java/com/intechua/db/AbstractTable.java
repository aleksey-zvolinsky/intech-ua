package com.intechua.db;

import ggsmvkr.Server;

import com.intechua.HDatabase;

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