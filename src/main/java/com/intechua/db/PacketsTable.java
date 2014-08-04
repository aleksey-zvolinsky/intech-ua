package com.intechua.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jooq.Record;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.beans.PacketEntry;
import com.intechua.db.jooq.tables.Packets;


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
			db.update("CREATE TABLE packets ( id INTEGER IDENTITY, date TIMESTAMP, level1 INTEGER, level2 INTEGER, level3 INTEGER)");
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
	
	public void save(PacketEntry entry)
	{

		HSQLDBDSL.using(db.getConn())
			.insertInto(Packets.PACKETS, 
					Packets.PACKETS.DATE, Packets.PACKETS.LEVEL1, Packets.PACKETS.LEVEL2, Packets.PACKETS.LEVEL3)
			.values(new Timestamp(entry.getDate().getTime()), entry.getLevel1(), entry.getLevel2(), entry.getLevel3())
			.execute();

	}
	
	public PacketEntry getLastPacket()
	{		
		Record record = HSQLDBDSL.using(db.getConn())
			.select()
			.from(Packets.PACKETS)
			.orderBy(Packets.PACKETS.DATE.desc())
			.limit(1)
			.fetchOne();
		
		return currentToEntry(record); 
	}
	
	public List<PacketEntry> query(PacketCriteria criteria)
	{
		Statement st = null;
		ResultSet rs = null;
		PacketEntry entry = null;
		List<PacketEntry> list = new ArrayList<PacketEntry>(1000);

		try
		{
			st = db.getConn().createStatement();
			// statement objects can be reused with

			// repeated calls to execute but we
			// choose to make a new one each time
			rs = st.executeQuery("select date, level1, level2, level3 from packets order by date"); // run the query
	
			// do something with the result set.

			for (; rs.next();)
			{
				entry = currentToEntry(rs);
				list.add(entry);
			}
			
			st.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list; 
	}

	private PacketEntry currentToEntry(ResultSet rs) throws SQLException
	{
		PacketEntry entry = new PacketEntry();
		entry.setDate(rs.getDate("date"));
		entry.setLevel1(rs.getInt("level1"));
		entry.setLevel2(rs.getInt("level2"));
		entry.setLevel3(rs.getInt("level3"));
		return entry;
	}
	
	private PacketEntry currentToEntry(Record r)
	{
		
		PacketEntry entry = new PacketEntry();
		entry.setDate(r.getValue(Packets.PACKETS.DATE, Date.class));
		entry.setLevel1(r.getValue(Packets.PACKETS.LEVEL1, Integer.class));
		entry.setLevel2(r.getValue(Packets.PACKETS.LEVEL2, Integer.class));
		entry.setLevel3(r.getValue(Packets.PACKETS.LEVEL3, Integer.class));
		return entry;
	}

	
}
