package com.intechua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.jooq.tools.StringUtils;

public class FillHexData
{
	public static void main(String[] args) throws IOException, InterruptedException
	{

		String[] fileNames = {"sampledata.txt"};
		
		for(int i=0; i<fileNames.length; i++)
		{
			String fileName = fileNames[i];
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
			    	
			    	System.out.printf("%06d: %s%n", ++lineNumber, line);
			    	
			    	Thread.sleep(100000);
			    }
			}
		}
	}

	private static String makeParams(String line)
	{
		//		B1-1	B1-2	B2-B3	B4-B5	B6-B7	B2-B3 (HEX)	B4-B5 (HEX)	B6-B7 (HEX)
		//	1	1	1	608	403	467	260	192	1D2
		StringBuffer sb = new StringBuffer();
		String[] values = line.split("\t");
		//int num = Integer.parseInt(values[0]);
		sb.append("ID=1&CSQ=22&B1=");
		int crc = Integer.parseInt("000100" + values[2]+values[1], 2);
		sb.append(Integer.toHexString(Integer.parseInt("000100" + values[2]+values[1], 2)));
		
		
		
		String formated = StringUtils.leftPad(values[6], 4, "0");
		
		sb.append("&B2=" + formated.charAt(0)+formated.charAt(1));
		sb.append("&B3=" + formated.charAt(2)+formated.charAt(3));
		crc = crc ^ Integer.parseInt("" + formated.charAt(0)+formated.charAt(1), 16);
		crc = crc ^ Integer.parseInt("" + formated.charAt(2)+formated.charAt(3), 16);
		
		formated = StringUtils.leftPad(values[7], 4, "0");
		
		sb.append("&B4=" + formated.charAt(0)+formated.charAt(1));
		sb.append("&B5=" + formated.charAt(2)+formated.charAt(3));
		
		crc = crc ^ Integer.parseInt("" + formated.charAt(0)+formated.charAt(1), 16);
		crc = crc ^ Integer.parseInt("" + formated.charAt(2)+formated.charAt(3), 16);
		
		formated = StringUtils.leftPad(values[8], 4, "0");
		
		sb.append("&B6=" + formated.charAt(0)+formated.charAt(1));
		sb.append("&B7=" + formated.charAt(2)+formated.charAt(3));
		
		crc = crc ^ Integer.parseInt("" + formated.charAt(0)+formated.charAt(1), 16);
		crc = crc ^ Integer.parseInt("" + formated.charAt(2)+formated.charAt(3), 16);
		
		sb.append("&B8=" + Integer.toHexString(crc));
		
		return sb.toString();
	}
}
