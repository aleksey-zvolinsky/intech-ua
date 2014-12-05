package com.intechua.db.managers;

import java.sql.SQLException;

import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.util.hsqldb.HSQLDBDSL;

import com.intechua.db.jooq.tables.Operator;
import com.intechua.db.jooq.tables.records.OperatorRecord;

public class OperatorTable extends AbstractTable
{

	public static final String USERNAME = "oper";

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
		
		initData();
	}

	public void initData()
	{
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
			
			record = new OperatorRecord();
			record.setId(9998);
			record.setUsername(USERNAME);
			record.setPassword(USERNAME);
			
			record.attach(HSQLDBDSL.using(db.getConn()).configuration());
			record.insert();
		}
	}

	public String getPassword(String user)
	{
		 Result<OperatorRecord> one = HSQLDBDSL.using(db.getConn())
				.selectFrom(Operator.OPERATOR)
				.where(Operator.OPERATOR.USERNAME.equal(user))
				.fetch();
		return one.get(0).getPassword();
	}
	
	public void update(String user, String passwd)
	{
		HSQLDBDSL.using(db.getConn())
				.update(Operator.OPERATOR)
				.set(Operator.OPERATOR.PASSWORD, passwd)
				.where(Operator.OPERATOR.USERNAME.equal(user))
				.execute();
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
