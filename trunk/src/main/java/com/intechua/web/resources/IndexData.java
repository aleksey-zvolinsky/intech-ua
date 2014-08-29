package com.intechua.web.resources;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.jooq.Result;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.jooq.tables.records.JournalRecord;
import com.intechua.db.managers.JournalCriteria;
import com.intechua.db.managers.JournalTable;

public class IndexData extends Route
{
	private final static Logger LOG = Logger.getLogger(IndexData.class);
	
	public IndexData(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		try
		{
			JournalTable table = new JournalTable();
			JournalCriteria crit = new JournalCriteria();
			crit.order = "asc";
			
			Date date = new Date();
			crit.dateFrom = DateUtils.addHours(date, -6);
			crit.dateTo = date;
			
			for(int i = 1; i <= 3; i++)
			{
				crit.counterIds.clear();
				crit.counterIds.add(i);
				Result<JournalRecord> res = table.query(crit);
				
				request.attribute("list"+i, res.formatJSON());
			}
		}
		catch(Throwable th)
		{
			LOG.error("Failed to get index data", th);
		}
		return null;
	}
}
