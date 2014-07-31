package com.intechua.db;

import ggsmvkr.Server;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.HDatabase;
import com.intechua.db.beans.PacketEntry;
import com.intechua.db.beans.PacketJournalEntry;
import com.intechua.db.jooq.tables.Journal;

public class JournalTable
{
	private final HDatabase db;
	private static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public JournalTable()
	{
		this.db = Server.getHdb();
	}
	
	public void save(PacketJournalEntry entry)
	{
		try
		{

			// make an empty table
			//
			// by declaring the id column IDENTITY, the db will automatically
			// generate unique values for new rows- useful for row keys
			db.update("CREATE TABLE journal ( id INTEGER IDENTITY, date TIMESTAMP, counter_id INTEGER, level INTEGER, state INTEGER, power INTEGER)");
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

		try
		{
			db.update(MessageFormat.format("INSERT INTO journal(date, counter_id, level, state, power) VALUES(TO_DATE(''{0}'', ''DD.MM.YYYY HH:MI:SS''), {1}, {2}, {3}, {4})", 
					df.format(entry.getDate()), entry.getCounterId(), entry.getLevel(), entry.getState(), entry.getPower()));
		}
		catch (SQLException ex3)
		{
			ex3.printStackTrace();
		}
	}

	public void save(PacketEntry entry)
	{
		PacketJournalEntry pje = new PacketJournalEntry();
		pje.setCounterId(1);
		pje.setDate(entry.getDate());
		pje.setLevel(entry.getLevel1());
		pje.setPower(entry.isPower1());
		pje.setState(entry.isFlowmeterState1());
		save(pje);
		
		pje = new PacketJournalEntry();
		pje.setCounterId(2);
		pje.setDate(entry.getDate());
		pje.setLevel(entry.getLevel2());
		pje.setPower(entry.isPower2());
		pje.setState(entry.isFlowmeterState2());
		save(pje);
		
		pje = new PacketJournalEntry();
		pje.setCounterId(3);
		pje.setDate(entry.getDate());
		pje.setLevel(entry.getLevel3());
		pje.setPower(entry.isPower3());
		pje.setState(entry.isFlowmeterState3());
		save(pje);
	}
	
	public List<PacketJournalEntry> query(PacketJournalCriteria crit)
	{
		PacketJournalEntry entry = null;
		

		SelectConditionStep<Record> q = HSQLDBDSL.using(db.getConn())
			.select()
			.from(Journal.JOURNAL)
			.where("1=1");
		
		if(null != crit.dateFrom)
		{
			q = q.and(Journal.JOURNAL.DATE.greaterOrEqual(new Timestamp(crit.dateFrom.getTime())));
		}
		if(null != crit.dateTo)
		{
			q = q.and(Journal.JOURNAL.DATE.lessOrEqual(new Timestamp(crit.dateTo.getTime())));
		}
		
		Condition cond = null;
		for(Integer id : crit.counterIds)
		{
			if(null == cond)
			{
				cond = Journal.JOURNAL.COUNTER_ID.equal(id);
			}
			else
			{
				cond = cond.or(Journal.JOURNAL.COUNTER_ID.equal(id));
			}
		}
		if(null != cond)
		{
			q.and(cond);
		}
		
		Result<Record> result = q.fetch(); 

		List<PacketJournalEntry> list = new ArrayList<PacketJournalEntry>(result.size());
		for (Record r : result)
		{
			entry = new PacketJournalEntry();
			
			entry.setDate(r.getValue(Journal.JOURNAL.DATE, Date.class));
			entry.setCounterId(r.getValue(Journal.JOURNAL.COUNTER_ID, Integer.class));
			entry.setLevel(r.getValue(Journal.JOURNAL.LEVEL, Integer.class));
			entry.setState(r.getValue(Journal.JOURNAL.STATE, Integer.class));
			entry.setPower(r.getValue(Journal.JOURNAL.POWER, Integer.class));
			list.add(entry);
		}
		
		
		return list; 
	}
}
