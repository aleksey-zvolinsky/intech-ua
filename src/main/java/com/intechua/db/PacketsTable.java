package com.intechua.db;

import java.sql.SQLException;

import org.jooq.Result;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.jooq.tables.Packets;
import com.intechua.db.jooq.tables.records.PacketsRecord;


public class PacketsTable extends AbstractTable
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
			db.update("CREATE TABLE packets ( id INTEGER IDENTITY, date TIMESTAMP, modemid INTEGER, level1 INTEGER, level2 INTEGER, level3 INTEGER, rawlevel1 INTEGER, rawlevel2 INTEGER, rawlevel3 INTEGER, state INTEGER, connection_level INTEGER, power INTEGER, B1 varchar(256))");
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
	}
	
	public void save(PacketsRecord entry)
	{
		HSQLDBDSL.using(db.getConn())
			.insertInto(Packets.PACKETS)
			.set(entry)
			.execute();
	}
	
	public  Result<PacketsRecord> getLastPacket()
	{		
		Result<PacketsRecord> result = HSQLDBDSL.using(db.getConn())
			.selectFrom(Packets.PACKETS)
			.orderBy(Packets.PACKETS.DATE.desc())
			.limit(1)
			.fetch();
		
		return result; 
	}
		
}
