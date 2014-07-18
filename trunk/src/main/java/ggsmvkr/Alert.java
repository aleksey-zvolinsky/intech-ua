package ggsmvkr;


public class Alert {

   Alert.SysAlert sys;
   Alert.TankAlert tank;
   Alert.PumpAlert[] pumps;


   Alert() {
      this.sys = new Alert.SysAlert();
      this.tank = new Alert.TankAlert();
      this.pumps = new Alert.PumpAlert[]{new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert()};
   }

   Alert(Message msg) {
      this.sys = new Alert.SysAlert();
      this.tank = new Alert.TankAlert();
      this.pumps = new Alert.PumpAlert[]{new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert()};
      this.sys.flags = ~msg.flags & 9 | msg.flags & 20;
      this.tank.flags = msg.tank.status & 16;

      int p;
      for(p = 0; p < 4; ++p) {
         if(msg.pumps[p] != null) {
            this.pumps[p].flags = msg.pumps[p].flags & 248;
            this.pumps[p].flags |= msg.pumps[p].flags1 >> 5 & 7;
         }
      }

      if(msg.pumps[3] == null) {
         this.sys.flags &= -9;
      }

      if(msg.type == ChannelInfo.Type.Zelio && msg.pumps[2] == null) {
         for(p = 0; p < 2; ++p) {
            this.pumps[p].flags &= -17;
         }
      }

   }

   Alert(Alert.State state) {
      this(state.mask);

      for(int p = 0; p < 4; ++p) {
         this.pumps[p].fc = state.pumpFailCodes[p];
      }

   }

   Alert(boolean connected) {
      this.sys = new Alert.SysAlert();
      this.tank = new Alert.TankAlert();
      this.pumps = new Alert.PumpAlert[]{new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert()};
      if(connected) {
         this.sys.flags |= 128;
      } else {
         this.sys.flags |= 64;
      }

   }

   Alert(long mask) {
      this.sys = new Alert.SysAlert();
      this.tank = new Alert.TankAlert();
      this.pumps = new Alert.PumpAlert[]{new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert()};
      this.sys.flags = (int)(mask & 255L);
      this.tank.flags = (int)(mask >> 8 & 255L);

      for(int p = 0; p < 4; ++p) {
         this.pumps[p].flags = (int)(mask >> 16 + p * 8 & 255L);
      }

   }

   Alert(int p, int fc) {
      this.sys = new Alert.SysAlert();
      this.tank = new Alert.TankAlert();
      this.pumps = new Alert.PumpAlert[]{new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert(), new Alert.PumpAlert()};
      this.pumps[p].fc = fc;
   }

   public Alert.State getState() {
      Alert.State state = new Alert.State();
      state.mask = (long)this.sys.flags;
      state.mask |= (long)(this.tank.flags & 255) << 8;
      state.mask |= (long)this.pumps[0].flags << 16;
      state.mask |= (long)this.pumps[1].flags << 24;
      state.mask |= (long)this.pumps[2].flags << 32;
      state.mask |= (long)this.pumps[3].flags << 40;

      for(int p = 0; p < 4; ++p) {
         state.pumpFailCodes[p] = this.pumps[p].fc;
      }

      return state;
   }

   public Alert.SysAlert getSys() {
      return this.sys;
   }

   public Alert.TankAlert getTank() {
      return this.tank;
   }

   public Alert.PumpAlert[] getPumps() {
      return this.pumps;
   }

   public class PumpAlert {

      int flags;
      int fc;


      public boolean isFailure() {
         return (this.flags & 8) != 0;
      }

      public boolean isEngineFailure() {
         return (this.flags & 16) != 0;
      }

      public boolean isStartFailure() {
         return (this.flags & 32) != 0;
      }

      public boolean isHoursCounterOverflow() {
         return (this.flags & 64) != 0;
      }

      public boolean isQFFailure() {
         return (this.flags & 128) != 0;
      }

      public boolean isModbus0Failure() {
         return (this.flags & 1) != 0;
      }

      public boolean isModbus1Failure() {
         return (this.flags & 2) != 0;
      }

      public boolean isModbus2Failure() {
         return (this.flags & 4) != 0;
      }

      public PumpFailCode getFailCode() {
         return PumpFailCode.byCode(this.fc);
      }
   }

   public class TankAlert {

      int flags;


      public boolean isFailure() {
         return (this.flags & 16) != 0;
      }
   }

   static class State {

      long mask;
      int[] pumpFailCodes = new int[]{0, 0, 0, 0};


      void release(Alert.State st) {
         this.mask &= st.mask;

         for(int p = 0; p < 4; ++p) {
            if(st.pumpFailCodes[p] == 0) {
               this.pumpFailCodes[p] = 0;
            }
         }

      }

      void setConnectFailure(boolean failure) {
         if(failure) {
            this.mask |= 64L;
         } else {
            this.mask &= -65L;
         }

      }
   }

   public class SysAlert {

      int flags;


      public boolean isVoltageFailure() {
         return (this.flags & 1) != 0;
      }

      public boolean isVoltage2Failure() {
         return (this.flags & 8) != 0;
      }

      public boolean isFailure() {
         return (this.flags & 4) != 0;
      }

      public boolean isAdjPressed() {
         return (this.flags & 16) != 0;
      }

      public boolean isConnectFailure() {
         return (this.flags & 64) != 0;
      }

      public boolean isConnectOK() {
         return (this.flags & 128) != 0;
      }
   }
}
