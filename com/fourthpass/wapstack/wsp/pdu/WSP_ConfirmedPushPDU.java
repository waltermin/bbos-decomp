package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import java.io.InputStream;

public final class WSP_ConfirmedPushPDU extends WSP_PDU {
   public WSP_ConfirmedPushPDU(boolean connectionless, InputStream data) {
      byte tid = 0;

      try {
         if (connectionless) {
            tid = (byte)data.read();
         }

         byte type = (byte)data.read();
         long headerLen = VarLengthInt.decodeEx(data);
         super._PDU = new byte[2 + (int)headerLen];
         super._PDU[0] = tid;
         super._PDU[1] = type;
         data.read(super._PDU, 2, (int)headerLen);
         super._connectionLessMode = connectionless;
         super._pduLength = (short)(2 + headerLen);
         super._dataLength = super._pduLength;
         super._pduData = data;
      } finally {
         return;
      }
   }

   public WSP_ConfirmedPushPDU(boolean connectionless, byte tid, WSPHeaders headers, byte[] pData) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = 7;
      if (headers != null && headers.getLength() != 0) {
         super._dataLength = 2;
         int encodedHLen = VarLengthInt.encode(headers.getLength(), super._uintvar);
         this.appendPDU(super._uintvar, super._uintvar.length - encodedHLen, super._uintvar.length);
         if (headers != null) {
            this.appendPDU(headers.getAttributeList());
         }
      } else {
         super._PDU[2] = 0;
         super._dataLength = 3;
      }

      this.appendPDU(pData);
   }
}
