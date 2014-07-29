package ggsmvkr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Setup
{
	private final Properties props = new Properties();
	
	private static Setup setup = new Setup();
			
	public static Setup get()
	{
		return setup;		
	}
	
	public Setup()
	{
		InputStream stream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("setup.ini");
		
		try
		{
			props.load(stream);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to load setup.ini file", e);
		}
	}
	
	public int getWebServerPort()
	{
		return Integer.parseInt(props.getProperty("web.server.port", "8050"));
	}

	public int getPacketServerPort()
	{
		return Integer.parseInt(props.getProperty("packet.server.port", "8129"));
	}
	
	
}
