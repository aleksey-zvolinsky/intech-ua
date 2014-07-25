package com.intechua;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DateFormat;
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
		    	try(Socket clientSocket = new Socket("localhost", 8129))
		    	{
			    	DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			    	byte[] bytes = makePacket(line);
					outToServer.write(bytes);
		    	}
		    	System.out.printf("%04d: %s%n", ++lineNumber, line);
		    }
		}		
	}

	private static byte[] makePacket(String line)
	{
		int lenBuff = 8;
		byte[] newBuffRead = new byte[lenBuff];
		newBuffRead[0] = 0b01111111;
		newBuffRead[0] = 0b01111111;
		String[] values = line.split("\t");
		int number = Integer.parseInt(values[0]);
		try
		{
			Date date = df.parse(values[1]);
		} 
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
		int level1 = Integer.parseInt(values[2]);
		int level2 = Integer.parseInt(values[3]);
		int level3 = Integer.parseInt(values[4]);
		
		byte[] bytesLevel1 = ByteBuffer.allocate(4).putInt(level1).array();
		byte[] bytesLevel2 = ByteBuffer.allocate(4).putInt(level2).array();
		byte[] bytesLevel3 = ByteBuffer.allocate(4).putInt(level3).array();
		
		newBuffRead[1] = bytesLevel1[2];
		newBuffRead[2] = bytesLevel1[3];
		
		newBuffRead[3] = bytesLevel2[2];
		newBuffRead[4] = bytesLevel2[3];
		
		newBuffRead[5] = bytesLevel3[2];
		newBuffRead[6] = bytesLevel3[3];

		byte valCRC = 0;
		for (int i=0; i<lenBuff-1; i++) 
		{
			valCRC = (byte) (valCRC ^ newBuffRead[i]);
		}
		
		newBuffRead[7] = valCRC;
		
		return newBuffRead;
	}
}
