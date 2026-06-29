package net.rim.device.apps.internal.supl;

final class WcdmaCellInfo extends CellInformation {
   private short mcc;
   private short mnc;
   private int uc;
   private FrequencyInfo freqInfo;
   private short primaryScrCode;
   private byte numMeasuredResultItems;
   private MeasuredResults[] results;
   private byte optionals;
   static final byte CELL_INFO_OPT_FREQ_INFO = 4;
   static final byte CELL_INFO_OPT_PRI_SCR_CODE = 2;
   static final byte CELL_INFO_OPT_MEAS_RES_LIST = 1;
   static final byte NUM_OPT_ELEMENTS = 3;
   static final byte MCC_MNC_NUM_BITS = 10;
   static final byte UC_NUM_BITS = 28;
   static final byte PRI_SCR_CODE_NUM_BITS = 9;
   static final byte MIN_NUM_MEAS_RES_ITEMS = 1;
   static final byte NUM_MEAS_RES_ITEMS_NUM_BITS = 3;

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(1, 2);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 3);
      stuff.putBits(this.mcc, 10);
      stuff.putBits(this.mnc, 10);
      stuff.putBits(this.uc, 28);
      if ((this.optionals & 4) == 4) {
         this.freqInfo.encode(stuff);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.primaryScrCode, 9);
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.numMeasuredResultItems - 1, 3);

         for (byte i = 0; i < this.numMeasuredResultItems; i++) {
            this.results[i].encode(stuff);
         }
      }
   }

   @Override
   final void print() {
      System.out.println("Wcdma CellInfo");
      System.out.println("Optionals: " + this.optionals);
      System.out.println("MCC: " + this.mcc);
      System.out.println("MNC: " + this.mnc);
      System.out.println("UC: " + this.uc);
      if ((this.optionals & 4) == 4) {
         this.freqInfo.print();
      }

      if ((this.optionals & 2) == 2) {
         System.out.println("Primary Scrambling Code: " + this.primaryScrCode);
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Measure Results List: ");
         System.out.println("Num Items: " + this.numMeasuredResultItems);

         for (int i = 0; i < this.numMeasuredResultItems; i++) {
            System.out.println("Meas Res Item " + i);
            this.results[i].print();
         }
      }
   }
}
