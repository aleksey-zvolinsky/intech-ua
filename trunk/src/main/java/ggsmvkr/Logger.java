package ggsmvkr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

   String prefix;
   static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


   public Logger(String prefix) {
      this.prefix = prefix;
   }

   public void beginLog(String message) {
      Date cdate = new Date();
      System.out.print(df.format(cdate) + " " + this.prefix + " " + message);
      System.out.flush();
   }

   public void continueLog(String message) {
      System.out.print(message);
      System.out.flush();
   }

   public void endLog(String message) {
      System.out.println(message);
   }

   public void log(String message) {
      this.beginLog(message);
      this.endLog("");
   }

}
