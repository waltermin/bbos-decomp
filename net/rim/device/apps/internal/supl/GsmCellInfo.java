package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GPRSInfo$GPRSCellInfo;
import net.rim.device.api.system.RadioInfo;

final class GsmCellInfo extends CellInformation {
   private short mcc;
   private short mnc;
   private int lac;
   private int ci;
   private byte numNmrElements;
   private NmrElement[] nmr;
   private short ta;
   private byte optionals;
   static final byte CELL_INFO_OPT_NMR = 2;
   static final byte CELL_INFO_OPT_TA = 1;
   static final byte NUM_OPT_ELEMENTS = 2;
   static final byte MNC_MCC_NUM_BITS = 10;
   static final byte LAC_CI_NUM_BITS = 16;
   static final byte TA_NUM_BITS = 8;
   static final byte MIN_NUM_NMR_ELEMENTS = 1;
   static final byte NUM_NMR_ELEMENTS_BITS = 4;

   GsmCellInfo() {
      GPRSInfo$GPRSCellInfo cellInfo = GPRSInfo.getCellInfo();
      int index = RadioInfo.getCurrentNetworkIndex();
      int temp = RadioInfo.getMCC(index);
      this.mcc = this.convertBcdToDec(temp);
      temp = RadioInfo.getMNC(index);
      this.mnc = this.convertBcdToDec(temp);
      this.lac = cellInfo.getLAC();
      this.ci = cellInfo.getCellId();
      this.optionals = 0;
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(0, 2);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 2);
      stuff.putBits(this.mcc, 10);
      stuff.putBits(this.mnc, 10);
      stuff.putBits(this.lac, 16);
      stuff.putBits(this.ci, 16);
      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.numNmrElements - 1, 4);

         for (byte i = 0; i < this.numNmrElements; i++) {
            this.nmr[i].encode(stuff);
         }
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.ta, 8);
      }
   }

   private final short convertBcdToDec(int hexValue) {
      short MAX_NIBBLES = 4;
      int nibbleBitmask = 61440;
      int shiftBy = 12;
      short temp = 0;
      short result = 0;

      for (int i = 0; i < 4; i++) {
         result = (short)(result * 10);
         temp = (short)((hexValue & nibbleBitmask) >> shiftBy);
         nibbleBitmask >>= 4;
         shiftBy -= 4;
         result = (short)(result + temp);
      }

      return result;
   }

   @Override
   final void print() {
      System.out.println("GSM Cell Information:");
      System.out.println("Optionals: " + this.optionals);
      System.out.println("MCC: " + this.mcc);
      System.out.println("MNC: " + this.mnc);
      System.out.println("LAC: " + this.lac);
      System.out.println("CellId: " + this.ci);
      if ((this.optionals & 2) == 2) {
         System.out.println("Nmr Elements: " + this.numNmrElements);

         for (int i = 0; i < this.numNmrElements; i++) {
            System.out.println("Element " + i);
            this.nmr[i].print();
         }
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("TA: " + this.ta);
      }
   }
}
