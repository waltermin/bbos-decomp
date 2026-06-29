package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import java.io.InputStream;

public final class WSP_ResumePDU extends WSP_PDU {
   public WSP_ResumePDU(boolean connectionless, InputStream data) {
      super(connectionless, data);
   }

   public WSP_ResumePDU(long sessionId, WSPHeaders headers) {
      super._PDU[0] = 0;
      super._PDU[1] = 9;
      super._dataLength = 2;
      int eclen = VarLengthInt.encode(sessionId, super._uintvar);
      this.appendPDU(super._uintvar, super._uintvar.length - eclen, super._uintvar.length);
      super._PDU[super._dataLength++] = 0;
      if (headers != null) {
         this.appendPDU(headers.getAttributeList());
      }
   }
}
