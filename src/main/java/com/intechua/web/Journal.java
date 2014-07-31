package com.intechua.web;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.JournalTable;
import com.intechua.db.PacketJournalCriteria;
import com.intechua.db.beans.PacketJournalEntry;

public class Journal extends Route
{
	public Journal()
	{
		super("/packetjournal");
	}

	@Override
	public Object handle(Request request, Response response)
	{
		JournalTable jtable = new JournalTable();
		PacketJournalCriteria crit = new PacketJournalCriteria();
		request.queryParams("dateFrom");
		request.queryParams("dateTo");
		request.queryParams("counter0");
		request.queryParams("counter1");
		request.queryParams("counter2");
		
		List<PacketJournalEntry> result = jtable.query(crit);
		request.attribute("result", result);
		return null;
	}

}
