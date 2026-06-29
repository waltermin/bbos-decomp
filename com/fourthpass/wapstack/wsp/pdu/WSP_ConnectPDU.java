package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPCapabilities;
import com.fourthpass.wapstack.wsp.WSPHeaders;

public final class WSP_ConnectPDU extends WSP_PDU {
   public WSP_ConnectPDU(boolean connectionless, byte tid, WSPHeaders headers, WSPCapabilities capb) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = 1;
      super._PDU[2] = 16;
      int current = 2;
      byte[] capbData = capb.getCapabilitiesData();
      if (capbData != null && capbData.length != 0) {
         super._dataLength = (short)(current + 1);
         int encodedLen = VarLengthInt.encode(capbData.length, super._uintvar);
         this.appendPDU(super._uintvar, super._uintvar.length - encodedLen, super._uintvar.length);
         current += encodedLen;
      } else {
         super._PDU[++current] = 0;
         super._dataLength = (short)(current + 1);
      }

      if (headers.getLength() == 0) {
         super._PDU[++current] = 0;
         super._dataLength = (short)(current + 1);
      } else {
         int encodedLen = VarLengthInt.encode(headers.getLength(), super._uintvar);
         this.appendPDU(super._uintvar, super._uintvar.length - encodedLen, super._uintvar.length);
         current += encodedLen;
      }

      this.appendPDU(capbData);
      this.appendPDU(headers.getAttributeList());
   }
}
