package net.rim.device.apps.internal.supl;

final class Fdd extends ModeSpecificInfo {
   private short primaryCPICHInfo;
   private byte cpichEcN0;
   private byte cpichRscp;
   private byte pathLoss;
   private byte optionals;
   static final byte FDD_OPT_CPICH_EC_N0 = 4;
   static final byte FDD_OPT_CPICH_RSCP = 2;
   static final byte FDD_OPT_PATH_LOSS = 1;
   static final byte NUM_OPT_ELEMENTS = 3;
   static final byte PRIMARY_CPICH_INFO_BIT_SIZE = 9;
   static final byte CPICH_EC_N0_BIT_SIZE = 6;
   static final byte CPICH_RSCP_BIT_SIZE = 7;

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
      System.out.println("Optionals: " + this.optionals);
      System.out.println("Primary CPICH Info: " + this.primaryCPICHInfo);
      if ((this.optionals & 4) == 4) {
         System.out.println("CpichEcN0: " + this.cpichEcN0);
      }

      if ((this.optionals & 2) == 2) {
         System.out.println("CpichRscp: " + this.cpichRscp);
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Path Loss: " + this.pathLoss);
      }
   }
}
