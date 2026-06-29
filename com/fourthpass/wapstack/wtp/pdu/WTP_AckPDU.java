package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_AckPDU extends WTP_PDU {
   public WTP_AckPDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_AckPDU(int TID, boolean tve_tok) {
      super._PDU[0] = 24;
      if (tve_tok) {
         super._PDU[0] = (byte)(super._PDU[0] + 4);
      }

      this.populateTID(TID);
      super._headerLength = 3;
   }

   public final void addPacketSeqNumTPI(byte seqNum) {
      if ((super._PDU[0] & 128) == 0) {
         super._PDU[0] = (byte)(super._PDU[0] | 128);
         super._PDU[3] = 25;
         super._PDU[4] = seqNum;
         super._totalTPILength = 2;
         super._TPICount = 1;
      }
   }

   public final boolean isTveTokSet() {
      return (super._PDU[0] >> 2 & 1) == 1;
   }
}
