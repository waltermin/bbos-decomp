package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import java.io.InputStream;

public final class WSP_PushPDU extends WSP_PDU {
   private int _headersLen;
   private int _headersSize;

   public WSP_PushPDU(boolean connectionless, InputStream data) {
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
         this._headersSize = (int)headerLen;
         this._headersLen = 0;
         super._pduLength = (short)(2 + headerLen);
         super._dataLength = super._pduLength;
         super._pduData = data;
      } finally {
         return;
      }
   }

   public WSP_PushPDU(boolean connectionless, byte tid, WSPHeaders headers, byte[] pData) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = 6;
      super._dataLength = 2;
      if (headers != null && headers.getLength() != 0) {
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

   public final byte[] getHeader() {
      byte[] headerInfo = null;
      if (this._headersSize > 0) {
         headerInfo = new byte[this._headersSize];
         System.arraycopy(super._PDU, 2 + this._headersLen, headerInfo, 0, headerInfo.length);
      }

      return headerInfo;
   }

   public final InputStream getData() {
      return super._pduData;
   }
}
