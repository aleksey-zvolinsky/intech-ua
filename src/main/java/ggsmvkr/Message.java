package ggsmvkr;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Message {

   Date date;
//   Message.Packet packet = new Message.Packet(); // FEXME message
   Message.Tank tank = new Message.Tank();
   Message.Pump[] pumps = new Message.Pump[4];
   ChannelInfo.Type type;
   int flags;
   static final int ZELIO_PUMP_INVERT_MASK = 48;


   Message(Date date, List data) {
      this.type = ChannelInfo.Type.Zelio;
      this.date = date;
      Iterator p = data.iterator();

      while(p.hasNext()) {
         RegisterValue mode = (RegisterValue)p.next();
         int newflags;
         switch(Message.NamelessClass495863707.$SwitchMap$ggsmvkr$Register[mode.getRegister().ordinal()]) {
         case 1:
            newflags = mode.getValue() & 3;
            if(newflags >= 0 && newflags <= 3) {
               if(this.pumps[newflags] == null) {
                  this.pumps[newflags] = new Message.Pump();
               }

               if(this.type == ChannelInfo.Type.Zelio) {
                  int fl = mode.getValue() >> 8;
                  this.pumps[newflags].flags = fl & -49 | ~fl & 48;
               } else if(this.type == ChannelInfo.Type.Kinco) {
                  this.pumps[newflags].flags = mode.getValue() >> 8 & -49 | ~(mode.getValue() >> 8) & 48;
               }

               this.pumps[newflags].flags1 = mode.getValue() & 240;
            }
            break;
         case 2:
            this.tank.status = mode.getValue() & 31;
            this.flags = mode.getValue() >> 5;
            if(mode.getPump() == 1) {
               this.type = ChannelInfo.Type.Kinco;
               newflags = this.flags & 249 | (this.flags & 4) >> 1;
               this.flags = newflags;
            }
            break;
         case 3:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].l1 = mode.getValue();
            break;
         case 4:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].l2 = mode.getValue();
            break;
         case 5:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].l3 = mode.getValue();
            break;
         case 6:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].v = mode.getValue();
            break;
         case 7:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].fc = mode.getValue();
            break;
         case 8:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].moto_hours = mode.getValue();
            break;
         case 9:
            if(this.pumps[mode.getPump()] == null) {
               this.pumps[mode.getPump()] = new Message.Pump();
            }

            this.pumps[mode.getPump()].moto_mins = mode.getValue();
         }
      }

      if(this.type == ChannelInfo.Type.Kinco) {
         for(int var7 = 0; var7 < 4; ++var7) {
            if(this.pumps[var7] != null) {
               int var8 = this.pumps[var7].flags & 12 | this.pumps[var7].flags1 & 64;
               this.pumps[var7].flags &= -13;
               switch(var8) {
               case 0:
                  this.pumps[var7].sc = 1;
                  if(this.pumps[3] == null) {
                     this.pumps[var7].flags |= 8;
                  }
                  break;
               case 4:
                  this.pumps[var7].sc = 2;
                  if(this.pumps[3] != null) {
                     this.pumps[var7].flags |= 4;
                  }
                  break;
               case 8:
                  this.pumps[var7].sc = 3;
                  if(this.pumps[3] != null) {
                     this.pumps[var7].flags |= 8;
                  } else {
                     this.pumps[var7].flags |= 4;
                  }
                  break;
               case 64:
                  this.pumps[var7].sc = 0;
               }
            }
         }
      }

   }

   public Date getDate() {
      return this.date;
   }
   
//   public Message.Packet getPacket() {  // FEXME
//	      return this.packet;
//   }
   
   public Message.Tank getTank() {
      return this.tank;
   }

   public Message.Pump[] getPumps() {
      return this.pumps;
   }

   public boolean isVoltageOK() {
      return (this.flags & 1) != 0;
   }

   public boolean isVoltage2OK() {
      return (this.flags & 8) != 0;
   }

   public boolean isFailure() {
      return (this.flags & 4) != 0;
   }

   public boolean isAutomaticMode() {
      return (this.flags & 2) != 0;
   }

   public boolean isAdjPressed() {
      return (this.flags & 16) != 0;
   }

   public class Tank {

      int status;


      public int getLevel() {
         int level = this.status & 15;
         return level >= 8?4:(level >= 4?3:(level >= 2?2:(level >= 1?1:0)));
      }

      public boolean isFailure() {
         return (this.status & 16) != 0;
      }
   }

   // $FF: synthetic class
   static class NamelessClass495863707 {

      // $FF: synthetic field
      static final int[] $SwitchMap$ggsmvkr$Register = new int[Register.values().length];


      static {
         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsP.ordinal()] = 1;
         } catch (NoSuchFieldError var9) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsS.ordinal()] = 2;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsL1.ordinal()] = 3;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsL2.ordinal()] = 4;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsL3.ordinal()] = 5;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsV.ordinal()] = 6;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsFC.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsMH.ordinal()] = 8;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            $SwitchMap$ggsmvkr$Register[Register.FlagsMM.ordinal()] = 9;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }

   public class Pump {

      int flags;
      int flags1;
      int l1;
      int l2;
      int l3;
      int v;
      int moto_hours;
      int moto_mins;
      int fc;
      int sc;


      public boolean isManualMode() {
         return (this.flags & 1) != 0;
      }

      public boolean isAutomaticMode() {
         return (this.flags & 2) != 0;
      }

      public boolean isWorking() {
         return (this.flags & 4) != 0;
      }

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
         return (this.flags1 & 32) != 0;
      }

      public boolean isModbus1Failure() {
         return (this.flags1 & 64) != 0;
      }

      public boolean isModbus2Failure() {
         return (this.flags1 & 128) != 0;
      }

      public int getStateCode() {
         return this.sc;
      }

      public int getL1() {
         return this.l1;
      }

      public int getL2() {
         return this.l2;
      }

      public int getL3() {
         return this.l3;
      }

      public int getMotoHours() {
         return this.moto_hours;
      }

      public int getMotoMins() {
         return this.moto_mins;
      }

      public PumpFailCode getFailCode() {
         return PumpFailCode.byCode(this.fc);
      }

      public int getV() {
         return this.v;
      }
   }
//   public Packet Packet(){
//	   this.packet.setLevel1(0);
//	   this.packet.setLevel2(0);
//	   this.packet.setLevel3(0);
//	return packet;
//	   
//   }


}
