package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_InvokePDU extends WTP_PDU {
   public WTP_InvokePDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_InvokePDU(int TID, boolean tidNew, boolean upFlag, byte tcl, boolean sarMode, boolean lastPacket) {
      if (sarMode) {
         super._PDU[0] = (byte)(136 + (lastPacket ? 2 : 0) & 0xFF);
         this.populateTID(TID);
         super._PDU[3] = 0;
         if (tidNew) {
            super._PDU[3] = (byte)(super._PDU[3] + 32);
         }

         if (upFlag) {
            super._PDU[3] = (byte)(super._PDU[3] + 16);
         }

         super._PDU[3] = (byte)(super._PDU[3] + (tcl & 3));
         super._PDU[4] = 19;
         super._PDU[5] = 1;
         super._PDU[6] = -118;
         super._PDU[7] = 120;
         super._headerLength = 8;
      } else {
         super._PDU[0] = (byte)(8 + (lastPacket ? 2 : 0) & 0xFF);
         this.populateTID(TID);
         super._PDU[3] = 0;
         if (tidNew) {
            super._PDU[3] = (byte)(super._PDU[3] + 32);
         }

         if (upFlag) {
            super._PDU[3] = (byte)(super._PDU[3] + 16);
         }

         super._PDU[3] = (byte)(super._PDU[3] + (tcl & 3));
         super._headerLength = 4;
      }
   }
}
