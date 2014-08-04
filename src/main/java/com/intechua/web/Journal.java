package com.intechua.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.JournalTable;
import com.intechua.db.PacketJournalCriteria;
import com.intechua.db.beans.PacketJournalEntry;

public class Journal extends Route
{
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public Journal(String path)
	{
		super(path);
	}

	@Override
	public synchronized Object handle(Request request, Response response)
	{
		JournalTable jtable = new JournalTable();
		PacketJournalCriteria crit = new PacketJournalCriteria();
		
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
		

		if("checked".equals(request.queryParams("counter0")))
		{
			crit.counterIds.add(0);
		}
		if("checked".equals(request.queryParams("counter1")))
		{
			crit.counterIds.add(1);
		}
		if("checked".equals(request.queryParams("counter2")))
		{
			crit.counterIds.add(2);
		}
		
		List<PacketJournalEntry> result = jtable.query(crit);
		request.attribute("result", result);
		
		for(String param: request.queryParams())
		{
			request.attribute(param, request.queryParams(param));
		}
		
		return null;
	}

}
