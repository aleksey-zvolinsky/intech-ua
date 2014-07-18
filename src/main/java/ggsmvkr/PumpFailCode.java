package ggsmvkr;


public enum PumpFailCode {

   None("None", 0, 0, "", ""),
   Code01("Code01", 1, 1, "UCF", "Недогрузка по току"),
   Code02("Code02", 2, 2, "OCF", "Перегрузка по току"),
   Code03("Code03", 3, 3, "PHbd", "Ассиметрия фаз"),
   Code04("Code04", 4, 4, "GrdF", "Отключение по току истока"),
   Code05("Code05", 5, 5, "OLF", "Перегрузка двигателя"),
   Code06("Code06", 6, 6, "OtF", "Перегрев двигателя"),
   Code07("Code07", 7, 7, "OHF", "Перегрев двигателя (РТС)"),
   Code08("Code08", 8, 8, "PIF", "Очередность фаз"),
   Code09("Code09", 9, 9, "PHF", "Обрыв фазы"),
   Code10("Code10", 10, 10, "USF", "Низкое напряжение или его отсутствие"),
   Code11("Code11", 11, 11, "OSF", "Високое напряжение"),
   Code12("Code12", 12, 12, "StF", "Ошибка несоответствия времени пуска"),
   Code13("Code13", 13, 13, "SnbF", "Слишком большое число пусков"),
   Code14("Code14", 14, 14, "SSCr", "Замикание тиристоров или неправильное подключение"),
   Code15("Code15", 15, 15, "EtF", "Внешняя неисправность"),
   Code16("Code16", 16, 16, "InF", "Внутреняя неисправность"),
   Code17("Code17", 17, 17, "SLF", "Тайм-аут ModBus"),
   Code18("Code18", 18, 18, "trAp", "Код прерывания"),
   Code19("Code19", 19, 19, "SCF", "Короткое замыкание"),
   Code20("Code20", 20, 20, "bPF", "Неисправность байпасного контактора"),
   Code21("Code21", 21, 21, "CFF", "Некорhектная конфигурация");
   int code;
   String nik;
   String name;
   // $FF: synthetic field
   private static final PumpFailCode[] $VALUES = new PumpFailCode[]{None, Code01, Code02, Code03, Code04, Code05, Code06, Code07, Code08, Code09, Code10, Code11, Code12, Code13, Code14, Code15, Code16, Code17, Code18, Code19, Code20, Code21};


   private PumpFailCode(String var1, int var2, int code, String nik, String name) {
      this.code = code;
      this.nik = nik;
      this.name = name;
   }

   public int getCode() {
      return this.code;
   }

   public String getNik() {
      return this.nik;
   }

   public String getName() {
      return this.name;
   }

   static PumpFailCode byCode(int code) {
      PumpFailCode[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         PumpFailCode pfc = arr$[i$];
         if(code == pfc.code) {
            return pfc;
         }
      }

      return null;
   }

}
