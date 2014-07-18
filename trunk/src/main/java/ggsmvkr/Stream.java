package ggsmvkr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Stream
{

	static byte[] welcome = new byte[]
	{ (byte) 103, (byte) 103, (byte) 115, (byte) 109, (byte) 107, (byte) 114 };
	Socket socket;
	InputStream is;
	OutputStream os;
	Logger logger;

	public Stream(Socket socket) throws IOException
	{
		socket.setTcpNoDelay(true);
		this.socket = socket;
		this.is = socket.getInputStream();
		this.os = socket.getOutputStream();
		this.logger = new Logger(socket.getRemoteSocketAddress().toString());
	}

	void close() throws IOException
	{
		this.socket.close();
	}

	void read(byte[] buff)
	{
		boolean readed = true;

		int readed1;
		try
		{
			readed1 = this.is.read(buff);
		} catch (SocketTimeoutException var4)
		{
			throw new StreamException("Read timeout: " + var4.getMessage());
		} catch (IOException var5)
		{
			throw new StreamException("Can\'t read: " + var5.getMessage());
		}

		if (readed1 != buff.length)
		{
			throw new StreamException("Can\'t read " + buff.length + " bytes ("
					+ readed1 + " readed)");
		}
	}

	byte read()
	{
		int readed;
		try
		{
			readed = this.is.read();
		} catch (SocketTimeoutException var3)
		{
			throw new StreamException("Read timeout: " + var3.getMessage());
		} catch (IOException var4)
		{
			throw new StreamException("Can\'t read: " + var4.getMessage());
		}

		if (readed == -1)
		{
			throw new StreamException("Can\'t read 1 byte");
		} else
		{
			return (byte) readed;
		}
	}

	private void write(byte buff)
	{
		try
		{
			this.os.write(buff);
			this.os.flush();
		} catch (IOException var3)
		{
			throw new StreamException("Can\'t write: " + var3.getMessage());
		}
	}

	private void write(byte[] buff)
	{
		try
		{
			this.os.write(buff);
			this.os.flush();
		} catch (IOException var3)
		{
			throw new StreamException("Can\'t write: " + var3.getMessage());
		}
	}

	void writeWelcome()
	{
		this.write(welcome);
	}

	boolean readAndCheckWelcome()
	{
		byte[] buff = new byte[6];
		this.read(buff);
		this.logger.beginLog("Welcome received: ");
		if (Arrays.equals(buff, welcome))
		{
			this.logger.endLog("OK");
			return true;
		} else
		{
			this.logger.endLog("BAD");
			return false;
		}
	}

	void writeVersion(int[] version)
	{
		byte[] buff = new byte[]
		{ (byte) version[0], (byte) version[1] };
		this.write(buff);
	}

	int[] readVersion()
	{
		int[] version = new int[2];
		byte[] buff = new byte[2];
		this.read(buff);
		version[0] = buff[0];
		version[1] = buff[1];
		return version;
	}

	void writeId(int id)
	{
		this.write((byte) id);
	}

	int readId()
	{
		byte id = this.read();
		return id;
	}

	void writeAuth(byte[] auth)
	{
		this.write(auth);
	}

	byte[] readAuth()
	{
		byte[] auth = new byte[16];
		this.read(auth);
		return auth;
	}

	void makeAndWriteAuth(int id, byte[] salt)
	{
		byte[] data = makeAuthData(id, makePassword(id), salt);
		byte[] auth = makeAuth(data);
		this.write(auth);
	}

	boolean readAndCheckAuth(int id, byte[] salt)
	{
		byte[] data = makeAuthData(id, makePassword(id), salt);
		byte[] auth = makeAuth(data);
		byte[] answer = new byte[16];
		this.read(answer);
		return Arrays.equals(auth, answer);
	}

	private static byte[] makeAuth(byte[] data)
	{
		try
		{
			MessageDigest e = MessageDigest.getInstance("MD5");
			return e.digest(data);
		} catch (NoSuchAlgorithmException var2)
		{
			throw new StreamException("Can\'t make hash", var2);
		}
	}

	private static byte[] makeAuthData(int id, String password, byte[] salt)
	{
		byte[] idbb = String.format("%02x", new Object[]
		{ Integer.valueOf(id) }).getBytes();
		byte[] passwd = password.getBytes();
		byte[] data = new byte[idbb.length + passwd.length + salt.length];
		System.arraycopy(idbb, 0, data, 0, idbb.length);
		System.arraycopy(passwd, 0, data, idbb.length, passwd.length);
		System.arraycopy(salt, 0, data, idbb.length + passwd.length,
				salt.length);
		return data;
	}

	private static String makePassword(int id)
	{
		return "gGsK" + String.format("%02x", new Object[]
		{ Integer.valueOf(id) });
	}

	void writeSalt(byte[] salt)
	{
		this.write((byte) salt.length);
		this.write(salt);
	}

	byte[] readSalt()
	{
		byte len = this.read();
		byte[] salt = new byte[len];
		this.read(salt);
		return salt;
	}

	List readParameters()
	{
		byte cnt = this.read();
		this.logger.log("Parameters count " + cnt);
		ArrayList parameters = new ArrayList();

		for (byte i = 0; i < cnt; ++i)
		{
			parameters.add(ParameterValue.fromStream(this));
		}

		return parameters;
	}

	List readRegisters()
	{
		byte cnt = this.read();
		this.logger.log("Registers count " + cnt);
		ArrayList registers = new ArrayList();

		for (byte i = 0; i < cnt; ++i)
		{
			byte idd = this.read();
			Register reg = Register.byId((byte) (idd & 63));
			byte[] buff = new byte[2];
			this.read(buff);
			int val = buff[1];
			if (val < 0)
			{
				val += 256;
			}

			val *= 256;
			val += buff[0];
			if (buff[0] < 0)
			{
				val += 256;
			}

			int pump = idd >> 6;
			if (pump < 0)
			{
				pump += 4;
			}

			this.logger.log("Register " + reg + "[" + pump + "]=" + val + "("
					+ Integer.toBinaryString(val) + ")");
			registers.add(new RegisterValue(reg, pump, val));
		}

		return registers;
	}

	void writeCommand(Command cmd)
	{
		this.write(cmd.id());
	}

	Command readCommand()
	{
		byte cmdId = this.read();
		Command cmd = Command.forId(cmdId);
		if (cmd == null)
		{
			throw new StreamException("Invalid command for byte "
					+ String.format("%02x", new Object[]
					{ Byte.valueOf(cmdId) }));
		} else
		{
			return cmd;
		}
	}

	static String getHexString(byte[] data)
	{
		return String.format("%0" + data.length * 2 + "x", new Object[]
		{ new BigInteger(1, data) });
	}

}
