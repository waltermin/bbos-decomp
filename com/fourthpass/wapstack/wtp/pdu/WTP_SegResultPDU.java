package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_SegResultPDU extends WTP_PDU {
   public WTP_SegResultPDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_SegResultPDU(int TID, int packetSeqNo) {
      super._PDU[0] = 54;
      this.populateTID(TID);
      super._PDU[3] = (byte)packetSeqNo;
      super._headerLength = 4;
   }

   public final int getPacketSequenceNumber() {
      return super._PDU[3] & 0xFF;
   }

   public final boolean isEndOfGroup() {
      return (super._PDU[0] & 6) == 4;
   }

   public final boolean isEndOfMessage() {
      return (super._PDU[0] & 2) == 2;
   }
}
