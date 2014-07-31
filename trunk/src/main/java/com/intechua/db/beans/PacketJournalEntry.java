package com.intechua.db.beans;

import java.util.Date;

public class PacketJournalEntry
{
	@Override
	public String toString()
	{
		return "PacketJournalEntry [date=" + date + ", id=" + id + ", power=" + power + ", state=" + state + ", level=" + level + ", counterId=" + counterId + "]";
	}
	
	
	Date date;
	int id;

	boolean power;
	boolean state;
	int level;
	private int counterId;
	
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public boolean isPower()
	{
		return power;
	}	
	public int getPower()
	{
		return power?1:0;
	}
	public void setPower(boolean power)
	{
		this.power = power;
	}
	public void setPower(int power)
	{
		this.power = power==1;
	}
	public boolean isState()
	{
		return state;
	}
	public int getState()
	{
		return state?1:0;
	}
	public void setState(boolean state)
	{
		this.state = state;
	}
	public void setState(int state)
	{
		this.state = state==1;
	}
	public int getLevel()
	{
		return level;
	}
	public void setLevel(int level)
	{
		this.level = level;
	}
	public int getCounterId()
	{
		return counterId;
	}	
	public void setCounterId(int counterId)
	{
		this.counterId = counterId;
	}
}
