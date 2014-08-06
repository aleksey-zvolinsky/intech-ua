package com.intechua.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.jooq.tables.Settings;
import com.intechua.db.jooq.tables.records.SettingsRecord;

public class SettingsTable extends AbstractTable
{

	@Override
	public void create()
	{
		try
		{
			// make an empty table
			//
			// by declaring the id column IDENTITY, the db will automatically
			// generate unique values for new rows- useful for row keys
			db.update("CREATE TABLE settings ( id INTEGER IDENTITY, name VARCHAR(256), desc VARCHAR(256), value VARCHAR(256))");
			
		}
		catch (SQLException ex2)
		{

			// ignore
			// ex2.printStackTrace(); // second time we run program
			// should throw execption since table
			// already there
			//
			// this will have no effect on the db
		}
		
		init();		
	}
	
	private void init()
	{
		Record1<Integer> one = HSQLDBDSL.using(db.getConn())
				.selectCount()
				.from(Settings.SETTINGS)
				.fetchOne();
			
		//create volume user
		if(one.getValue(0, Integer.class) != 0)
		{
			return;
		}
		
		try(InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("settings.ini"))
		{
			InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
			
			Properties properties = new Properties();
			properties.load(isr);
			
			for (Entry<Object, Object> e : properties.entrySet())
			{
				SettingsRecord record = new SettingsRecord();
				record.setName(e.getKey().toString());
				String[] value = e.getValue().toString().split(";");
				record.setValue(value[0]);
				record.setDesc(value[1]);
				
				record.attach(HSQLDBDSL.using(db.getConn()).configuration());
				record.insert();
			}

		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to read settings.ini file", e);
		}

	}

	public Result<SettingsRecord> getList()
	{
		Result<SettingsRecord> list = HSQLDBDSL.using(db.getConn())
			.selectFrom(Settings.SETTINGS)
			.fetch();
		
		return list;		
	}
	
	public String get(String name)
	{
		SettingsRecord item = HSQLDBDSL.using(db.getConn())
				.selectFrom(Settings.SETTINGS)
				.where(Settings.SETTINGS.NAME.equal(name))
				.fetchOne();
		return item.getValue();
	}
	
	public void update(String name, String value)
	{
		HSQLDBDSL.using(db.getConn())
				.update(Settings.SETTINGS)
				.set(Settings.SETTINGS.VALUE, value)
				.where(Settings.SETTINGS.NAME.equal(name))
				.execute();
	}

}
