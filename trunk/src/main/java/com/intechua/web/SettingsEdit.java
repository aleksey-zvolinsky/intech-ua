package com.intechua.web;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.SettingsTable;

public class SettingsEdit extends Route
{
	private final SettingsTable table = new SettingsTable();

	public SettingsEdit(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		String name = request.queryParams("name");
		if(null != request.queryParams("value"))
		{
			table.update(name, request.queryParams("value"));
		};
		request.attribute("name", name);
		request.attribute("value", table.get(name));
		return null;
	}

}
