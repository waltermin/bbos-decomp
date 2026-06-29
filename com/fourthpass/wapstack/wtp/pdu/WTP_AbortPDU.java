package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_AbortPDU extends WTP_PDU {
   public WTP_AbortPDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_AbortPDU(byte abortType, int TID, byte abortReason) {
      super._PDU[0] = (byte)(32 + (abortType & 7));
      this.populateTID(TID);
      super._PDU[3] = abortReason;
      super._headerLength = 4;
   }

   public final byte getAbortType() {
      return (byte)(super._PDU[0] & 7);
   }

   public final byte getAbortReason() {
      return super._PDU[3];
   }
}
