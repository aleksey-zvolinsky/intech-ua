package com.intechua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FillData
{
	// 02.01.2014 00:00:00
	public static DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
	
	public static void main(String[] args) throws IOException
	{
		//String fileName = "sample.txt";
		String fileName = "24hours.txt";
		try(InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName))
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
		    int lineNumber = 0;
		    br.readLine();// skip first line with headers
		    while ((line = br.readLine()) != null) 
		    {
		    	URL url = new URL("http://localhost:8050/input?" + makeParams(line));
		    	try(InputStream webstream = url.openStream())
		    	{
		    		
		    	}
		    	
		    	System.out.printf("%04d: %s%n", ++lineNumber, line);
		    }
		}		
	}

	private static String makeParams(String line)
	{
		String[] values = line.split("\t");
		int number = Integer.parseInt(values[0]);
		Date date = null;
		try
		{
			date = df.parse(values[1]);
		} 
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
		int level1 = Integer.parseInt(values[2]);
		int level2 = Integer.parseInt(values[3]);
		int level3 = Integer.parseInt(values[4]);
		return MessageFormat.format("level1={0}&level2={1}&level3={2}&timestamp=" + date.getTime(), level1, level2, level3);
	}

}