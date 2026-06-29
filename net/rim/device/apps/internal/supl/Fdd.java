package net.rim.device.apps.internal.supl;

final class Fdd extends ModeSpecificInfo {
   private short primaryCPICHInfo;
   private byte cpichEcN0;
   private byte cpichRscp;
   private byte pathLoss;
   private byte optionals;
   static final byte FDD_OPT_CPICH_EC_N0;
   static final byte FDD_OPT_CPICH_RSCP;
   static final byte FDD_OPT_PATH_LOSS;
   static final byte NUM_OPT_ELEMENTS;
   static final byte PRIMARY_CPICH_INFO_BIT_SIZE;
   static final byte CPICH_EC_N0_BIT_SIZE;
   static final byte CPICH_RSCP_BIT_SIZE;

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 3);
      stuff.putBits(this.primaryCPICHInfo, 9);
      if ((this.optionals & 4) == 4) {
         stuff.putBits(this.cpichEcN0, 6);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.cpichRscp, 7);
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.pathLoss, 7);
      }
   }

   @Override
   final void print() {
      System.out.println("Fdd: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      System.out.println(((StringBuffer)(new Object("Primary CPICH Info: "))).append(this.primaryCPICHInfo).toString());
      if ((this.optionals & 4) == 4) {
         System.out.println(((StringBuffer)(new Object("CpichEcN0: "))).append(this.cpichEcN0).toString());
      }

      if ((this.optionals & 2) == 2) {
         System.out.println(((StringBuffer)(new Object("CpichRscp: "))).append(this.cpichRscp).toString());
      }

      if ((this.optionals & 1) == 1) {
         System.out.println(((StringBuffer)(new Object("Path Loss: "))).append(this.pathLoss).toString());
      }
   }
}
