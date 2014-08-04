package com.intechua.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;
import com.intechua.db.JournalTable;
import com.intechua.db.PacketJournalCriteria;
import com.intechua.db.beans.PacketJournalEntry;

public class Graph extends Route
{
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	public Graph(String path)
	{
		super(path);
	}
	
	@Override
	public Object handle(Request request, Response response)
	{
		JournalTable jtable = new JournalTable();
		PacketJournalCriteria crit = new PacketJournalCriteria();
		
		if(null != request.queryParams("fixedPeriod"))
		{
			
			switch (request.queryParams("fixedPeriod"))
			{
			case "hour":
				crit.dateFrom = DateUtils.addHours(new Date(), -6);
				break;
			case "day":
				crit.dateFrom = DateUtils.addDays(new Date(), -1);
				break;
			case "week":
				crit.dateFrom = DateUtils.addWeeks(new Date(), -1);
				break;
			case "month":
				crit.dateFrom = DateUtils.addMonths(new Date(), -1);
				break;
			case "year":
				crit.dateFrom = DateUtils.addYears(new Date(), -1);
				break;

			default:
				break;
			}
			
			crit.dateTo = new Date();
		}
		else
		{
			try
			{
				synchronized (DF)
				{
					crit.dateFrom = DF.parse(request.queryParams("dateFrom"));
				}
				
			}
			catch (NullPointerException | ParseException e)
			{
				crit.dateFrom = null;
			}
			try
			{
				synchronized (DF)
				{
					crit.dateTo = DF.parse(request.queryParams("dateTo"));
				}				
			}
			catch (NullPointerException | ParseException e)
			{
				crit.dateTo = null;
			}
		}
		
		crit.counterIds.add(Integer.parseInt(request.queryParams("counter")));
		
		List<PacketJournalEntry> result = jtable.query(crit);
		Gson gson = new Gson();
		request.attribute("result", gson.toJson(result));
		
		for(String param: request.queryParams())
		{
			request.attribute(param, request.queryParams(param));
		}
		
		return null;
	}

}
