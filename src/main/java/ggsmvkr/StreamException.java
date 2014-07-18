package ggsmvkr;


public class StreamException extends RuntimeException {

   public StreamException(String msg) {
      super(msg);
   }

   public StreamException(String msg, Throwable th) {
      super(msg, th);
   }
}
