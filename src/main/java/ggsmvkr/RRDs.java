package ggsmvkr;

import java.awt.Color;
import java.io.IOException;

import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

public class RRDs {

   DB db;


   RRDs(DB db) {
      this.db = db;
   }

   void updateRRD(int id, Message msg) {
      long ctime = (System.currentTimeMillis() + 500L) / 1000L;

      try {
         for(int e = 0; e < 4; ++e) {
            if(msg.pumps[e] != null) {
               this.updatePump(id, ctime, e, msg.pumps[e]);
            }
         }

         this.updateTank(id, ctime, msg.tank);
//         this.updatePacket(id, ctime, msg.packet);         //FEXME
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   void updatePacket(int id, long time,  PacketMessage packet) throws IOException { // FEXME
	      String rrdName = "packet1";
	      if(!this.db.isRrdExists(rrdName)) {
	         this.createPacketRrd(rrdName);
	      }

	      RrdDb rrd = new RrdDb(rrdName);
	      Sample sample = rrd.createSample(time);
	      sample.setValue(0, packet.getLevel1());
	      sample.setValue(1, packet.getLevel2());
	      sample.setValue(2, packet.getLevel3());
	      println("== Last info was: " + rrd.getInfo());
	      println("== RRD ");
	      println(rrd.dump());
	      println("==  sample ");
	      println(sample.dump());
	      println("==  sample ");	      
	      sample.update();
	      rrd.close();
	   }
   
   void updatePump(int id, long time, int p, Message.Pump pump) throws IOException {
      String rrdName = id + "/" + p;
      if(!this.db.isRrdExists(rrdName)) {
         this.createPumpRrd(rrdName);
      }

      RrdDb rrd = new RrdDb(rrdName);
      Sample sample = rrd.createSample(time);
      sample.setValue(0, pump.getV());
      sample.setValue(1, pump.getL1());
      sample.setValue(2, pump.getL2());
      sample.setValue(3, pump.getL3());
      sample.update();
      rrd.close();
   }

   void updateTank(int id, long time, Message.Tank tank) throws IOException {
      String rrdName = id + "/t";
      if(!this.db.isRrdExists(rrdName)) {
         this.createTankRrd(rrdName);
      }

      RrdDb rrd = new RrdDb(rrdName);
      Sample sample = rrd.createSample(time);
      sample.setValue(0, tank.getLevel());
      sample.update();
      rrd.close();
   }

   void createPacketRrd(String name) throws IOException {  //FEXME
	      RrdDef rrdDef = new RrdDef(name, 60L);    // Expect new data every 60 seconds = 1 minute
	      rrdDef.setVersion(2);
	      rrdDef.addDatasource("level1", DsType.GAUGE, 300L, 0.0D, Double.NaN); //every 300 sec=5 min
	      rrdDef.addDatasource("level2", DsType.GAUGE, 300L, 0.0D, Double.NaN); //minvalue = 0
	      rrdDef.addDatasource("level3", DsType.GAUGE, 300L, 0.0D, Double.NaN); //max value = Double.NaN
	      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1, '\ue100'); //каждые 1 минут * 57600dec see http://www.fileformat.info/info/unicode/char/e100/index.htm
	      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 12, 4800);    //каждые 12 минут -- 40 дней
	      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 60, 2400);    //каждые 1 час -- 100 дней
	      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 240, 1200);   //каждые 4 часа -- 200 дней
	      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1440, 600);   //каждые 24 часа = 1 день -- 600 дней
	      RrdDb rrdDb = new RrdDb(rrdDef);
	        // Create and check the database
	        println(rrdDef.dump());
	        println("Estimated file size: " + rrdDef.getEstimatedSize());
	        println("== RRD file created.");
	        if (rrdDb.getRrdDef().equals(rrdDef)) {
	            println("Checking RRD file structure... OK");
	        } else {
	            println("Invalid RRD file created. This is a serious bug, bailing out");
	            return;
	        }

	      rrdDb.close();
	   }
   
   private void println(String dump) {
       System.out.println(dump);

	
}

