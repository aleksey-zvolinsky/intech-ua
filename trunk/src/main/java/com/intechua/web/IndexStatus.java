package com.intechua.web;

import org.apache.log4j.Logger;
import org.jooq.Result;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.PacketsTable;
import com.intechua.db.SettingsTable;
import com.intechua.db.jooq.tables.records.PacketsRecord;

public class IndexStatus extends Route
{
	private final static Logger LOG = Logger.getLogger(IndexStatus.class);
	private final PacketsTable table = new PacketsTable();
	private final SettingsTable settings = new SettingsTable();

	public IndexStatus(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		try
		{
			
			Result<PacketsRecord> items = table.getLastPacket();
			
			request.attribute("packet", items.formatJSON());
			request.attribute("volume_enabled", settings.get("volume_enabled"));
		}
		catch(Throwable th)
		{
			LOG.error("Failed to get index status", th);
		}

		return null;
	}

}
