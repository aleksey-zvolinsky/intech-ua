package ggsmvkr;



import static org.rrd4j.ConsolFun.AVERAGE;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

import com.intechua.HDatabase;
import com.intechua.db.beans.PacketEntry;

public class Server
{
	static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	static HDatabase hdb;
	public static HDatabase getHdb()
	{
		return hdb;
	}

	static DB db;
	static RRDs rrds;
	static Processor processor;
	//String string_date = "20.07.2014 00:00:01";
	//Date d = df.parse(string_date);
	//long milliseconds = d.getTime();

    // Util.getTimestamp(1999, 3, 7) + 12 * 60 * 60;
    //static final long START = Util.getTimestamp(2014, 7, 20) + 12 * 60 * 60; //920804400; Error!!!
    // End is 80 minutes later
    //static final long END = Util.getTimestamp(2014, 7, 29) + 12 * 60 * 60;
    //static final long START = (System.currentTimeMillis()+500L)/1000L; //920804423; 
	static final long START = 1406392672010L;
    // End is 80 minutes later
    static final long END = START + 80 * 60;   //1406388255185 1406388723960

    static final String FILE = "level";
    static final int IMG_WIDTH = 500;
    static final int IMG_HEIGHT = 300;
    static final String FILE_FORMAT = "png";

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
		
		hdb = new HDatabase("db/db");
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

