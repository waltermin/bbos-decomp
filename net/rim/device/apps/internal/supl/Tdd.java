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
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      System.out.println(((StringBuffer)(new Object("Cell Parameters Id: "))).append(this.cellParametersId).toString());
      if ((this.optionals & 8) == 8) {
         System.out.println(((StringBuffer)(new Object("Proposed Tgsn: "))).append(this.proposedTgsn).toString());
      }

      if ((this.optionals & 4) == 4) {
         System.out.println(((StringBuffer)(new Object("Primary CcpchRscp: "))).append(this.primaryCcpchRscp).toString());
      }

      if ((this.optionals & 2) == 2) {
         System.out.println(((StringBuffer)(new Object("Path Loss: "))).append(this.pathLoss).toString());
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Timeslot TSCP List: ");
         System.out.println(((StringBuffer)(new Object("Num Items: "))).append(this.numTimeslotIscpListItems).toString());

         for (int i = 0; i < this.numTimeslotIscpListItems; i++) {
            System.out.println(((StringBuffer)(new Object("TS ISCP Item "))).append(i).toString());
            System.out.println(((StringBuffer)(new Object("Timeslot ISCP: "))).append(this.timeslotIscpList[i]).toString());
         }
      }
   }
}
