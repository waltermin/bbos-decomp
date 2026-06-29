package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;

public final class WSP_GetPDU extends WSP_PDU {
   public WSP_GetPDU(boolean connectionless, byte tid, byte methodType, WSPHeaders headers, byte[] URI) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = methodType;
      int uriLength = 0;
      if (URI != null) {
         uriLength = URI.length;
      }

      if (uriLength == 0) {
         super._PDU[2] = 0;
         super._dataLength = 3;
      } else {
         super._dataLength = 2;
         int enclen = VarLengthInt.encode(uriLength, super._uintvar);
         this.appendPDU(super._uintvar, super._uintvar.length - enclen, super._uintvar.length);
         this.appendPDU(URI);
      }

      if (headers != null) {
         this.appendPDU(headers.getAttributeList());
      }
   }
}
