package com.intechua.web;

import spark.Filter;
import spark.Request;
import spark.Response;

public class ConverterFilter extends Filter
{

	@Override
	public void handle(Request request, Response response)
	{
		int b1 = Integer.parseInt(request.queryParams("B1"));
		
		request.queryParams("B2");
		request.queryParams("B3");
		request.queryParams("B4");
		request.queryParams("B5");
		request.queryParams("B6");
		request.queryParams("B7");
		request.queryParams("B8");
		

	}

}
