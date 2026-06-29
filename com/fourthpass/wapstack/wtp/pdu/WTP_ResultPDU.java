package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_ResultPDU extends WTP_PDU {
   public WTP_ResultPDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_ResultPDU(int TID) {
      super._PDU[0] = 22;
      this.populateTID(TID);
      super._headerLength = 3;
   }

   public final boolean isSegmentationPacket() {
      return (super._PDU[0] & 6) == 0 || (super._PDU[0] & 6) == 4;
   }

   public final boolean isEndOfGroup() {
      return (super._PDU[0] & 6) == 4;
   }
}
