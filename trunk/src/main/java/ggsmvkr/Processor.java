package ggsmvkr;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Processor {

   DB db;
   RRDs rrds;
   Alert.State[] lastAlerts = new Alert.State[24];
   Alert.State[] submAlerts = new Alert.State[24];
   Map<Integer, AtomicInteger> connectedCounters = new ConcurrentHashMap<Integer, AtomicInteger>();


   Processor(DB _db, RRDs _rrds) {
      this.db = _db;
      this.rrds = _rrds;

      for(int id = 1; id <= 24; ++id) {
         Message msg = this.db.getLastMessage(id);
         this.lastAlerts[id - 1] = msg != null?(new Alert(msg)).getState():new Alert.State();
         Alert sbm = this.db.getSubmittedAlert(id);
         this.submAlerts[id - 1] = sbm != null?sbm.getState():new Alert.State();
         this.submAlerts[id - 1].release(this.lastAlerts[id - 1]);
      }

   }

   public void onConnect(int id, List<ParameterValue> params) {
      this.getConnectedCounter(id).incrementAndGet();
      this.db.putConnection(id, new Date(), params);
      Alert.State[] var3 = this.submAlerts;
      synchronized(this.submAlerts) {
         Alert alert = new Alert(true);
         this.db.putJournalAlert(id, alert);
         this.lastAlerts[id - 1].setConnectFailure(false);
      }
   }

   public void onDisconnect(int id) {
      this.getConnectedCounter(id).decrementAndGet();
      Alert.State[] var2 = this.submAlerts;
      synchronized(this.submAlerts) {
         Alert alert = new Alert(false);
         this.db.putJournalAlert(id, alert);
         this.lastAlerts[id - 1].setConnectFailure(true);
      }
   }

   public boolean isConnected(int id) {
      return this.getConnectedCounter(id).get() > 0;
   }

   AtomicInteger getConnectedCounter(int id) {
      Map<Integer, AtomicInteger> var2 = this.connectedCounters;
      synchronized(this.connectedCounters) {
         if(this.connectedCounters.containsKey(Integer.valueOf(id))) {
            return this.connectedCounters.get(Integer.valueOf(id));
         } else {
            AtomicInteger ai = new AtomicInteger();
            this.connectedCounters.put(Integer.valueOf(id), ai);
            return ai;
         }
      }
   }

   public Message putRegisters(int id, List<RegisterValue> regs) {
      Message msg = this.db.putRegisters(id, regs);
      Alert.State[] var4 = this.submAlerts;
      synchronized(this.submAlerts) {
         Alert.State newAlert = (new Alert(msg)).getState();
         Alert.State oldAlert = this.lastAlerts[id - 1];

         for(long p = 1L; p != 1073741824L; p <<= 1) {
            if((oldAlert.mask & p) == 0L && (newAlert.mask & p) != 0L) {
               this.db.putJournalAlert(id, new Alert(p));
            }
         }

         int var11 = 0;

         while(true) {
            if(var11 >= 4) {
               this.lastAlerts[id - 1] = newAlert;
               this.submAlerts[id - 1].release(newAlert);
               break;
            }

            if(oldAlert.pumpFailCodes[var11] != newAlert.pumpFailCodes[var11] && newAlert.pumpFailCodes[var11] != 0) {
               this.db.putJournalAlert(id, new Alert(var11, newAlert.pumpFailCodes[var11]));
            }

            ++var11;
         }
      }

      this.rrds.updateRRD(id, msg);
      return msg;
   }

   public boolean isSubmitRequired(int id) {
      if(this.lastAlerts[id - 1].mask != this.submAlerts[id - 1].mask) {
         return true;
      } else {
         for(int p = 0; p < 4; ++p) {
            if(this.lastAlerts[id - 1].pumpFailCodes[p] != this.submAlerts[id - 1].pumpFailCodes[p]) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean submit(int id, Operator operator) {
      Alert.State[] var3 = this.submAlerts;
      synchronized(this.submAlerts) {
         Alert.State lastAlert = this.lastAlerts[id - 1];
         Alert.State submAlert = this.submAlerts[id - 1];
         boolean needSubmit = lastAlert.mask != submAlert.mask;
         Alert.State toSubmit = new Alert.State();

         for(long p = 1L; p != 1073741824L; p <<= 1) {
            if((submAlert.mask & p) == 0L && (lastAlert.mask & p) != 0L) {
               toSubmit.mask |= p;
            }
         }

         for(int var12 = 0; var12 < 4; ++var12) {
            if(submAlert.pumpFailCodes[var12] != lastAlert.pumpFailCodes[var12]) {
               toSubmit.pumpFailCodes[var12] = lastAlert.pumpFailCodes[var12];
               needSubmit = true;
            }
         }

         if(needSubmit) {
            this.db.putJournalSubmit(id, operator.getLogin(), new Alert(toSubmit));
            this.submAlerts[id - 1] = lastAlert;
            this.db.putSubmittedAlert(id, new Alert(lastAlert));
         }

         return needSubmit;
      }
   }
}
