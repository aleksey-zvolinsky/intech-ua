package ggsmvkr;


public enum Register {

   FlagsP("FlagsP", 0, (byte)0),
   FlagsS("FlagsS", 1, (byte)1),
   FlagsV("FlagsV", 2, (byte)4),
   FlagsL1("FlagsL1", 3, (byte)5),
   FlagsL2("FlagsL2", 4, (byte)6),
   FlagsL3("FlagsL3", 5, (byte)7),
   FlagsFC("FlagsFC", 6, (byte)8),
   FlagsMH("FlagsMH", 7, (byte)9),
   FlagsMM("FlagsMM", 8, (byte)10);
   private byte id;
   // $FF: synthetic field
   private static final Register[] $VALUES = new Register[]{FlagsP, FlagsS, FlagsV, FlagsL1, FlagsL2, FlagsL3, FlagsFC, FlagsMH, FlagsMM};


   private Register(String var1, int var2, byte id) {
      this.id = id;
   }

   byte id() {
      return this.id;
   }

   static Register byId(byte id) {
      Register[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Register par = arr$[i$];
         if(id == par.id()) {
            return par;
         }
      }

      return null;
   }

}
