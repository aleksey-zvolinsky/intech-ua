package ggsmvkr;

import ggsmvkr.Parameter;
import ggsmvkr.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ParameterValue {

   Parameter par;
   Object value;


   public ParameterValue(Parameter par, Object value) {
      this.par = par;
      this.value = value;
   }

   public Parameter getParameter() {
      return this.par;
   }

   public Object getValue() {
      return this.value;
   }

   public static ParameterValue fromReader(ParameterValue.Reader r) {
      byte parId = r.read();
      Parameter par = Parameter.forId(parId);
      if(par == null) {
         throw new RuntimeException("Unknown parameter id " + String.format("x%02x", new Object[]{Byte.valueOf(parId)}));
      } else {
         Integer value = null;
         switch(par.type()) {
         case 66:
            value = Integer.valueOf(r.read());
            break;
         default:
            new RuntimeException("Unknown parameter type " + String.format("x%02x", new Object[]{Character.valueOf(par.type())}));
         }

         return new ParameterValue(par, value);
      }
   }

   public static ParameterValue fromStream(final Stream ss) {
      return fromReader(new ParameterValue.Reader() {
         public byte read() {
            return ss.read();
         }
      });
   }

   public void toWriter(ParameterValue.Writer w) {
      w.write(this.par.id());
      switch(this.par.type()) {
      case 66:
         w.write(((Integer)this.value).byteValue());
         break;
      default:
         new RuntimeException("Unknown parameter type " + String.format("x%02x", new Object[]{Character.valueOf(this.par.type())}));
      }

   }

   public static ParameterValue fromStream(final InputStream is) {
      return fromReader(new ParameterValue.Reader() {
         public byte read() {
            try {
               return (byte)is.read();
            } catch (IOException var2) {
               throw new RuntimeException("Can\'t read", var2);
            }
         }
      });
   }

   public void toStream(final OutputStream os) {
      this.toWriter(new ParameterValue.Writer() {
         public void write(byte data) {
            try {
               os.write(data);
            } catch (IOException var3) {
               throw new RuntimeException("Can\'t write", var3);
            }
         }
      });
   }

   interface Reader {

      byte read();
   }

   interface Writer {

      void write(byte var1);
   }
}
