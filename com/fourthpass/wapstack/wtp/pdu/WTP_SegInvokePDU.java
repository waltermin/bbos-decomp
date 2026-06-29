package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.wdp.WDPPacket;

public final class WTP_SegInvokePDU extends WTP_PDU {
   public WTP_SegInvokePDU(WDPPacket wdpPacket) {
      super(wdpPacket);
   }

   public WTP_SegInvokePDU(int TID, int packetSeqNo, boolean lastInGroup, boolean lastPacket) {
      super._PDU[0] = (byte)(40 + (lastInGroup & !lastPacket ? 4 : 0) + (lastPacket ? 2 : 0));
      this.populateTID(TID);
      super._PDU[3] = (byte)packetSeqNo;
      super._headerLength = 4;
   }
}
