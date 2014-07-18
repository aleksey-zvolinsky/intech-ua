package ggsmvkr;

import ggsmvkr.Register;

public class RegisterValue {

   Register register;
   int value;
   int pump;


   RegisterValue(Register reg, int p, int v) {
      this.register = reg;
      this.pump = p;
      this.value = v;
   }

   public Register getRegister() {
      return this.register;
   }

   public int getValue() {
      return this.value;
   }

   public int getPump() {
      return this.pump;
   }
}
