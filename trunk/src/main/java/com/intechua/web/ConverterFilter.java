package com.intechua.web;

import org.apache.log4j.Logger;

import spark.Filter;
import spark.Request;
import spark.Response;

public class ConverterFilter extends Filter
{
	private final static Logger LOG = Logger.getLogger(ConverterFilter.class);

	public ConverterFilter(String path)
	{
		super(path);
	}

	@Override
	public void handle(Request request, Response response)
	{
		LOG.debug("Received request "+ request.pathInfo());
		
		request.attribute("lost_connection", request.pathInfo().contains("&&"));
		
		request.attribute("modemid", Integer.parseInt(request.queryParams("ID"), 16));
		request.attribute("connection_level", Integer.parseInt(request.queryParams("CSQ"), 16));
		
		
		Integer crc = Integer.parseInt(request.queryParams("B"+1), 16);
		for(int i=2; i <= 7; i++)
		{
			crc = crc ^ Integer.parseInt(request.queryParams("B"+i), 16);
		}
		
		if(crc != Integer.parseInt(request.queryParams("B8"), 16))
		{
			LOG.error("CRC check failed");
			request.attribute("crc", "failed");
		}
		else
		{
			request.attribute("crc", "success");
		}
		
		String b1 = Integer.toString(Integer.parseInt(request.queryParams("B1"), 16), 2);
		request.attribute("power", ('1' == b1.charAt(6) && '1' == b1.charAt(7)));
				
		Integer level1 = getLevel(request, "B2", "B3");
		Integer level2 = getLevel(request, "B4", "B5");
		Integer level3 = getLevel(request, "B6", "B7");
		
		request.attribute("level1", level1);
		request.attribute("level2", level2);
		request.attribute("level3", level3);
	}

	public int getLevel(Request request, String param1, String param2)
	{
		return Integer.parseInt(
				Integer.toString(
						Integer.parseInt(request.queryParams(param1)),
						16) 
				+ Integer.toString(
						Integer.parseInt(request.queryParams(param2)),
						16), 
				16);
	}

}
