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
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public Journal(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		JournalTable jtable = new JournalTable();
		PacketJournalCriteria crit = new PacketJournalCriteria();
		try
		{
			crit.dateFrom = df.parse(request.queryParams("dateFrom"));
		}
		catch (NullPointerException | ParseException e)
		{
			crit.dateFrom = null;
		}
		try
		{
			crit.dateTo = df.parse(request.queryParams("dateTo"));
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
