package net.rim.device.apps.internal.supl;

final class Tdd extends ModeSpecificInfo {
   private byte cellParametersId;
   private byte proposedTgsn;
   private byte primaryCcpchRscp;
   private byte pathLoss;
   private byte numTimeslotIscpListItems;
   private byte[] timeslotIscpList;
   private byte optionals;
   static final byte TDD_OPT_TGSN = 8;
   static final byte TDD_OPT_PRI_CCPCH_RSCP = 4;
   static final byte TDD_OPT_PATH_LOSS = 2;
   static final byte TDD_OPT_TS_ISCP_LIST = 1;
   static final byte NUM_OPT_ELEMENTS = 4;
   static final byte CELL_PARAM_ID_BIT_SIZE = 7;
   static final byte TGSN_BIT_SIZE = 4;
   static final byte PRI_CCPCH_RSCP_BIT_SIZE = 7;
   static final byte TS_ISCP_NUM_LIST_ITEMS_BIT_SIZE = 4;
   static final byte TS_ISCP_ELEMENT_BIT_SIZE = 7;

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 4);
      stuff.putBits(this.cellParametersId, 7);
      if ((this.optionals & 8) == 8) {
         stuff.putBits(this.proposedTgsn, 4);
      }

      if ((this.optionals & 4) == 4) {
         stuff.putBits(this.primaryCcpchRscp, 7);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.pathLoss, 7);
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.numTimeslotIscpListItems, 4);

         for (byte i = 0; i < this.numTimeslotIscpListItems; i++) {
            stuff.putBits(this.timeslotIscpList[i], 7);
         }
      }
   }

   @Override
   final void print() {
      System.out.println("Tdd: ");
      System.out.println("Optionals: " + this.optionals);
      System.out.println("Cell Parameters Id: " + this.cellParametersId);
      if ((this.optionals & 8) == 8) {
         System.out.println("Proposed Tgsn: " + this.proposedTgsn);
      }

      if ((this.optionals & 4) == 4) {
         System.out.println("Primary CcpchRscp: " + this.primaryCcpchRscp);
      }

      if ((this.optionals & 2) == 2) {
         System.out.println("Path Loss: " + this.pathLoss);
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Timeslot TSCP List: ");
         System.out.println("Num Items: " + this.numTimeslotIscpListItems);

         for (int i = 0; i < this.numTimeslotIscpListItems; i++) {
            System.out.println("TS ISCP Item " + i);
            System.out.println("Timeslot ISCP: " + this.timeslotIscpList[i]);
         }
      }
   }
}
