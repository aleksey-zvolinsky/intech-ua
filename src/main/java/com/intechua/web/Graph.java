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
import com.intechua.db.jooq.tables.records.JournalRecord;

public class Graph extends Route
{
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
			SimpleDateFormat df = null;
			String date = null;
			
			if(request.queryParams("timeFrom") != null && request.queryParams("dateFrom") != null)
			{
				df = DTF;
				date = request.queryParams("dateFrom") + " " + request.queryParams("timeFrom");
			}
			else if(request.queryParams("dateFrom") != null)
			{
				df = DF;
				date = request.queryParams("dateFrom");
			}
			
			try
			{
				crit.dateFrom = df.parse(date);
			}
			catch (NullPointerException | ParseException e)
			{
				crit.dateFrom = null;
			}
			
			if(request.queryParams("timeTo") != null && request.queryParams("dateTo") != null)
			{
				df = DTF;
				date = request.queryParams("dateTo") + " " + request.queryParams("timeTo");
			}
			else if(request.queryParams("dateTo") != null)
			{
				df = DF;
				date = request.queryParams("dateTo");
			}
			
			try
			{
				crit.dateTo = DF.parse(date);
			}
			catch (NullPointerException | ParseException e)
			{
				crit.dateTo = null;
			}
		}
		
		crit.counterIds.add(Integer.parseInt(request.queryParams("counter")));
		
		List<JournalRecord> result = jtable.query(crit);
		Gson gson = new Gson();
		request.attribute("result", gson.toJson(result));
		
		for(String param: request.queryParams())
		{
			request.attribute(param, request.queryParams(param));
		}
		
		return null;
	}

}
