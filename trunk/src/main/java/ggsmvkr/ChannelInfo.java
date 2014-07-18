package ggsmvkr;


public class ChannelInfo {

   int id;
   ChannelInfo.Type type;
   ChannelInfo.PumpState[] states;


   public ChannelInfo(int _id) {
      this.type = ChannelInfo.Type.None;
      this.states = new ChannelInfo.PumpState[]{ChannelInfo.PumpState.None, ChannelInfo.PumpState.None, ChannelInfo.PumpState.None, ChannelInfo.PumpState.None};
      this.id = _id;
   }

   public int getId() {
      return this.id;
   }

   public ChannelInfo.Type getType() {
      return this.type;
   }

   public void setType(ChannelInfo.Type type) {
      this.type = type;
   }

   public ChannelInfo.PumpState getPumpState(int id) {
      return this.states[id];
   }

   public void setPumpState(int id, ChannelInfo.PumpState state) {
      this.states[id] = state;
   }

   public static enum PumpState {

      None("None", 0, (byte)0),
      Off("Off", 1, (byte)1),
      On("On", 2, (byte)2);
      private final byte id;
      // $FF: synthetic field
      private static final ChannelInfo.PumpState[] $VALUES = new ChannelInfo.PumpState[]{None, Off, On};


      private PumpState(String var1, int var2, byte id) {
         this.id = id;
      }

      public byte id() {
         return this.id;
      }

      static ChannelInfo.PumpState byId(byte id) {
         ChannelInfo.PumpState[] arr$ = values();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ChannelInfo.PumpState state = arr$[i$];
            if(id == state.id()) {
               return state;
            }
         }

         return null;
      }

   }

   public static enum Type {

      None("None", 0, ""),
      Zelio("Zelio", 1, "zelio"),
      Kinco("Kinco", 2, "kinco");
      private String name;
      // $FF: synthetic field
      private static final ChannelInfo.Type[] $VALUES = new ChannelInfo.Type[]{None, Zelio, Kinco};


      private Type(String var1, int var2, String name) {
         this.name = name;
      }

      static ChannelInfo.Type byName(String name) {
         ChannelInfo.Type[] arr$ = values();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ChannelInfo.Type type = arr$[i$];
            if(type.name.equals(name)) {
               return type;
            }
         }

         return null;
      }

      public String getName() {
         return this.name;
      }

   }
}
