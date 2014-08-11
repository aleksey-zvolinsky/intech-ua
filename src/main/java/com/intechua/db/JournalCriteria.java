package com.intechua.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JournalCriteria
{
	public Date dateFrom, dateTo;
	public Set<Integer> counterIds = new HashSet<Integer>();
	public String order;
}
