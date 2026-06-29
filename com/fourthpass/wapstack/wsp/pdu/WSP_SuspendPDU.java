package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import java.io.InputStream;

public final class WSP_SuspendPDU extends WSP_PDU {
   public WSP_SuspendPDU(boolean connectionless, InputStream data) {
      super(connectionless, data);
   }

   public WSP_SuspendPDU(long sessionId) {
      super._PDU[0] = 0;
      super._PDU[1] = 8;
      super._dataLength = 2;
      int eclen = VarLengthInt.encode(sessionId, super._uintvar);
      this.appendPDU(super._uintvar, super._uintvar.length - eclen, super._uintvar.length);
   }
}
