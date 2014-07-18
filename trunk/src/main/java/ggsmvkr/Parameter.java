package ggsmvkr;


public enum Parameter {

   SQ("SQ", 0, (byte)81, 'B');
   private byte id;
   private char type;
   // $FF: synthetic field
   private static final Parameter[] $VALUES = new Parameter[]{SQ};


   private Parameter(String var1, int var2, byte id, char type) {
      this.id = id;
      this.type = type;
   }

   byte id() {
      return this.id;
   }

   char type() {
      return this.type;
   }

   static Parameter forId(byte id) {
      Parameter[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Parameter par = arr$[i$];
         if(id == par.id()) {
            return par;
         }
      }

      return null;
   }

}
