package com.intechua.web;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.jooq.tools.StringUtils;

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
		try
		{
			LOG.debug("Received request "+ request.pathInfo() + "?" + request.queryString());
			
			request.attribute("lost_connection", request.queryString().contains("&&"));
			
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
			
			String b1 = StringUtils.leftPad(Integer.toString(Integer.parseInt(request.queryParams("B1"), 16), 2), 8, "0");
			request.attribute("power", ('1' == b1.charAt(6) && '1' == b1.charAt(7))?1:0);
			request.attribute("b1", b1);
					
			Integer rawlevel1 = getRawLevel(request, "B2", "B3");
			Integer rawlevel2 = getRawLevel(request, "B4", "B5");
			Integer rawlevel3 = getRawLevel(request, "B6", "B7");
			
			request.attribute("rawlevel1", rawlevel1);
			request.attribute("rawlevel2", rawlevel2);
			request.attribute("rawlevel3", rawlevel3);
			
			LOG.debug(MessageFormat.format("rawlevel1 = {0}, rawlevel2 = {1}, rawlevel3 = {2}", rawlevel1, rawlevel2, rawlevel3));
			
			int level1 = getLevel(rawlevel1, 1);
			int level2 = getLevel(rawlevel2, 2);
			int level3 = getLevel(rawlevel3, 3);
			
			request.attribute("level1", level1);
			request.attribute("level2", level2);
			request.attribute("level3", level3);
			
			LOG.debug(MessageFormat.format("level1 = {0}, level2 = {1}, level3 = {2}", level1, level2, level3));
		}
		catch(Throwable th)
		{
			LOG.error("Failed to convert data", th);
		}
	}

	private int getLevel(Integer rawlevel1, int counter)
	{
		//(А-B)*C
		
		float c = Float.parseFloat(settings.get("c"+counter).replace(",", "."));
		float b = Float.parseFloat(settings.get("b"+counter).replace(",", "."));
		
		return Math.round((rawlevel1 - b) * c);
	}
	
	public int getRawLevel(Request request, String param1, String param2)
	{
		return Integer.parseInt(StringUtils.leftPad(request.queryParams(param1), 2, "0") + StringUtils.leftPad(request.queryParams(param2), 2, "0"),16);
	}

}
