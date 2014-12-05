package com.intechua.com;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.apache.log4j.Logger;

public class ComPortReader implements SerialPortEventListener
{
	private final static Logger LOG = Logger.getLogger(ComPortReader.class);
	
	private final SerialPort serialPort;

	public ComPortReader(SerialPort serialPort)
	{
		this.serialPort = serialPort;
	}

	@Override
	public void serialEvent(SerialPortEvent event)
	{
		if (event.isRXCHAR() && event.getEventValue() > 0)
		{
			LOG.debug("Received packed");
			try
			{
				// Получаем ответ от устройства, обрабатываем данные и т.д.
				
				String data = serialPort.readString(event.getEventValue());
				LOG.debug("Received packed data is : " + data);
				
				// И снова отправляем запрос
				serialPort.writeString("Get data");
			}
			catch (SerialPortException ex)
			{
				System.out.println(ex);
			}
		}

	}

}
