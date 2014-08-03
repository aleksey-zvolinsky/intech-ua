package com.intechua.web;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;
import com.intechua.db.PacketsTable;

public class IndexStatus extends Route
{

	public IndexStatus(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		PacketsTable table = new PacketsTable();
		Gson gson = new Gson();
		String gsonPacket = gson.toJson(table.getLastPacket());
		request.attribute("packet", gsonPacket);

		return null;
	}

}
