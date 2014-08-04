package com.intechua.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;
import com.intechua.db.JournalTable;
import com.intechua.db.PacketJournalCriteria;
import com.intechua.db.beans.PacketJournalEntry;

public class IndexData extends Route
{
	private static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	
	public IndexData(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		String json;
		Gson gson = new Gson();
		JournalTable table = new JournalTable();
		PacketJournalCriteria crit = new PacketJournalCriteria();
		
		try
		{
			synchronized (DF)
			{
				//TODO setup proper dates
				crit.dateFrom = DF.parse("2013-12-31");
				crit.dateTo = DF.parse("2014-01-15");
			}
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < 3; i++)
		{
			crit.counterIds.clear();
			crit.counterIds.add(i);
			List<PacketJournalEntry> res = table.query(crit);
	
			json = gson.toJson(res);
			
			request.attribute("list"+i, json);
		}
		
		return null;
	}
}
