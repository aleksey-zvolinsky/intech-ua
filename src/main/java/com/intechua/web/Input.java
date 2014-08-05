package com.intechua.web;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import spark.Route;

import com.intechua.db.JournalTable;
import com.intechua.db.PacketsTable;
import com.intechua.db.jooq.tables.records.PacketsRecord;

public class Input extends Route
{
	private final PacketsTable packetsTable = new PacketsTable();
	private final JournalTable journalTable = new JournalTable();
	private final static Logger LOG = Logger.getLogger(Input.class);
	
	public Input(String path)
	{
		super(path);
	}

	@Override
	public Object handle(Request request, Response response)
	{
		try
		{
			// receiving packet
			PacketsRecord entry = new PacketsRecord();
			
			String connectionLevel = (request.attribute("connection_level") == null) ? request.queryParams("connection_level") : "" + request.attribute("connection_level");
			entry.setConnectionLevel(Integer.parseInt(connectionLevel));
			entry.setB1("" + request.attribute("b1"));
			
			Boolean isLostConnection = Boolean.parseBoolean((request.attribute("lost_connection") == null) ? request.queryParams("lost_connection") : "false" + request.attribute("connection_level"));
			
			if(isLostConnection)
			{
				entry.setState(200);
			}
			else
			{
				entry.setState(100);
				
			
				String level1 = (request.attribute("level1") == null) ? request.queryParams("level1") : "" + request.attribute("level1");
				String level2 = (request.attribute("level2") == null) ? request.queryParams("level2") : "" + request.attribute("level2");
				String level3 = (request.attribute("level3") == null) ? request.queryParams("level3") : "" + request.attribute("level3");
	
				entry.setLevel1(Integer.parseInt(level1));
				entry.setLevel2(Integer.parseInt(level2));
				entry.setLevel3(Integer.parseInt(level3));
				
				String rawlevel1 = (request.attribute("rawlevel1") == null) ? request.queryParams("rawlevel1") : "" + request.attribute("rawlevel1");
				String rawlevel2 = (request.attribute("rawlevel2") == null) ? request.queryParams("rawlevel2") : "" + request.attribute("rawlevel2");
				String rawlevel3 = (request.attribute("rawlevel3") == null) ? request.queryParams("rawlevel3") : "" + request.attribute("rawlevel3");
				
				entry.setRawlevel1(Integer.parseInt(rawlevel1));
				entry.setRawlevel2(Integer.parseInt(rawlevel2));
				entry.setRawlevel3(Integer.parseInt(rawlevel3));
	
			}
			
			
			// for debug purposes can set time
			if(request.queryParams().contains("timestamp"))
			{
				entry.setDate(new Timestamp(Long.parseLong(request.queryParams("timestamp"))));
			}
			else
			{
				Date date = new Date();
				entry.setDate(new Timestamp(date.getTime()));
			}
			
			String modemid = (request.attribute("modemid") == null) ? request.queryParams("modemid") : "" + request.attribute("modemid");
			entry.setModemid(Integer.parseInt(modemid));
			
			String power = (request.attribute("power") == null) ? request.queryParams("power") : "" + request.attribute("power");
			entry.setPower(Integer.parseInt(power));
	
			packetsTable.save(entry);		
			journalTable.save(entry);
			 
			request.attribute("result", (request.attribute("crc") == null) ? "success" : request.attribute("crc"));
		}
		catch(Throwable th)
		{
			LOG.error("Failed to input data", th);
		}
		
		return null;
	}

}
