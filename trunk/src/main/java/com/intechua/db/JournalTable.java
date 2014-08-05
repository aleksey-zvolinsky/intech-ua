package com.intechua.db;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.jooq.tables.Journal;
import com.intechua.db.jooq.tables.records.JournalRecord;
import com.intechua.db.jooq.tables.records.PacketsRecord;

public class JournalTable extends AbstractTable
{
	

	/* (non-Javadoc)
	 * @see com.intechua.db.Table#create()
	 */
	@Override
	public void create()
	{
		try
		{
			// make an empty table
			//
			// by declaring the id column IDENTITY, the db will automatically
			// generate unique values for new rows- useful for row keys
			db.update("CREATE TABLE journal ( id INTEGER IDENTITY, date TIMESTAMP, counter_id INTEGER, level INTEGER, rawlevel INTEGER, state INTEGER, power INTEGER)");
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
	
	public void save(JournalRecord entry)
	{

		HSQLDBDSL.using(db.getConn())
			.insertInto(Journal.JOURNAL)
			.values(entry)
			.execute();
	}
	
	public void save(PacketsRecord entry)
	{
		if(entry.getState() == 2)//no power
		{
			JournalRecord record = new JournalRecord(); 
			
			record.setCounterId(-1);
			record.setDate(entry.getDate());
			record.setState(200);
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
		}
		if(entry.getState() == 1)//ok
		{
			JournalRecord record = new JournalRecord(); 
		
			record.setCounterId(1);
			record.setDate(entry.getDate());
			record.setRawlevel(entry.getRawlevel1());
			record.setLevel(entry.getLevel1());
			record.setPower(entry.getRawlevel1()>200?1:0);
			//TODO Where is this state
			record.setState(100);
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
			
			record = new JournalRecord(); 
			
			record.setCounterId(2);
			record.setDate(entry.getDate());
			record.setRawlevel(entry.getRawlevel2());
			record.setLevel(entry.getLevel2());
			record.setPower(entry.getRawlevel2()>200?1:0);
			//TODO Where is this state
			record.setState(100);
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
		
			
			record = new JournalRecord(); 
			
			record.setCounterId(3);
			record.setDate(entry.getDate());
			record.setRawlevel(entry.getRawlevel3());
			record.setLevel(entry.getLevel3());
			record.setPower(entry.getRawlevel3()>200?1:0);
			//TODO Where is this state
			record.setState(100);
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
		}
	}
	
	public Result<JournalRecord> query(JournalCriteria crit)
	{
		SelectConditionStep<JournalRecord> q = HSQLDBDSL.using(db.getConn())
			.selectFrom(Journal.JOURNAL)
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
		
		Result<JournalRecord> result = q.fetch(); 
		
		return result; 
	}
}
