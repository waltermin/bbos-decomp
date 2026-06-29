package net.rim.device.apps.internal.supl;

final class CellMeasuredResults {
   private int cellId;
   private ModeSpecificInfo info;
   private byte optionals;
   static final byte CELL_MEAS_RES_OPT_CELL_ID = 1;
   static final byte NUM_OPT_ELEMENTS = 1;
   static final byte CELL_ID_BIT_SIZE = 28;

   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 1);
      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.cellId, 28);
      }

      this.info.encode(stuff);
   }

   final void print() {
      System.out.println("Cell Measured Results: ");
      System.out.println("Optionals: " + this.optionals);
      if ((this.optionals & 1) == 1) {
         System.out.println("Cell Id: " + this.cellId);
      }

      this.info.print();
   }
}
