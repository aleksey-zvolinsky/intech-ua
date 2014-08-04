package com.intechua.web;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.SettingsTable;

public class Settings extends Route
{

	public Settings(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		SettingsTable table = new SettingsTable();
		
		request.attribute("result", table.getList());
		return null;
	}

}
