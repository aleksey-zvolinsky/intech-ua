package com.intechua.web;

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

public class IndexData extends Route
{
	
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
		
		Date date = new Date();
		crit.dateFrom = DateUtils.addHours(date, -6);
		crit.dateTo = date;
		
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
