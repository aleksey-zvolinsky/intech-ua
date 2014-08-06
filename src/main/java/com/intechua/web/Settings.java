package com.intechua.web;

import org.jooq.Result;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.OperatorTable;
import com.intechua.db.PacketsTable;
import com.intechua.db.SettingsTable;
import com.intechua.db.jooq.tables.records.PacketsRecord;
import com.intechua.db.jooq.tables.records.SettingsRecord;

public class Settings extends Route
{
	private final SettingsTable settings = new SettingsTable();
	private final OperatorTable oper = new OperatorTable();
	private final PacketsTable packets = new PacketsTable();
	

	public Settings(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		save(request);
		
		for(SettingsRecord setting : settings.getList())
		{
			request.attribute(setting.getName(), setting.getValue());
		}
		
		request.attribute("password", oper.getPassword(OperatorTable.USERNAME));
		
		
		Result<PacketsRecord> items = packets.getLastPacket();
		if(items.size()>0)
		{
			PacketsRecord item = items.get(0);
			
			request.attribute("a1", item.getRawlevel1());
			request.attribute("a2", item.getRawlevel2());
			request.attribute("a3", item.getRawlevel3());
		}
		else
		{
			request.attribute("a1", 300);
			request.attribute("a2", 400);
			request.attribute("a3", 500);
		}
		
		// old page
		request.attribute("result", settings.getList());
		return null;
	}

	private void save(Request request)
	{
		for(SettingsRecord setting : settings.getList())
		{
			if(null != request.queryParams(setting.getName()))
			{
				settings.update(setting.getName(), request.queryParams(setting.getName()));
			}
		}	
		
		if(null != request.queryParams("on"))
		{
			settings.update("volume_enabled", "true");
		}
		
		if(null != request.queryParams("off"))
		{
			settings.update("volume_enabled", "false");
		}
		
		if(null != request.queryParams("password"))
		{
			oper.update(OperatorTable.USERNAME, request.queryParams("password"));
		}
	}

}
