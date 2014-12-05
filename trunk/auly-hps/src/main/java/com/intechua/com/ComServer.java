package com.intechua.com;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.log4j.Logger;

public class ComServer
{
	private final static Logger LOG = Logger.getLogger(ComServer.class);
	private SerialPort serialPort;

	public void start()
	{
		// Передаём в конструктор имя порта
		serialPort = new SerialPort("COM1");
		try
		{
			// Открываем порт
			serialPort.openPort();
			// Выставляем параметры
			serialPort.setParams(
					SerialPort.BAUDRATE_9600, 
					SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_1, 
					SerialPort.PARITY_NONE);
			// Включаем аппаратное управление потоком
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			// Устанавливаем ивент лисенер и маску
			serialPort.addEventListener(new ComPortReader(serialPort), SerialPort.MASK_RXCHAR);
			// Отправляем запрос устройству
			serialPort.writeString("Get data");
		}
		catch (SerialPortException e)
		{
			LOG.error("Failed to open port", e);
		}
	}

	public void stop()
	{
		try
		{
			serialPort.closePort();
		}
		catch (SerialPortException e)
		{
			LOG.error("Failed to close port", e);
		}
	}
}