void createPumpRrd(String name) throws IOException { 
      RrdDef rrdDef = new RrdDef(name, 60L);
      rrdDef.addDatasource("v", DsType.GAUGE, 300L, 0.0D, 600.0D);
      rrdDef.addDatasource("l1", DsType.GAUGE, 300L, 0.0D, 300.0D);
      rrdDef.addDatasource("l2", DsType.GAUGE, 300L, 0.0D, 300.0D);
      rrdDef.addDatasource("l3", DsType.GAUGE, 300L, 0.0D, 300.0D);  
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1, '\ue100'); 	
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 12, 4800);    	
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 60, 2400);		
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 240, 600);		
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1440, 600);	
      RrdDb rrdDb = new RrdDb(rrdDef);
      rrdDb.close();
   } 

   void createTankRrd(String name) throws IOException {
      RrdDef rrdDef = new RrdDef(name, 60L);
      rrdDef.addDatasource("level", DsType.GAUGE, 300L, 0.0D, 10.0D);
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1, '\ue100');
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 12, 4800);
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 60, 2400);
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 240, 600);
      rrdDef.addArchive(ConsolFun.AVERAGE, 0.5D, 1440, 600);
      RrdDb rrdDb = new RrdDb(rrdDef);
      rrdDb.close();
   }

   byte[] makeTankGraph(int id, char type) {
      String rrdName = id + "/t";
      RrdGraphDef graphDef = this.makeGraphDef(type);
      graphDef.datasource("level", rrdName, "level", ConsolFun.AVERAGE);
      graphDef.area("level", new Color(Integer.parseInt("00cc00", 16)), "Level");
      return this.makeGraph(graphDef);
   }

   byte[] makePumpGraph(int id, char type, int pump, boolean v, boolean[] ls) {
      String rrdName = id + "/" + pump;
      RrdGraphDef graphDef = this.makeGraphDef(type);
      if(v) {
         graphDef.datasource("v", rrdName, "v", ConsolFun.AVERAGE);
         graphDef.line("v", new Color(Integer.parseInt("0000ff", 16)), "V", 2.0F);
         graphDef.setMinValue(340.0D);
         graphDef.setMaxValue(440.0D);
         graphDef.setVerticalLabel("V");
      }

      for(int l = 0; l < 3; ++l) {
         if(ls[l]) {
            graphDef.datasource("l" + (l + 1), rrdName, "l" + (l + 1), ConsolFun.AVERAGE);
            graphDef.line("l" + (l + 1), new Color(6553600 + l * 120 * 256), "L" + (l + 1), 2.0F);
            graphDef.setVerticalLabel("A");
         }
      }

      return this.makeGraph(graphDef);
   }

   boolean isPumpExists(int id, int pump) {
      return this.db.isRrdExists(id + "/" + pump);
   }

   boolean isTankExists(int id) {
      return this.db.isRrdExists(id + "/t");
   }

   RrdGraphDef makeGraphDef(char type) {
      RrdGraphDef graphDef = new RrdGraphDef();
      long intervals = 3600L;
      long intervale = 0L;
      switch(type) {
      case 100:
         intervals = 120000L;
         intervale = 150L;
         break;
      case 104:
         intervals = 24000L;
         intervale = 30L;
         break;
      case 109:
         intervals = 2678400L;
         intervale = 750L;
         graphDef.setTimeAxis(11, 6, 5, 1, 5, 1, 86400, "dd.MM");
         break;
      case 119:
         intervals = 604800L;
         intervale = 750L;
         graphDef.setTimeAxis(11, 1, 5, 1, 5, 1, 86400, "dd.MM");
         break;
      case 121:
         intervals = 31622400L;
         intervale = 750L;
         graphDef.setTimeAxis(5, 1, 2, 1, 2, 1, 2592000, "MM.yyyy");
      }

      graphDef.setTimeSpan(-intervals, 0L);
      graphDef.setFilename("-");
      graphDef.setMinValue(0.0D);
      graphDef.setWidth(1600);
      graphDef.setHeight(200);
      return graphDef;
   }

   byte[] makeGraph(RrdGraphDef graphDef) {
      try {
         RrdGraph e = new RrdGraph(graphDef);
         return e.getRrdGraphInfo().getBytes();
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
