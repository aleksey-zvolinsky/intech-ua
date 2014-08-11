package com.intechua.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jooq.Result;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.JournalCriteria;
import com.intechua.db.JournalTable;
import com.intechua.db.jooq.tables.records.JournalRecord;

public class Graph extends Route
{
	private final JournalTable journal = new JournalTable();

	public Graph(String path)
	{
		super(path);
	}
	
	@Override
	public Object handle(Request request, Response response)
	{
		SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

		JournalCriteria crit = new JournalCriteria();
		crit.order = "asc";
		
		if(null != request.queryParams("fixedPeriod"))
		{
			
			switch (request.queryParams("fixedPeriod"))
			{
			case "hour":
				crit.dateFrom = DateUtils.addHours(new Date(), -6);
				break;
			case "day":
				crit.dateFrom = DateUtils.addDays(new Date(), -1);
				break;
			case "week":
				crit.dateFrom = DateUtils.addWeeks(new Date(), -1);
				break;
			case "month":
				crit.dateFrom = DateUtils.addMonths(new Date(), -1);
				break;
			case "year":
				crit.dateFrom = DateUtils.addYears(new Date(), -1);
				break;

			default:
				break;
			}
			
			crit.dateTo = new Date();
		}
		else
		{
			SimpleDateFormat df = null;
			String date = null;
			
			if(request.queryParams("dateFrom") != null)
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
			
			if(request.queryParams("dateTo") != null)
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
		}
		
		if(null == crit.dateFrom && null == crit.dateTo)
		{
			try
			{
				crit.dateFrom = DF.parse(DF.format(new Date()));
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		
		crit.counterIds.add(Integer.parseInt(request.queryParams("counter")));
		
		Result<JournalRecord> result = journal.query(crit);
		request.attribute("result", result.formatJSON());
		
		for(String param: request.queryParams())
		{
			request.attribute(param, request.queryParams(param));
		}
		
		return null;
	}

}
