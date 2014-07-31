package com.intechua.web;

import java.util.Date;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.JournalTable;
import com.intechua.db.PacketsTable;
import com.intechua.db.beans.PacketEntry;

public class Input extends Route
{
	public Input()
	{
		super("/input");
	}

	@Override
	public Object handle(Request request, Response response)
	{
		// receiving packet
		
		PacketEntry entry = new PacketEntry();
		entry.setLevel1(Integer.parseInt(request.queryParams("level1")));
		entry.setLevel2(Integer.parseInt(request.queryParams("level2")));
		entry.setLevel3(Integer.parseInt(request.queryParams("level3")));
		if(request.queryParams().contains("timestamp"))
		{
			entry.setDate(new Date(Long.parseLong(request.queryParams("timestamp"))));
		}
		else
		{
			entry.setDate(new Date());
		}
		
		PacketsTable table = new PacketsTable();
		table.save(entry);
		
		JournalTable jtable = new JournalTable();
		jtable.save(entry);

		request.attribute("result", "success");
		
		return null;
	}

}
