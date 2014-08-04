package com.intechua.web;

import org.apache.log4j.Logger;

import spark.Filter;
import spark.Request;
import spark.Response;

import com.intechua.db.SettingsTable;

public class ConverterFilter extends Filter
{
	private final static Logger LOG = Logger.getLogger(ConverterFilter.class);
	private final SettingsTable settings = new SettingsTable();

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
		request.attribute("b1", b1);
				
		Integer rawlevel1 = getRawLevel(request, "B2", "B3");
		Integer rawlevel2 = getRawLevel(request, "B4", "B5");
		Integer rawlevel3 = getRawLevel(request, "B6", "B7");
		
		request.attribute("rawlevel1", rawlevel1);
		request.attribute("rawlevel2", rawlevel2);
		request.attribute("rawlevel3", rawlevel3);
		
		
		request.attribute("level1", getLevel(rawlevel1, 1));
		request.attribute("level2", getLevel(rawlevel2, 2));
		request.attribute("level3", getLevel(rawlevel3, 3));
		
		

	}

	private int getLevel(Integer rawlevel1, int counter)
	{
		//(А-B)*C

		Float c = Float.parseFloat(settings.get("c"+counter));
		Float b = Float.parseFloat(settings.get("b"+counter));
		
		return Math.round((rawlevel1 - b) * c);
	}
	
	public int getRawLevel(Request request, String param1, String param2)
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
