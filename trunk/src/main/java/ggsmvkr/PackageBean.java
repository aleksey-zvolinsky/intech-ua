package ggsmvkr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class PackageBean
{
	private byte[] data;
	private final int state;
	private final int level1;
	private final int level2;
	private final int level3;
	private int crc;

	public PackageBean(byte[] data) throws IOException
	{
		this.data = data;
		
		state = bytesToInt(new byte[] {data[0]});
		
		level1 = bytesToInt(new byte[] {data[1], data[2]});
		level2 = bytesToInt(new byte[] {data[3], data[4]});
		level3 = bytesToInt(new byte[] {data[5], data[6]});
		crc = bytesToInt(new byte[] {data[7]});
	}
	
	public PackageBean(int state, int level1, int level2, int level3)
	{
		this.state = state; 
		this.level1 = level1; 
		this.level2 = level2; 
		this.level3 = level3; 
		
		data = new byte[8];
		try
		{
			data[0] = intToBytes(state)[0];
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			byte[] bytes = intToBytes(level1);
			data[1] = bytes[0];
			data[2] = bytes[1];
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			byte[] bytes = intToBytes(level2);
			data[3] = bytes[0];
			data[4] = bytes[1];
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			byte[] bytes = intToBytes(level3);
			data[5] = bytes[0];
			data[6] = bytes[1];
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		data[7] = (byte) (data[6] ^ data[5] ^ data[4] ^ data[3] ^ data[2] ^ data[1] ^ data[0]);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + crc;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + level1;
		result = prime * result + level2;
		result = prime * result + level3;
		result = prime * result + state;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackageBean other = (PackageBean) obj;
		if (crc != other.crc)
			return false;
		if (!Arrays.equals(data, other.data))
			return false;
		if (level1 != other.level1)
			return false;
		if (level2 != other.level2)
			return false;
		if (level3 != other.level3)
			return false;
		if (state != other.state)
			return false;
		return true;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	public int getCrc()
	{
		return crc;
	}

	public void setCrc(int crc)
	{
		this.crc = crc;
	}

	public int getState()
	{
		return state;
	}

	public int getLevel1()
	{
		return level1;
	}

	public int getLevel2()
	{
		return level2;
	}

	public int getLevel3()
	{
		return level3;
	}

	public byte[] intToBytes(int my_int) throws IOException
	{
		return ByteBuffer.allocate(4).putInt(my_int).array();
	}

	public int bytesToInt(byte[] int_bytes) throws IOException
	{
		return ByteBuffer.wrap(int_bytes).getInt();
	}
}
