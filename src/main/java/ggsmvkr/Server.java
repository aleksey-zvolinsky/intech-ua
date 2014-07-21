package ggsmvkr;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

public class Server
{
	static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	static DB db;
	static RRDs rrds;
	static Processor processor;

	public static void beginLog(String message)
	{
		Date cdate = new Date();
		System.out.print(df.format(cdate) + " " + message);
		System.out.flush();
	}

	public static void continueLog(String message)
	{
		System.out.print(message);
		System.out.flush();
	}

	public static void endLog(String message)
	{
		System.out.println(message);
	}

	public static void log(String message)
	{
		beginLog(message);
		endLog("");
	}

	public static void log(int id, String message)
	{
		log("[" + id + "] " + message);
	}

	public static void main(String[] argv) throws Exception
	{
		boolean noauth = false;
		boolean debug = false;

		for (String arg : argv)
		{
			if (arg.equals("-noauth"))
			{
				noauth = true;
			} 
			else if (arg.equals("-debug"))
			{
				debug = true;
			} 
			else
			{
				System.out.println("Invalid option '" + arg + "'");
				return;
			}
		}
		db = new DB("db");
		rrds = new RRDs(db);

		processor = new Processor(db, rrds);
		if (!debug)
		{
			List<org.apache.log4j.Logger> loggers = Collections.list(LogManager
					.getCurrentLoggers());
			loggers.add(LogManager.getRootLogger());
			for (org.apache.log4j.Logger logger : loggers)
			{
				logger.setLevel(Level.OFF);
			}
			System.setOut(new PrintStream(new OutputStream()
			{
				@Override
				public void write(int b)
				{
				}
			}));
		}
		Face.init(db, processor, rrds, noauth);

		beginLog("Starting server:");
		ServerSocket welcomeSocket;
		try
		{
			welcomeSocket = new ServerSocket(8129);
			endLog("OK");
		} catch (BindException e)
		{
			endLog("Fail (" + e.getMessage() + ")");
			return;
		}
		for (;;)
		{
			log("Waiting connection");
			Socket connectionSocket = welcomeSocket.accept();
			beginLog("Connection from "
					+ connectionSocket.getRemoteSocketAddress() + ":");
			Stream ss;
			try
			{
				connectionSocket.setSoTimeout(240000);

				ss = new Stream(connectionSocket);
				new Communicator(ss).start();
				endLog("OK");
			} catch (Exception e)
			{
				endLog("Fail (" + e.getMessage() + ")");
			}
			continue;

			// FIXME
			// new Communicator(ss).start();
		}
	}

	static class Communicator extends Thread
	{
		Stream ss;
		int idPacket;
		Communicator(Stream _ss)
		{
			this.ss = _ss;
		}

		@Override
		public void run()
		{
			int lenBuff = 8;
			byte[] newBuffRead = new byte[lenBuff];
			byte   newByteRead;
			byte   valCRC=0;
			StringBuilder sb = new StringBuilder();
			try
			{
				//TODO how
				//byte[] = this.ss.readPacket();
				//verify CRC
				this.ss.logger.beginLog("Reading packet: ");
				for (int i=0; i<lenBuff; i++) {
					newBuffRead[i] = this.ss.read();	
					sb.append(String.format("%02X", newBuffRead[i]));
					if (i != (lenBuff-1)) valCRC = (byte) (valCRC ^ newBuffRead[i]);
				}
				sb.append(String.format("CRC=%02X", valCRC));
				this.ss.logger.log(": " + sb.toString() + ": " + String.format("CRC=%02X", valCRC));
				
				idPacket++;
				//String strEntry = new String(newBuffRead); 

				this.ss.logger.log( "puPacket: idPacket=" + idPacket + " strEntry=:" + sb  + ":");
				db.putPacket(idPacket, sb.toString());
				this.ss.logger.log("getPacket: "+ db.getPacketString(0));
				
				if (valCRC == newBuffRead[lenBuff-1])
				{// compared CRC
					this.ss.logger.endLog("OK");
				} else
				{// NOT compared CRC
					this.ss.logger.endLog("CRC Error");
				}
				db.getPacketString(0);
				//TODO how
				
				if (this.ss.readAndCheckWelcome())
				{
					int[] version = this.ss.readVersion();
					this.ss.logger.log("Incoming");
					byte[] salt = Server.makeSalt();
					this.ss.writeSalt(salt);
					this.ss.logger.log("Salt sended");
					int id = this.ss.readId();
					this.ss.logger.log("Client Id=" + id);
					this.ss.logger.beginLog("Authenticating: ");
					if (this.ss.readAndCheckAuth(id, salt))
					{
						this.ss.logger.endLog("OK");
						this.ss.writeCommand(Command.Ok);
						this.ss.logger.log("OK");
						List<ParameterValue> params = this.ss.readParameters();
						Server.processor.onConnect(id, params);
						try
						{
							mainLoop(this.ss, id);
						}
						finally
						{
							Server.processor.onDisconnect(id);
						}
					} else
					{
						this.ss.logger.endLog("Fail");
						this.ss.writeCommand(Command.Fail);
						this.ss.logger.log("OK");
					}
				}
			} catch (StreamException e)
			{
				this.ss.logger.log(e.getMessage());
			}
			this.ss.logger.beginLog("Closing connection: ");
			try
			{
				this.ss.close();
				this.ss.logger.endLog("OK");
			} catch (Exception e)
			{
				this.ss.logger.endLog("Fail (" + e.getMessage() + ")");
			}
		}

		void mainLoop(Stream ss, int id)
		{
			boolean done = false;
			while (!done)
			{
				Server.log(id, "Waiting command");
				Command cmd = ss.readCommand();
				Server.log(id, "Received " + cmd);
				// FIXME
				// switch (Server.2.$SwitchMap$ggsmvkr$Command[cmd.ordinal()])
				switch (cmd.ordinal())
				{
				case 1:
					List<RegisterValue> regs = ss.readRegisters();

					Server.processor.putRegisters(id, regs);
					ss.writeCommand(Command.Ok);
					break;
				case 2:
					ss.writeCommand(Command.Ok);
					done = true;
				}
			}
		}
	}

	static byte[] makeSalt()
	{
		byte[] salt = new byte[4];
		for (int i = 0; i < salt.length; i++)
		{
			salt[i] = ((byte) (int) Math.round(255.0D * Math.random()));
		}
		return salt;
	}
}
