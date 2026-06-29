package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;

public final class WSP_PostPDU extends WSP_PDU {
   public WSP_PostPDU(boolean connectionless, byte tid, WSPHeaders headers, byte[] URI, byte[] pData) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = 96;
      super._dataLength = 2;
      int length = 0;
      int encodedLen = 0;
      if (URI != null) {
         length = URI.length;
      }

      int var9 = VarLengthInt.encode(length, super._uintvar);
      this.appendPDU(super._uintvar, super._uintvar.length - var9, super._uintvar.length);
      length = 0;
      if (headers != null) {
         length = headers.getLength();
      }

      var9 = VarLengthInt.encode(length, super._uintvar);
      this.appendPDU(super._uintvar, super._uintvar.length - var9, super._uintvar.length);
      this.appendPDU(URI);
      if (headers != null) {
         this.appendPDU(headers.getAttributeList());
      }

      this.appendPDU(pData);
   }
}