		beginLog("Starting server on "+ Setup.get().getPacketServerPort() +" port: ");
		try(ServerSocket welcomeSocket = new ServerSocket(Setup.get().getPacketServerPort()))
		{
			endLog("OK");

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
				}
				catch (Exception e)
				{
					endLog("Fail (" + e.getMessage() + ")");
				}
				continue;
			}
		}
		catch (BindException e)
		{
			endLog("Fail (" + e.getMessage() + ")");
			return;
		}
	}

	static class Communicator extends Thread
	{
		Stream ss;   	//FIXME
		int idPacket;  	//FIXME
		//RRDs rrds;	 	//FIXME
		
		int idPacketJournal;

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
			long ctime;
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
				
				
				//read manual timestamp - for testing purposes
				StringBuilder timestampsb = new StringBuilder();
				byte[] timestampBuffRead = new byte[lenBuff];
				for (int i=0; i<lenBuff; i++) 
				{
					timestampBuffRead[i] = this.ss.read();
					timestampsb.append(String.format("%02X", timestampBuffRead[i]));
				}
				
				Date date = new Date(ByteBuffer.wrap(timestampBuffRead).getLong());
				this.ss.logger.log("Needed date time is " + df.format(date));

				this.ss.logger.log("putPacket: idPacket=" + idPacket + " strEntry=:" + sb  + ":");
				PacketEntry pe = PacketEntry.fromString(sb.toString());
				pe.setDate(new Date());
				
				db.putPacket(pe.getDate().getTime(), idPacket, sb.toString());
				this.ss.logger.log("getPacket: "+ db.getPacketString(0));
				
//				PacketJournalEntry pje = new PacketJournalEntry();
//				pje.setDate(pe.getDate());
//				pje.setLevel(pe.getLevel1());
//				pje.setPower(pe.isPower1());
//				pje.setState(pe.isFlowmeterState1());
//				
//				db.putPacketJournal(idPacketJournal++, pje);
//				
//				pje = new PacketJournalEntry();
//				pje.setDate(pe.getDate());
//				pje.setLevel(pe.getLevel2());
//				pje.setPower(pe.isPower2());
//				pje.setState(pe.isFlowmeterState2());
//				
//				db.putPacketJournal(idPacketJournal++, pje);
//				
//				pje = new PacketJournalEntry();
//				pje.setDate(pe.getDate());
//				pje.setLevel(pe.getLevel2());
//				pje.setPower(pe.isPower2());
//				pje.setState(pe.isFlowmeterState2());
//				
//				db.putPacketJournal(idPacketJournal++, pje);
//				
				if (valCRC == newBuffRead[lenBuff-1])
				{// compared CRC
					this.ss.logger.endLog("OK");
				}
				else
				{// NOT compared CRC
					this.ss.logger.endLog("CRC Error");
				}
				db.getPacketString(0);
				
				//TODO how
				ctime = System.currentTimeMillis(); //(System.currentTimeMillis() + 500L) / 1000L;
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss:SSS");
		        Date resultDate = new Date(ctime);
		        // System.out.println(sdf.format(resultDate));
				this.ss.logger.log("updatePacket ctime: "+ sdf.format(resultDate) + " ctime= " + ctime);
				ctime = ctime  + 500L;
		        resultDate = new Date(ctime);
				this.ss.logger.log("updatePacket ctime+500: "+ sdf.format(resultDate));
//				ctime = (ctime  / 1000L) ;
//		        resultDate = new Date(ctime * 1000L);
//				this.ss.logger.log("updatePacket ctime/1000: "+ sdf.format(resultDate));
				
				 println("START="+sdf.format(START)+ " Util=" + Util.getTimestamp(2014, 07, 20));
				 println("END"+sdf.format(END));
				//Message msg = new Message(null, null) ;
				PacketMessage pc = new PacketMessage();
				pc.setLevel1(120);
				pc.setLevel2(110);
				pc.setLevel3(100);
				

//				try {
//					rrds.updatePacket(idPacket, ctime, pc);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				//RRDs r = null;   

				String rrdName = "packet1";
				if (!db.isRrdExists(rrdName)) {
					try {
						rrds.createPacketRrd(rrdName);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				final int[] odoData1 = { 145, 157, 163, 163, 163, 173, 183, 193,
						199, 105, 111, 115, 120, 122, 123 };

				RrdDb rrd = new RrdDb(rrdName);
				Sample sample1 = rrd.createSample(ctime);

				final int fiveMinutes1 = 1 * 60;
				for (int i = 0; i < odoData1.length; ++i) {
					// First sample is 12:05, so use i + 1
					sample1.setTime(ctime + (i + 1) * fiveMinutes1);
					sample1.setValue(0, odoData1[i]);
					sample1.setValue(1, odoData1[i]);
					sample1.setValue(2, odoData1[i]);
				      println("==  sample ");
				      println(sample1.dump());
				      println("==  sample ");
					// Store the value
					sample1.update();
				}
			      println("== Last info was1: " + rrd.getInfo());
			      println("== RRD1 ");
			      println(rrd.dump());
				rrd.close();
		        // test read-only access!
				rrd = new RrdDb(rrdName, true);
				this.ss.logger.log("File reopen in read-only mode");
				this.ss.logger.log("== Last update time was: " + rrd.getLastUpdateTime() + " " +sdf.format(rrd.getLastUpdateTime()));
				this.ss.logger.log("== Last info was: " + rrd.getInfo());

		        // rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200
		        // Fetch data at finest resolution (300 sec), which is the default.
				this.ss.logger.log("== Fetch request for the interval at default resolution:");
				 println("START="+sdf.format(START));
				 println("END"+sdf.format(END));				 
		        FetchRequest request1 = rrd.createFetchRequest(AVERAGE, START, END);
		        this.ss.logger.log(request1.dump());
		        this.ss.logger.log("== Fetching data at default resolution");
		        FetchData fetchData1 = request1.fetchData();
		        this.ss.logger.log("== Data fetched, " + fetchData1.getRowCount()
		                + " points obtained");
		        // This output is different from the tutorial output from rrdtool;
		        // it includes 920804400:nan but *not* 920809500:nan
		        this.ss.logger.log(fetchData1.toString());
		        
		        long coarseRes = 1800;
		        println("== Fetch request for the interval at coarse resolution:");
		        // START is before the first data point; END is after the last data
		        // point; ensure start and end are even multiples of the resolution
		        FetchRequest request2 = rrd.createFetchRequest(AVERAGE, START
		                / coarseRes * coarseRes, END / coarseRes * coarseRes, 1800);
		        println(request2.dump());
		        println("== Fetching data at coarse resolution");
		        FetchData fetchData2 = request2.fetchData();
		        println("== Data fetched, " + fetchData2.getRowCount()
		                + " points obtained");
		        println(fetchData2.toString());

		        // Done with direct access
		        rrd.close();

		        // Graph 1 has units in millis
		        // rrdtool graph speed.png \
		        // --start 920804400 --end 920808000 \
		        // DEF:myspeed=test.rrd:speed:AVERAGE \
		        // LINE2:myspeed#FF0000
		        println("== Creating graph 1");
		        RrdGraphDef gDef1 = new RrdGraphDef();
		        gDef1.setWidth(IMG_WIDTH);
		        gDef1.setHeight(IMG_HEIGHT);
		        String img1Path = Util.getRrd4jDemoPath(FILE + "1." + FILE_FORMAT);
		        gDef1.setFilename(img1Path);
		        gDef1.setStartTime(START);
		        gDef1.setEndTime(END);
		        gDef1.datasource("graphLevel1", rrdName, "level1", AVERAGE);
		        gDef1.line("graphLevel1", new Color(0xFF, 0x00, 0x00), "Level1");
		        gDef1.comment("Control Level1\\r");
		        gDef1.setImageInfo("<img src='%s' width='%d' height = '%d'>");
		        gDef1.setImageFormat(FILE_FORMAT);
		        println("Rendering graph 1");
		        RrdGraph graph1 = new RrdGraph(gDef1);
		        println(graph1.getRrdGraphInfo().dump());
		        println("== Graph 1 created");


		     // Graph 3 is fancier yet
		        // rrdtool graph speed3.png \
		        // --start 920804400 --end 920808000 \
		        // --vertical-label km/h \
		        // DEF:myspeed=test.rrd:speed:AVERAGE \
		        // "CDEF:kmh=myspeed,3600,*" \
		        // CDEF:fast=kmh,100,GT,kmh,0,IF \
		        // CDEF:good=kmh,100,GT,0,kmh,IF \
		        // HRULE:100#0000FF:"Maximum allowed" \
		        // AREA:good#00FF00:"Good speed" \
		        // AREA:fast#FF0000:"Too fast"
		        println("== Creating graph 3");
		        RrdGraphDef gDef3 = new RrdGraphDef();
		        gDef3.setWidth(IMG_WIDTH);
		        gDef3.setHeight(IMG_HEIGHT);
		        String img3Path = Util.getRrd4jDemoPath(FILE + "1_3" + "." + FILE_FORMAT);
		        gDef3.setFilename(img3Path);
		        gDef3.setStartTime(START);
		        gDef3.setEndTime(END);
		        gDef3.setVerticalLabel("HloroGas");
		        gDef3.datasource("graphLevel1", rrdName, "level1", AVERAGE);
		        gDef3.datasource("lvl", "graphLevel1,1,/");
		        gDef3.datasource("alarm", "lvl,150,GT,lvl,0,IF");
		        gDef3.datasource("good", "lvl,150,GT,0,lvl,IF");
		        gDef3.hrule(150, new Color(0x00, 0x00, 0xFF), "Maximum allowed");
		        gDef3.area("good", new Color(0x00, 0xFF, 0x00), "Good level");
		        gDef3.area("alarm", new Color(0xFF, 0x00, 0x00), "Alarm level");
		        gDef3.comment("Graph 3\\r");
		        gDef3.setImageInfo("<img src='%s' width='%d' height = '%d'>");
		        gDef3.setImageFormat(FILE_FORMAT);
		        println("Rendering graph 3");
		        RrdGraph graph3 = new RrdGraph(gDef3);
		        println(graph3.getRrdGraphInfo().dump());
		        println("== Graph 3 created");
 
		        // Graph 4 is the fanciest
		        // rrdtool graph speed4.png \
		        // --start 920804400 --end 920808000 \
		        // --vertical-label km/h \
		        // DEF:myspeed=test.rrd:speed:AVERAGE \
		        // CDEF:nonans=myspeed,UN,0,myspeed,IF \
		        // CDEF:kmh=nonans,3600,* \
		        // CDEF:fast=kmh,100,GT,100,0,IF \
		        // CDEF:over=kmh,100,GT,kmh,100,-,0,IF \
		        // CDEF:good=kmh,100,GT,0,kmh,IF \
		        // HRULE:100#0000FF:"Maximum allowed" \
		        // AREA:good#00FF00:"Good speed" \
		        // AREA:fast#550000:"Too fast" \
		        // STACK:over#FF0000:"Over speed"
		        println("== Creating graph 4");
		        RrdGraphDef gDef4 = new RrdGraphDef();
		        gDef4.setWidth(IMG_WIDTH);
		        gDef4.setHeight(IMG_HEIGHT);
		        String img4Path = Util.getRrd4jDemoPath(FILE + "1_4" + "." + FILE_FORMAT);
		        gDef4.setFilename(img4Path);
		        gDef4.setStartTime(START);
		        gDef4.setEndTime(END);
		        gDef4.setVerticalLabel("GipoHlorid");
		        gDef4.datasource("graphLevel1", rrdName, "level1", AVERAGE);
		        gDef4.datasource("nonans", "graphLevel1,UN,0,graphLevel1,IF");
		        gDef4.datasource("lvl", "nonans,1,*");
		        gDef4.datasource("alarm", "lvl,150,GT,lvl,0,IF");
		        gDef4.datasource("over", "lvl,150,GT,lvl,150,-,0,IF");
		        gDef4.datasource("good", "lvl,150,GT,0,lvl,IF");
		        gDef4.hrule(150, new Color(0x00, 0x00, 0xFF), "Maximum allowed");
		        gDef4.area("good", new Color(0x00, 0xFF, 0x00), "Good level");
		        gDef4.area("alarm", new Color(0x55, 00, 00), "Alarm level");
		        gDef4.stack("over", new Color(0xff, 0x00, 0x00), "Over level");
		        gDef4.comment("Graph 4\\r");
		        gDef4.setImageInfo("<img src='%s' width='%d' height = '%d'>");
		        gDef4.setImageFormat(FILE_FORMAT);
		        println("Rendering graph 4");
		        RrdGraph graph4 = new RrdGraph(gDef4);
		        println(graph4.getRrdGraphInfo().dump());
		        println("== Graph 4 created");

		        
		        this.ss.logger.log("updatePacket ctime: "+ sdf.format(resultDate) + " ctime= " + ctime + " ctime= " + ((ctime+500L)/1000L));
//// = = = = = = = = =   
//		        System.setProperty("java.awt.headless","true");
//  
//		        println("== Starting tutorial code");
//		        final String rrdPath = Util.getRrd4jDemoPath(FILE + ".rrd");
//		        final String speedSource = "speed";
//
//		        // rrdtool create test.rrd \
//		        // --start 920804400 \
//		        // DS:speed:COUNTER:600:U:U \
//		        // RRA:AVERAGE:0.5:1:24 \
//		        // RRA:AVERAGE:0.5:6:10
//		        println("== Creating RRD file " + rrdPath + " with initial time "
//		                + START);
//		        // Expect new data every 300 seconds
//		        RrdDef rrdDef = new RrdDef(rrdPath, START, 300);
//		        rrdDef.setVersion(2);
//		        // A counter for km: heartbeat is 600 sec; min val 0; no max val.
//		        // Heartbeat: after no update for the interval, declare value UNKNOWN
//		        rrdDef.addDatasource(speedSource, DsType.COUNTER, 600, 0, Double.NaN);
//		        // Add archive using fn AVERAGE, known:unkown ratio of 0.5,
//		        // use 1 data point, keep 24 rows. I.e., this archive keeps the latest
//		        // value, it's not actually an average.
//		        rrdDef.addArchive(AVERAGE, 0.5, 1, 24);
//		        // Add archive using fn AVERAGE, known:unkown ratio of 0.5,
//		        // use 6 data points, keep 10 rows. I.e., this keeps an average over
//		        // a 30 minute interval, and 10 * 30 = 300 minutes (5hrs) are kept.
//		        rrdDef.addArchive(AVERAGE, 0.5, 6, 10);
//
//		        // Create and check the database
//		        println(rrdDef.dump());
//		        println("Estimated file size: " + rrdDef.getEstimatedSize());
//		        RrdDb rrdDb = new RrdDb(rrdDef);
//		        println("== RRD file created.");
//		        if (rrdDb.getRrdDef().equals(rrdDef)) {
//		            println("Checking RRD file structure... OK");
//		        } else {
//		            println("Invalid RRD file created. This is a serious bug, bailing out");
//		            return;
//		        }
//		        rrdDb.close();
//
//		        // Reopen and get ready to update
//		        rrdDb = new RrdDb(rrdPath);
//			      println(rrdDb.dump());
//		        Sample sample = rrdDb.createSample();
//
//		        // Add these data points:
//		        // 12:05 12345 km
//		        // 12:10 12357 km
//		        // 12:15 12363 km
//		        // 12:20 12363 km
//		        // 12:25 12363 km
//		        // 12:30 12373 km
//		        // 12:35 12383 km
//		        // 12:40 12393 km
//		        // 12:45 12399 km
//		        // 12:50 12405 km
//		        // 12:55 12411 km
//		        // 13:00 12415 km
//		        // 13:05 12420 km
//		        // 13:10 12422 km
//		        // 13:15 12423 km
//		        final int[] odoData = { 12345, 12357, 12363, 12363, 12363, 12373,
//		                12383, 12393, 12399, 12405, 12411, 12415, 12420, 12422, 12423 };
//		        println("== Adding odometer data");
//		        final int fiveMinutes = 5 * 60;
//		        for (int i = 0; i < odoData.length; ++i) {
//		            // First sample is 12:05, so use i + 1
//		            sample.setTime(START + (i + 1) * fiveMinutes);
//		            sample.setValue(speedSource, odoData[i]);
//		            println("== sample data ");
//		            println(sample.dump());
//		            // Store the value
//		            sample.update();
//		        }
//			    println(rrdDb.dump());
//		        rrdDb.close();
//		        println("== Finished. RRD file updated " + odoData.length + " times");
//
//		        // test read-only access!
//		        rrdDb = new RrdDb(rrdPath, true);
//		        println("File reopen in read-only mode");
//		        println("== Last update time was: " + rrdDb.getLastUpdateTime());
//		        println("== Last info was: " + rrdDb.getInfo());
//
//		        // rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200
//		        // Fetch data at finest resolution (300 sec), which is the default.
//		        println("== Fetch request for the interval at default resolution:");
//		        FetchRequest request = rrdDb.createFetchRequest(AVERAGE, START, END);
//		        println(request.dump());
//		        println("== Fetching data at default resolution");
//		        FetchData fetchData = request.fetchData();
//		        println("== Data fetched, " + fetchData.getRowCount()
//		                + " points obtained");
//		        // This output is different from the tutorial output from rrdtool;
//		        // it includes 920804400:nan but *not* 920809500:nan
//		        println(fetchData.toString());
//
//		        long coarseRes = 1800;
//		        println("== Fetch request for the interval at coarse resolution:");
//		        // START is before the first data point; END is after the last data
//		        // point; ensure start and end are even multiples of the resolution
//		        FetchRequest request2 = rrdDb.createFetchRequest(AVERAGE, START
//		                / coarseRes * coarseRes, END / coarseRes * coarseRes, 1800);
//		        println(request2.dump());
//		        println("== Fetching data at coarse resolution");
//		        FetchData fetchData2 = request2.fetchData();
//		        println("== Data fetched, " + fetchData2.getRowCount()
//		                + " points obtained");
//		        println(fetchData2.toString());
//
//		        // Done with direct access
//		        rrdDb.close();
//
//		        // Graph 1 has units in millis
//		        // rrdtool graph speed.png \
//		        // --start 920804400 --end 920808000 \
//		        // DEF:myspeed=test.rrd:speed:AVERAGE \
//		        // LINE2:myspeed#FF0000
//		        println("== Creating graph 1");
//		        RrdGraphDef gDef1 = new RrdGraphDef();
//		        gDef1.setWidth(IMG_WIDTH);
//		        gDef1.setHeight(IMG_HEIGHT);
//		        String img1Path = Util.getRrd4jDemoPath(FILE + "." + FILE_FORMAT);
//		        gDef1.setFilename(img1Path);
//		        gDef1.setStartTime(START);//920804400);
//		        gDef1.setEndTime(END); //920808000);
//		        gDef1.datasource("myspeed", rrdPath, "speed", AVERAGE);
//		        gDef1.line("myspeed", new Color(0xFF, 0x00, 0x00), "speed");
//		        gDef1.comment("Graph 1\\r");
//		        gDef1.setImageInfo("<img src='%s' width='%d' height = '%d'>");
//		        gDef1.setImageFormat(FILE_FORMAT);
//		        println("Rendering graph 1");
//		        RrdGraph graph1 = new RrdGraph(gDef1);
//		        println(graph1.getRrdGraphInfo().dump());
//		        println("== Graph 1 created");
//
//		        // Graph 2 adjusts the units etc.
//		        // rrdtool graph speed2.png \
//		        // --start 920804400 --end 920808000 \
//		        // --vertical-label m/s \
//		        // DEF:myspeed=test.rrd:speed:AVERAGE \
//		        // CDEF:realspeed=myspeed,1000,\* \
//		        // LINE2:realspeed#FF0000
//		        println("== Creating graph 2");
//		        RrdGraphDef gDef2 = new RrdGraphDef();
//		        gDef2.setWidth(IMG_WIDTH);
//		        gDef2.setHeight(IMG_HEIGHT);
//		        String img2Path = Util.getRrd4jDemoPath(FILE + "2" + "." + FILE_FORMAT);
//		        gDef2.setFilename(img2Path);
//		        gDef2.setStartTime(START);//920804400);
//		        gDef2.setEndTime(END); //920808000);
//		        gDef2.setVerticalLabel("m/s");
//		        gDef2.datasource("myspeed", rrdPath, "speed", AVERAGE);
//		        gDef2.datasource("realspeed", "myspeed,1000,*");
//		        gDef2.line("realspeed", new Color(0xFF, 0x00, 0x00), "realspeed");
//		        gDef2.comment("Graph 2\\r");
//		        gDef2.setImageInfo("<img src='%s' width='%d' height = '%d'>");
//		        gDef2.setImageFormat(FILE_FORMAT);
//		        println("Rendering graph 2");
//		        RrdGraph graph2 = new RrdGraph(gDef2);
//		        println(graph2.getRrdGraphInfo().dump());
//		        println("== Graph 2 created");
//
//		        // Graph 3 is fancier yet
//		        // rrdtool graph speed3.png \
//		        // --start 920804400 --end 920808000 \
//		        // --vertical-label km/h \
//		        // DEF:myspeed=test.rrd:speed:AVERAGE \
//		        // "CDEF:kmh=myspeed,3600,*" \
//		        // CDEF:fast=kmh,100,GT,kmh,0,IF \
//		        // CDEF:good=kmh,100,GT,0,kmh,IF \
//		        // HRULE:100#0000FF:"Maximum allowed" \
//		        // AREA:good#00FF00:"Good speed" \
//		        // AREA:fast#FF0000:"Too fast"
//		        println("== Creating graph 3");
//		        RrdGraphDef gDef3 = new RrdGraphDef();
//		        gDef3.setWidth(IMG_WIDTH);
//		        gDef3.setHeight(IMG_HEIGHT);
//		        String img3Path = Util.getRrd4jDemoPath(FILE + "3" + "." + FILE_FORMAT);
//		        gDef3.setFilename(img3Path);
//		        gDef3.setStartTime(START);//920804400);
//		        gDef3.setEndTime(END); //920808000);
//		        gDef3.setVerticalLabel("km/h");
//		        gDef3.datasource("myspeed", rrdPath, "speed", AVERAGE);
//		        gDef3.datasource("kmh", "myspeed,3600,*");
//		        gDef3.datasource("fast", "kmh,100,GT,kmh,0,IF");
//		        gDef3.datasource("good", "kmh,100,GT,0,kmh,IF");
//		        gDef3.hrule(100, new Color(0x00, 0x00, 0xFF), "Maximum allowed");
//		        gDef3.area("good", new Color(0x00, 0xFF, 0x00), "Good Speed");
//		        gDef3.area("fast", new Color(0xFF, 0x00, 0x00), "Too fast");
//		        gDef3.comment("Graph 3\\r");
//		        gDef3.setImageInfo("<img src='%s' width='%d' height = '%d'>");
//		        gDef3.setImageFormat(FILE_FORMAT);
//		        println("Rendering graph 3");
//		        RrdGraph graph3 = new RrdGraph(gDef3);
//		        println(graph3.getRrdGraphInfo().dump());
//		        println("== Graph 3 created");
//
//		        // Graph 4 is the fanciest
//		        // rrdtool graph speed4.png \
//		        // --start 920804400 --end 920808000 \
//		        // --vertical-label km/h \
//		        // DEF:myspeed=test.rrd:speed:AVERAGE \
//		        // CDEF:nonans=myspeed,UN,0,myspeed,IF \
//		        // CDEF:kmh=nonans,3600,* \
//		        // CDEF:fast=kmh,100,GT,100,0,IF \
//		        // CDEF:over=kmh,100,GT,kmh,100,-,0,IF \
//		        // CDEF:good=kmh,100,GT,0,kmh,IF \
//		        // HRULE:100#0000FF:"Maximum allowed" \
//		        // AREA:good#00FF00:"Good speed" \
//		        // AREA:fast#550000:"Too fast" \
//		        // STACK:over#FF0000:"Over speed"
//		        println("== Creating graph 4");
//		        RrdGraphDef gDef4 = new RrdGraphDef();
//		        gDef4.setWidth(IMG_WIDTH);
//		        gDef4.setHeight(IMG_HEIGHT);
//		        String img4Path = Util.getRrd4jDemoPath(FILE + "4" + "." + FILE_FORMAT);
//		        gDef4.setFilename(img4Path);
//		        gDef4.setStartTime(START);//920804400);
//		        gDef4.setEndTime(END); //920808000);
//		        gDef4.setVerticalLabel("km/h");
//		        gDef4.datasource("myspeed", rrdPath, "speed", AVERAGE);
//		        gDef4.datasource("nonans", "myspeed,UN,0,myspeed,IF");
//		        gDef4.datasource("kmh", "nonans,3600,*");
//		        gDef4.datasource("fast", "kmh,100,GT,100,0,IF");
//		        gDef4.datasource("over", "kmh,100,GT,kmh,100,-,0,IF");
//		        gDef4.datasource("good", "kmh,100,GT,0,kmh,IF");
//		        gDef4.hrule(100, new Color(0x00, 0x00, 0xFF), "Maximum allowed");
//		        gDef4.area("good", new Color(0x00, 0xFF, 0x00), "Good Speed");
//		        gDef4.area("fast", new Color(0x55, 00, 00), "Too fast");
//		        gDef4.stack("over", new Color(0xff, 0x00, 0x00), "Over speed");
//		        gDef4.comment("Graph 4\\r");
//		        gDef4.setImageInfo("<img src='%s' width='%d' height = '%d'>");
//		        gDef4.setImageFormat(FILE_FORMAT);
//		        println("Rendering graph 4");
//		        RrdGraph graph4 = new RrdGraph(gDef4);
//		        println(graph4.getRrdGraphInfo().dump());
//		        println("== Graph 4 created");
//		        
//		        
//		        
//		        
//		        
////= = = = = = = = = =		        
				// FEXME 
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		private void println(String string) {
		     System.out.println(string);
			
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
