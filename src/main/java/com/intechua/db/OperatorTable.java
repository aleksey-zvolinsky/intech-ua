package com.intechua.db;

import java.sql.SQLException;

import org.jooq.Record1;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.jooq.tables.Operator;
import com.intechua.db.jooq.tables.records.OperatorRecord;

public class OperatorTable extends AbstractTable
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
			db.update("CREATE TABLE operator ( id INTEGER IDENTITY, username VARCHAR(256), password VARCHAR(256))");
			
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
		
		Record1<Integer> one = HSQLDBDSL.using(db.getConn())
			.selectCount()
			.from(Operator.OPERATOR)
			.fetchOne();
		
		//create default user
		if(one.getValue(0, Integer.class) == 0)
		{
			OperatorRecord record = new OperatorRecord();
			record.setId(9999);
			record.setUsername("admin");
			record.setPassword("admin");
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
		}
	}

	public OperatorRecord query()
	{
		return null;		
	}

	public boolean exist(String user, String passwd)
	{
		Record1<Integer> one = HSQLDBDSL.using(db.getConn())
				.selectCount()
				.from(Operator.OPERATOR)
				.where(Operator.OPERATOR.PASSWORD.equal(passwd)
						.and(Operator.OPERATOR.USERNAME.equal(user)))
				.fetchOne();
		return one.getValue(0, Integer.class) != 0;
	}
}
