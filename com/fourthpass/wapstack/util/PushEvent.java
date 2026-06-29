package com.fourthpass.wapstack.util;

import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;

public final class PushEvent {
   private WTP_PDU _pushData;

   public PushEvent(WTP_PDU wtpPdu) {
      this._pushData = wtpPdu;
   }

   public final WTP_PDU getPushData() {
      return this._pushData;
   }
}
