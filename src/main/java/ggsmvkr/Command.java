package ggsmvkr;


enum Command {

   Ok("Ok", 0, (byte)119),
   Fail("Fail", 1, (byte)0),
   Quit("Quit", 2, (byte)113),
   Offer("Offer", 3, (byte)111),
   Ask("Ask", 4, (byte)97),
   Nop("Nop", 5, (byte)110);
   private final byte id;
   // $FF: synthetic field
   static final Command[] $VALUES = new Command[]{Ok, Fail, Quit, Offer, Ask, Nop};


   private Command(String var1, int var2, byte id) {
      this.id = id;
   }

   byte id() {
      return this.id;
   }

   static Command forId(byte id) {
      Command[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Command cmd = arr$[i$];
         if(id == cmd.id()) {
            return cmd;
         }
      }

      return null;
   }

}
