package ggsmvkr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rrd4j.core.RrdBackendFactory;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

class DB
{
	static final int CHANNELS = 24;
	Environment dataEnv;
	Database channelDb;
	Database operatorDb;
	Database journalDb;
	Database[] messageDb;
	Database[] connectionDb;
	Database settingDb;
	Database rrdDb;
	
	Database packetDb;  //FIXME begin 1
	
	public class PacketEntry {

		Date date;
		int id;
		
//  	B1
//		bit7 Электропитание в норме
//		bit6 Электропитание датчиков в норме
//		bit5 Состояние расходомера №1
//		bit4 Состояние расходомера №2
//		bit3 Состояние расходомера №3
//		bit2 Обобщенный сигнал Авария
//		bit1 Резерв
//		bit0 Резерв

		boolean power;
		boolean sensorPower;
		boolean flowmeterState1;
		boolean flowmeterState2;
		boolean flowmeterState3;
		boolean alert;
		boolean reserve1;
		boolean reserve2;

		int level1;
		int level2;
		int level3;
		
		public int getLevel1() {
			return level1;
		}

		public void setLevel1(int level1) {
			this.level1 = level1;
		}

		public int getLevel2() {
			return level2;
		}

		public void setLevel2(int level2) {
			this.level2 = level2;
		}

		public int getLevel3() {
			return level3;
		}

		public void setLevel3(int level3) {
			this.level3 = level3;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Date getDate() {
			return this.date;
		}

		public int getId() {
			return this.id;
		}

		public boolean isPower() {
			return power;
		}

		public void setPower(boolean power) {
			this.power = power;
		}

		public boolean isSensorPower() {
			return sensorPower;
		}

		public void setSensorPower(boolean sensorPower) {
			this.sensorPower = sensorPower;
		}

		public boolean isFlowmeterState1() {
			return flowmeterState1;
		}

		public void setFlowmeterState1(boolean flowmeterState1) {
			this.flowmeterState1 = flowmeterState1;
		}

		public boolean isFlowmeterState2() {
			return flowmeterState2;
		}

		public void setFlowmeterState2(boolean flowmeterState2) {
			this.flowmeterState2 = flowmeterState2;
		}

		public boolean isFlowmeterState3() {
			return flowmeterState3;
		}

		public void setFlowmeterState3(boolean flowmeterState3) {
			this.flowmeterState3 = flowmeterState3;
		}

		public boolean isAlert() {
			return alert;
		}

		public void setAlert(boolean alert) {
			this.alert = alert;
		}

		public boolean isReserve1() {
			return reserve1;
		}

		public void setReserve1(boolean reserve1) {
			this.reserve1 = reserve1;
		}

		public boolean isReserve2() {
			return reserve2;
		}

		public void setReserve2(boolean reserve2) {
			this.reserve2 = reserve2;
		}

	}
	
	public void putPacket(int id, String value)
	{
		long timeAndId = new Date().getTime();
		timeAndId &= 0xFFFFFF00;
		timeAndId |= id;
		
		DatabaseEntry keyEntry = new DatabaseEntry();
		LongBinding.longToEntry(timeAndId, keyEntry);
		
		DatabaseEntry dataEntry = new DatabaseEntry();
		StringBinding.stringToEntry(value, dataEntry);
		OperationStatus status = this.packetDb.put(null, keyEntry, dataEntry);
		//OperationStatus status = db.put(null, keyEntry, dataEntry);
		if (status == OperationStatus.SUCCESS)
		{
			this.dataEnv.sync();
		}
	}
	
	public void getPacketKey (String key)  //FIXME  need TODO 1
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(key, keyEntry);
		DatabaseEntry dataEntry = new DatabaseEntry();
		OperationStatus status = packetDb.get(null, keyEntry, dataEntry, null);
		if (status == OperationStatus.SUCCESS)
		{
			//return dataEntry;
		}
		//return null;
	}
	
	public PacketEntry getPacketEntryInstance()
	{
		return new PacketEntry();
	}
	
