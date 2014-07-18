package ggsmvkr;

import java.io.IOException;

import org.rrd4j.core.RrdBackend;
import org.rrd4j.core.RrdBackendFactory;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;

public class RrdBerkeleyDbBackendFactory extends RrdBackendFactory
{
	public static final String NAME = "BERKELEY";
	Database rrdDatabase;

	public RrdBerkeleyDbBackendFactory(Database rrdDatabase)
	{
		this.rrdDatabase = rrdDatabase;
	}

	@Override
	protected RrdBackend open(String path, boolean readOnly) throws IOException
	{
		return new RrdBerkeleyDbBackend(path);
	}

	public void delete(String path)
	{
		DatabaseEntry keyEntry = makeKeyEntry(path);
		OperationStatus status = this.rrdDatabase.delete(null, keyEntry);
	}

	@Override
	public boolean exists(String path) throws IOException
	{
		DatabaseEntry keyEntry = makeKeyEntry(path);
		DatabaseEntry dataEntry = new DatabaseEntry();
		return this.rrdDatabase.get(null, keyEntry, dataEntry, null) == OperationStatus.SUCCESS;
	}

	@Override
	protected boolean shouldValidateHeader(String path)
	{
		return true;
	}

	public class RrdBerkeleyDbBackend extends RrdBackend
	{
		private byte[] buffer;
		private boolean dirty = false;

		public RrdBerkeleyDbBackend(String path)
		{
			super(path);
			DatabaseEntry keyEntry = RrdBerkeleyDbBackendFactory
					.makeKeyEntry(path);
			DatabaseEntry dataEntry = new DatabaseEntry();
			OperationStatus status = RrdBerkeleyDbBackendFactory.this.rrdDatabase
					.get(null, keyEntry, dataEntry, null);
			if (status == OperationStatus.SUCCESS)
			{
				this.buffer = dataEntry.getData();
			} else
			{
				this.buffer = new byte[0];
			}
		}

		@Override
		protected synchronized void write(long offset, byte[] b)
				throws IOException
		{
			System.arraycopy(b, 0, this.buffer, (int) offset, b.length);
			this.dirty = true;
		}

		@Override
		protected synchronized void read(long offset, byte[] b)
				throws IOException
		{
			if (offset <= this.buffer.length - b.length)
			{
				System.arraycopy(this.buffer, (int) offset, b, 0, b.length);
			} else
			{
				throw new IOException("Not enough bytes available in memory "
						+ getPath());
			}
		}

		@Override
		public long getLength()
		{
			return this.buffer.length;
		}

		@Override
		protected void setLength(long length) throws IOException
		{
			if (length > 2147483647L)
			{
				throw new IOException("Illegal RRD length: " + length);
			}
			this.buffer = new byte[(int) length];
		}

		@Override
		public void close() throws IOException
		{
			if (this.dirty)
			{
				DatabaseEntry theKey = RrdBerkeleyDbBackendFactory
						.makeKeyEntry(getPath());
				DatabaseEntry theData = new DatabaseEntry(this.buffer);
				OperationStatus status = RrdBerkeleyDbBackendFactory.this.rrdDatabase
						.put(null, theKey, theData);
				if (status != OperationStatus.SUCCESS)
				{
					throw new IOException("Fail to put " + getPath() + ": "
							+ status);
				}
			}
		}

		@Override
		protected boolean isCachingAllowed()
		{
			return false;
		}
	}

	public static DatabaseEntry makeKeyEntry(String path)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(path, keyEntry);
		return keyEntry;
	}

	public String getFactoryName()
	{
		return "BERKELEY";
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
