package com.intechua.web;

import spark.Request;
import spark.Response;
import spark.Route;

public class IndexStatus extends Route
{

	public IndexStatus()
	{
		super("indexstatus");
	}

	@Override
	public Object handle(Request request, Response response)
	{
		
		//include to request last packet

		return null;
	}

}