	public List<PacketEntry> getPacketList(int id)
	{	
		List<PacketEntry> list = new ArrayList<PacketEntry>(30000);
		Date packetDate;
		int packetId=0;
		String strPacket = ""; 
		String strByte = "";
		byte bytePacket, bytePacketHigh, bytePacketLow;
		int intValue;

		Cursor cursor = this.packetDb.openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			PacketEntry pe = new PacketEntry();
			long time = LongBinding.entryToLong(foundKey);
			if ((time & 0xFFFFFF00) != 0L)
			{
				packetDate = new Date(time);
				SimpleDateFormat formatter5=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String formatDate = formatter5.format(time);
				
				packetId = ((int) (time & 0xFF));
				if ((id == 0) || (id == packetId))
				{
					pe.date = packetDate;
					pe.id = packetId;
					strPacket = StringBinding.entryToString(foundData);
					intValue = Integer.parseInt(strPacket.substring(0, 2), 16);  
//					bit7 Электропитание в норме
//					bit6 Электропитание датчиков в норме
//					bit5 Состояние расходомера №1
//					bit4 Состояние расходомера №2
//					bit3 Состояние расходомера №3
//					bit2 Обобщенный сигнал Авария
//					bit1 Резерв
//					bit0 Резерв

					pe.power= ((intValue & 0x80) > 0 ? true : false);
					pe.sensorPower = ((intValue & 0x40) > 0 ? true : false);
					pe.flowmeterState1 = ((intValue & 0x20) > 0 ? true : false);
					pe.flowmeterState2 = ((intValue & 0x10) > 0 ? true : false);
					pe.flowmeterState3 = ((intValue & 0x08) > 0 ? true : false);
					pe.alert = ((intValue & 0x04) > 0 ? true : false);
					pe.reserve1 = ((intValue & 0x02) > 0 ? true : false);
					pe.reserve2 = ((intValue & 0x01) > 0 ? true : false);
					
					pe.level1 = Integer.parseInt(strPacket.substring(2, 6), 16);
					pe.level2 = Integer.parseInt(strPacket.substring(6, 10), 16);
					pe.level3 = Integer.parseInt(strPacket.substring(10, 14), 16);

					list.add(pe);
				}
			}
		}
		cursor.close();
		return list; 
	}								

	public String getPacketString(int id)
	{	
		Date packetDate;
		int packetId=0;
		String strPacket = ""; 
		
		StringBuilder sb = new StringBuilder();
		sb.append("");
		Cursor cursor = this.packetDb.openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			JournalEntry je = new JournalEntry();
			long time = LongBinding.entryToLong(foundKey);
			if ((time & 0xFFFFFF00) != 0L)
			{
				packetDate = new Date(time);
				SimpleDateFormat formatter5=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
				String formatDate = formatter5.format(time);
				

				packetId = ((int) (time & 0xFF));
				if ((id == 0) || (id == packetId))
				{
					strPacket = StringBinding.entryToString(foundData);
					sb.append(formatDate + ": ");
					sb.append(packetId);
					sb.append(" /" + strPacket + "/ ");
				}
			}
		}
		cursor.close();
		return sb.toString();
	}								//FIXME end 1
	
	public class ConnectionEntry
	{
		Date date;
		List<ParameterValue> parameters;

		ConnectionEntry(Date _date, List<ParameterValue> _params)
		{
			this.date = _date;
			this.parameters = _params;
		}

		public Date getDate()
		{
			return this.date;
		}

		public List<ParameterValue> getParameters()
		{
			return this.parameters;
		}
	}

	public class JournalEntry
	{
		Date date;
		int id;
		String operator;
		Alert alert;

		public JournalEntry()
		{
		}

		public Date getDate()
		{
			return this.date;
		}

		public int getId()
		{
			return this.id;
		}

		public String getOperator()
		{
			return this.operator;
		}

		public Alert getAlert()
		{
			return this.alert;
		}

		public boolean isSubmit()
		{
			return this.operator != null;
		}
	}

	static int ceil(int f, int d)
	{
		if (d == 0)
		{
			return 0;
		}
		return f % d == 0 ? f / d : f / d + 1;
	}

	public class ChannelAvgs
	{
		int id;

		public class Avg
		{
			public Avg()
			{
			}

			public class Pump
			{
				int vCount;
				int l1Count;
				int l2Count;
				int l3Count;
				int v;
				int l1;
				int l2;
				int l3;

				public Pump()
				{
				}

				public int getV()
				{
					return DB.ceil(this.v, this.vCount);
				}

				public int getL1()
				{
					return DB.ceil(this.l1, this.l1Count);
				}

				public int getL2()
				{
					return DB.ceil(this.l2, this.l2Count);
				}

				public int getL3()
				{
					return DB.ceil(this.l3, this.l3Count);
				}
			}

			public class Tank
			{
				int count;
				int level;

				public Tank()
				{
				}

				public int getLevel()
				{
					return DB.ceil(this.level, this.count);
				}
			}

			void update(Message msg)
			{
				this.tank.count += 1;
				this.tank.level += msg.tank.getLevel() * 10;
				for (int p = 0; p < 4; p++)
				{
					if (msg.pumps[p] != null)
					{
						if (msg.pumps[p].getV() > 0)
						{
							this.pumps[p].vCount += 1;
							this.pumps[p].v += msg.pumps[p].getV();
						}
						if (msg.pumps[p].getL1() > 0)
						{
							this.pumps[p].l1Count += 1;
							this.pumps[p].l1 += msg.pumps[p].getL1();
						}
						if (msg.pumps[p].getL2() > 0)
						{
							this.pumps[p].l2Count += 1;
							this.pumps[p].l2 += msg.pumps[p].getL2();
						}
						if (msg.pumps[p].getL3() > 0)
						{
							this.pumps[p].l3Count += 1;
							this.pumps[p].l3 += msg.pumps[p].getL3();
						}
					}
				}
			}

			public Tank getTank()
			{
				return this.tank.count > 0 ? this.tank : null;
			}

			public Pump[] getPumps()
			{
				return this.pumps;
			}

			Pump[] pumps =
			{ new Pump(), new Pump(), new Pump(), new Pump() };
			Tank tank = new Tank();
		}

		ChannelAvgs(int id)
		{
			this.id = id;
		}

		public int getId()
		{
			return this.id;
		}

		public Avg getByMinAvg()
		{
			return this.byMin;
		}

		public Avg getByHourAvg()
		{
			return this.byHour;
		}

		public Avg getByDayAvg()
		{
			return this.byDay;
		}

		Avg byDay = new Avg();
		Avg byHour = new Avg();
		Avg byMin = new Avg();
	}

	public DB(String dbdir)
	{
		File dir = new File(dbdir);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		this.dataEnv = new Environment(dir, envConfig);

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(false);
		this.messageDb = new Database[24];
		this.connectionDb = new Database[24];
		for (int i = 0; i < 24; i++)
		{
			this.messageDb[i] = this.dataEnv.openDatabase(null, "message" + i,
					dbConfig);
			this.connectionDb[i] = this.dataEnv.openDatabase(null, "connection"
					+ i, dbConfig);
		}
		this.channelDb = this.dataEnv.openDatabase(null, "channel", dbConfig);
		this.operatorDb = this.dataEnv.openDatabase(null, "operator", dbConfig);
		this.journalDb = this.dataEnv.openDatabase(null, "journal", dbConfig);
		this.settingDb = this.dataEnv.openDatabase(null, "setting", dbConfig);
		this.rrdDb = this.dataEnv.openDatabase(null, "rrd", dbConfig);
		this.packetDb = this.dataEnv.openDatabase(null, "packetDb", dbConfig);  //FIXME 2
		RrdBackendFactory
				.registerAndSetAsDefaultFactory(new RrdBerkeleyDbBackendFactory(
						this.rrdDb));
	}

	public void putOperator(Operator oper)
	{
		putOperatorStringField(oper.getLogin(), "name", oper.getName());
		putOperatorStringField(oper.getLogin(), "passwd", oper.getPasswd());
		this.dataEnv.sync();

		oper = getOperator(oper.getLogin());
	}

	public void delOperator(String login)
	{
		delOperatorField(login, "name");
		delOperatorField(login, "passwd");
		this.dataEnv.sync();
	}

	public String getOperatorStringField(String login, String name)
	{
		DatabaseEntry de = getFieldByStringKey(this.operatorDb, login + ":"
				+ name);
		return de == null ? null : StringBinding.entryToString(de);
	}

	public void putOperatorStringField(String login, String name, String value)
	{
		DatabaseEntry dataEntry = new DatabaseEntry();
		StringBinding.stringToEntry(value, dataEntry);
		putFieldByStringKey(this.operatorDb, login + ":" + name, dataEntry);
	}

	public void delOperatorField(String login, String name)
	{
		delFieldByStringKey(this.operatorDb, login + ":" + name);
	}

	public Operator getOperator(String login)
	{
		if (login == null)
		{
			return null;
		}
		Operator oper = new Operator(login);
		oper.setName(getOperatorStringField(login, "name"));
		oper.setPasswd(getOperatorStringField(login, "passwd"));
		return oper;
	}

	public String getOperatorPasswd(String login)
	{
		if (login == null)
		{
			return null;
		}
		return getOperatorStringField(login, "passwd");
	}

	public List<Operator> getOperators()
	{
		Set<String> logins = new HashSet();
		Cursor cursor = this.operatorDb.openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			String key = StringBinding.entryToString(foundKey);

			String[] skey = key.split(":");
			logins.add(skey[0]);
		}
		cursor.close();
		ArrayList<Operator> result = new ArrayList(logins.size() * 2);
		for (String login : logins)
		{
			result.add(getOperator(login));
		}
		return result;
	}

	public DatabaseEntry getFieldByStringKey(Database db, String key)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(key, keyEntry);
		DatabaseEntry dataEntry = new DatabaseEntry();
		OperationStatus status = db.get(null, keyEntry, dataEntry, null);
		if (status == OperationStatus.SUCCESS)
		{
			return dataEntry;
		}
		return null;
	}

	public void putFieldByStringKey(Database db, String key,
			DatabaseEntry dataEntry)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(key, keyEntry);
		OperationStatus status = db.put(null, keyEntry, dataEntry);
		if (status == OperationStatus.SUCCESS)
		{
		}
	}

	public void delFieldByStringKey(Database db, String key)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(key, keyEntry);
		OperationStatus status = db.delete(null, keyEntry);
		if (status == OperationStatus.SUCCESS)
		{
		}
	}

	public List<Integer> getWorkingChannels()
	{
		List<Integer> channels = new ArrayList(24);
		for (int id = 1; id <= 24; id++)
		{
			if (getChannelInfoField(id, "type") != null)
			{
				channels.add(Integer.valueOf(id));
			}
		}
		return channels;
	}

	String makeKey(int id, String name)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(':').append(name);
		return sb.toString();
	}

	public DatabaseEntry getChannelInfoField(int id, String name)
	{
		return getFieldByStringKey(this.channelDb, makeKey(id, name));
	}

	public void putChannelInfoField(int id, String name, DatabaseEntry dataEntry)
	{
		putFieldByStringKey(this.channelDb, makeKey(id, name), dataEntry);
	}

	public void delChannelInfoField(int id, String name)
	{
		delFieldByStringKey(this.channelDb, makeKey(id, name));
	}

	public void putChannelInfoStringField(int id, String name, String value)
	{
		DatabaseEntry de = new DatabaseEntry();
		StringBinding.stringToEntry(value, de);
		putChannelInfoField(id, name, de);
	}

	public ChannelInfo getChannelInfo(int id)
	{
		ChannelInfo ci = new ChannelInfo(id);
		DatabaseEntry de = getChannelInfoField(id, "type");
		if (de != null)
		{
			ci.setType(ChannelInfo.Type.byName(StringBinding.entryToString(de)));
		}
		de = getChannelInfoField(id, "pumps");
		if (de != null)
		{
			byte[] data = de.getData();
			for (int p = 0; p < 4; p++)
			{
				ci.setPumpState(p, ChannelInfo.PumpState.byId(data[p]));
			}
		}
		return ci;
	}

	public void putChannelInfo(ChannelInfo ci)
	{
		if (ci.getType() == ChannelInfo.Type.None)
		{
			delChannelInfoField(ci.id, "type");
		} else
		{
			putChannelInfoStringField(ci.id, "type", ci.getType().getName());
		}
		byte[] data = new byte[4];
		for (int p = 0; p < 4; p++)
		{
			data[p] = ci.getPumpState(p).id();
		}
		DatabaseEntry de = new DatabaseEntry(data);
		putChannelInfoField(ci.id, "pumps", de);
		this.dataEnv.sync();
	}

	public String getSetting(String key)
	{
		DatabaseEntry de = getFieldByStringKey(this.settingDb, key);
		return de == null ? null : StringBinding.entryToString(de);
	}

	public void setSetting(String key, String value)
	{
		DatabaseEntry de = new DatabaseEntry();
		StringBinding.stringToEntry(value, de);
		putFieldByStringKey(this.settingDb, key, de);
	}

	public void delSetting(String key)
	{
		delFieldByStringKey(this.settingDb, key);
	}

	public Message getLastMessage(int id)
	{
		if ((id < 1) || (id > 24))
		{
			return null;
		}
		Cursor cursor = this.messageDb[(id - 1)].openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		Message msg = null;
		if (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			long time = LongBinding.entryToLong(foundKey);
			byte[] data = foundData.getData();
			msg = new Message(new Date(time), registersFromBytes(data));
		}
		cursor.close();
		return msg;
	}

	public List<Message> getMessages(int id, Date day)
	{
		final List<Message> list = new ArrayList(30000);
		if ((id < 1) || (id > 24))
		{
			return list;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		long timef = cal.getTime().getTime();
		cal.add(5, 1);
		long timet = cal.getTime().getTime();
		processMessages(id, timef, timet, new MessageProcessor()
		{
			public void process(Message msg)
			{
				list.add(msg);
			}
		});
		return list;
	}

	public void processMessages(int id, long timef, long timet,
			MessageProcessor mp)
	{
		Cursor cursor = this.messageDb[(id - 1)].openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			long time = LongBinding.entryToLong(foundKey);
			if (time < timef)
			{
				break;
			}
			if (time < timet)
			{
				byte[] data = foundData.getData();
				Date date = new Date(time);
				mp.process(new Message(date, registersFromBytes(data)));
			}
		}
		cursor.close();
	}

	private static byte[] registersToBytes(List<RegisterValue> regs)
	{
		ByteBuffer bb = ByteBuffer.allocate(regs.size() * 3);
		for (RegisterValue rv : regs)
		{
			byte idd = rv.getRegister().id();
			idd = (byte) (idd | (byte) ((byte) rv.getPump() << 6));
			bb.put(idd);
			bb.putShort((short) rv.getValue());
		}
		return bb.array();
	}

	private static List<RegisterValue> registersFromBytes(byte[] data)
	{
		ByteBuffer bb = ByteBuffer.wrap(data);
		List<RegisterValue> regs = new ArrayList();
		while (bb.hasRemaining())
		{
			byte idd = bb.get();
			Register reg = Register.byId((byte) (idd & 0x3F));
			int pump = idd >> 6;
			if (pump < 0)
			{
				pump += 4;
			}
			regs.add(new RegisterValue(reg, pump, bb.getShort()));
		}
		return regs;
	}

	public synchronized Message putRegisters(int id, List<RegisterValue> regs)
	{
		if ((id < 1) || (id > 24))
		{
			return null;
		}
		Date date = new Date();
		putRegisters(this.messageDb[(id - 1)], date, regs);
		try
		{
			Thread.sleep(2L);
		} catch (Exception e)
		{
		}
		return new Message(date, regs);
	}

	private void putRegisters(Database db, Date date, List<RegisterValue> regs)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry(registersToBytes(regs));

		LongBinding.longToEntry(date.getTime(), keyEntry);

		OperationStatus status = db.put(null, keyEntry, dataEntry);
		this.dataEnv.sync();
	}

	public ChannelAvgs getChannelAvgs(int id)
	{
		long ctime = System.currentTimeMillis();
		final long minTime = ctime - 60000L;
		final long hourTime = ctime - 3600000L;
		long dayTime = ctime - 86400000L;
		final ChannelAvgs ca = new ChannelAvgs(id);
		processMessages(id, dayTime, ctime, new MessageProcessor()
		{
			public void process(Message msg)
			{
				if (msg.date.getTime() >= minTime)
				{
					ca.getByMinAvg().update(msg);
					//hourTime.byMin.update(msg);
				}
				if (msg.date.getTime() >= hourTime)
				{
					ca.getByHourAvg().update(msg);
					//hourTime.byHour.update(msg);
				}
				ca.getByDayAvg().update(msg);
				//hourTime.byDay.update(msg);
			}
		});
		return ca;
	}

	public void putConnection(int id, Date date, List<ParameterValue> params)
	{
		if ((id < 1) || (id > 24))
		{
			return;
		}
		Database db = this.connectionDb[(id - 1)];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write((byte) params.size());
		ParameterValue pv;
		for (Iterator i$ = params.iterator(); i$.hasNext(); pv.toStream(baos))
		{
			pv = (ParameterValue) i$.next();
		}
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry(baos.toByteArray());

		LongBinding.longToEntry(date.getTime(), keyEntry);

		OperationStatus status = db.put(null, keyEntry, dataEntry);
		this.dataEnv.sync();
	}

	public List<ConnectionEntry> getConnections(int id, Date day)
	{
		List<ConnectionEntry> list = new ArrayList(30000);
		if ((id < 1) || (id > 24))
		{
			return list;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		long timef = cal.getTime().getTime();
		cal.add(5, 1);
		long timel = cal.getTime().getTime();

		Cursor cursor = this.connectionDb[(id - 1)].openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		long timepkt = Math.min(timel, new Date().getTime());
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			long time = LongBinding.entryToLong(foundKey);
			if (time < timef)
			{
				break;
			}
			if (time < timel)
			{
				byte[] data = foundData.getData();
				Date date = new Date(time);
				ByteArrayInputStream bis = new ByteArrayInputStream(data);
				List<ParameterValue> params = new ArrayList();
				int cnt = bis.read();
				for (int i = 0; i < cnt; i++)
				{
					params.add(ParameterValue.fromStream(bis));
				}
				list.add(new ConnectionEntry(date, params));
			}
		}
		cursor.close();
		return list;
	}

	public Alert getSubmittedAlert(int id)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		LongBinding.longToEntry(id, keyEntry);
		DatabaseEntry dataEntry = new DatabaseEntry();
		OperationStatus status = this.journalDb.get(null, keyEntry, dataEntry,
				null);
		if (status == OperationStatus.SUCCESS)
		{
			JournalEntry je = new JournalEntry();
			journalFromBytes(je, dataEntry.getData());
			return je.alert;
		}
		return null;
	}

	public void putSubmittedAlert(int id, Alert alert)
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry(journalToBytes(alert, null));
		LongBinding.longToEntry(id, keyEntry);
		this.journalDb.put(null, keyEntry, dataEntry);
		this.dataEnv.sync();
	}

	public void putJournalAlert(int id, Alert alert)
	{
		putJournal(id, null, alert);
	}

	public void putJournalSubmit(int id, String operator, Alert alert)
	{
		putJournal(id, operator, alert);
	}

	public void putJournal(int id, String operator, Alert alert)
	{
		long timeAndId = new Date().getTime();
		timeAndId &= 0xFFFFFF00;
		timeAndId |= id;

		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry(journalToBytes(alert,
				operator));
		LongBinding.longToEntry(timeAndId, keyEntry);

		this.journalDb.put(null, keyEntry, dataEntry);
		this.dataEnv.sync();
	}

	public List<JournalEntry> getJournals(int id)
	{
		List<JournalEntry> list = new ArrayList(30000);
		if ((id < 0) || (id > 24))
		{
			return list;
		}
		Cursor cursor = this.journalDb.openCursor(null, null);
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		while (cursor.getPrev(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		{
			JournalEntry je = new JournalEntry();
			long time = LongBinding.entryToLong(foundKey);
			if ((time & 0xFFFFFF00) != 0L)
			{
				je.date = new Date(time);
				je.id = ((int) (time & 0xFF));
				if ((id == 0) || (id == je.id))
				{
					journalFromBytes(je, foundData.getData());
					list.add(je);
				}
			}
		}
		cursor.close();
		return list;
	}

	byte[] journalToBytes(Alert alert, String operator)
	{
		ByteBuffer bb = ByteBuffer.allocate(12 + (operator == null ? 0
				: operator.length()));
		bb.putShort((short) alert.sys.flags);
		bb.putShort((short) alert.tank.flags);
		for (int p = 0; p < 4; p++)
		{
			bb.putShort((short) (alert.pumps[p].flags & 0x3FF | alert.pumps[p].fc << 10));
		}
		if (operator != null)
		{
			bb.put(operator.getBytes());
		}
		return bb.array();
	}

	void journalFromBytes(JournalEntry je, byte[] data)
	{
		ByteBuffer bb = ByteBuffer.wrap(data);
		je.alert = new Alert();
		je.alert.sys.flags = bb.getShort();
		je.alert.tank.flags = bb.getShort();
		for (int p = 0; p < 4; p++)
		{
			int bt = bb.getShort();
			je.alert.pumps[p].flags = (bt & 0x3FF);
			je.alert.pumps[p].fc = (bt >> 10);
		}
		if (data.length > 12)
		{
			je.operator = new String(data, 12, data.length - 12);
		}
	}

	public boolean isRrdExists(String name)
	{
		DatabaseEntry keyEntry = RrdBerkeleyDbBackendFactory.makeKeyEntry(name);
		DatabaseEntry dataEntry = new DatabaseEntry();
		return this.rrdDb.get(null, keyEntry, dataEntry, null) == OperationStatus.SUCCESS;
	}

	public void close()
	{
		for (int i = 0; i < 24; i++)
		{
			this.messageDb[i].close();
			this.connectionDb[i].close();
		}
		this.channelDb.close();
		this.operatorDb.close();
		this.journalDb.close();
		this.settingDb.close();
		this.rrdDb.close();
		this.dataEnv.close();
		this.packetDb.close();	//FIXME 3
	}

	static abstract interface MessageProcessor
	{
		public abstract void process(Message paramMessage);
	}

	class DayFilter
	{
		long timef;
		long timet;

		DayFilter(Date day)
		{
		}
	}
}
