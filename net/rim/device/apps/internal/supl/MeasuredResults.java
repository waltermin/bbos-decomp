package net.rim.device.apps.internal.supl;

final class MeasuredResults {
   private FrequencyInfo frequencyInfo;
   private byte utraCarrierRssi;
   private byte numCellMeasuredResultListItems;
   private CellMeasuredResults[] cellMeasuredResults;
   private byte optionals;
   static final byte MEAS_RES_OPT_FREQ_INFO = 4;
   static final byte MEAS_RES_OPT_UTRA_CARRIER_RSSI = 2;
   static final byte MEAS_RES_OPT_CELL_MEAS_RESULTS = 1;
   static final byte NUM_OPT_ELEMENTS = 3;
   static final byte UTRA_CARRIER_RSSI_BIT_SIZE = 7;
   static final byte CELL_MEAS_RES_NUM_ITEMS_BIT_SIZE = 5;

   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 3);
      if ((this.optionals & 4) == 4) {
         this.frequencyInfo.encode(stuff);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.utraCarrierRssi, 7);
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.numCellMeasuredResultListItems, 5);

         for (byte i = 0; i < this.numCellMeasuredResultListItems; i++) {
            this.cellMeasuredResults[i].encode(stuff);
         }
      }
   }

   final void print() {
      System.out.println("Measured Results: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      if ((this.optionals & 4) == 4) {
         this.frequencyInfo.print();
      }

      if ((this.optionals & 2) == 2) {
         System.out.println(((StringBuffer)(new Object("Utra Carrier Rssi: "))).append(this.utraCarrierRssi).toString());
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Cell Measured Results List: ");
         System.out.println(((StringBuffer)(new Object("Num Items: "))).append(this.numCellMeasuredResultListItems).toString());

         for (int i = 0; i < this.numCellMeasuredResultListItems; i++) {
            System.out.println(((StringBuffer)(new Object("Cell Meas Result Item "))).append(i).toString());
            this.cellMeasuredResults[i].print();
         }
      }
   }
}
