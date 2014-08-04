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
	public Input(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		// receiving packet
		PacketEntry entry = new PacketEntry();
		
		String connectionLevel = (request.attribute("connection_level") == null) ? request.queryParams("connection_level") : "" + request.attribute("connection_level");
		entry.setConnectionLevel(Integer.parseInt(connectionLevel));
		
		Boolean isLostConnection = Boolean.parseBoolean((request.attribute("lost_connection") == null) ? request.queryParams("lost_connection") : "false" + request.attribute("connection_level"));
		entry.setLostConnection(isLostConnection);
		if(isLostConnection)
		{
			
		}
		else
		{
		
			String level1 = (request.attribute("level1") == null) ? request.queryParams("level1") : "" + request.attribute("level1");
			String level2 = (request.attribute("level2") == null) ? request.queryParams("level2") : "" + request.attribute("level2");
			String level3 = (request.attribute("level3") == null) ? request.queryParams("level3") : "" + request.attribute("level3");
			
			String isPower = (request.attribute("power") == null) ? request.queryParams("power") : "false" + request.attribute("power");
			
			entry.setLevel1(Integer.parseInt(level1));
			entry.setLevel2(Integer.parseInt(level2));
			entry.setLevel3(Integer.parseInt(level3));
			entry.setSensorPower(Boolean.parseBoolean(isPower));
		}
		
		
		// for debug purposes can set time
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

		 
		request.attribute("result", (request.attribute("crc") == null) ? "success" : request.attribute("crc"));
		
		return null;
	}

}
