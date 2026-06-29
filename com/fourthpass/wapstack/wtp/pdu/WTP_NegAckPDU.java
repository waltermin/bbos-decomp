package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_NegAckPDU extends WTP_PDU {
   public WTP_NegAckPDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_NegAckPDU(int TID, byte missingPktCount, byte[] pktSequence) {
      super._PDU[0] = 62;
      this.populateTID(TID);
      super._PDU[3] = missingPktCount;

      for (short index = 0; index < missingPktCount; index++) {
         super._PDU[4 + index] = pktSequence[index];
      }

      super._headerLength = (short)(4 + missingPktCount);
   }

   public final int getMissingPktCount() {
      return super._PDU[3] & 0xFF;
   }

   public final int getMissingPktSequenceAt(int i) {
      return super._PDU[3] < i ? 0 : super._PDU[3 + i + 1] & 0xFF;
   }
}
