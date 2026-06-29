package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPCapabilities;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import java.io.InputStream;

public final class WSP_ConnectReplyPDU extends WSP_PDU {
   private int _sessionIdLength;
   private int _capabilitiesLen;
   private int _headersLen;
   private int _capbOctetCount;

   public WSP_ConnectReplyPDU(boolean connectionless, InputStream data) {
      super(connectionless, data);
      this.init();
   }

   public final WSPHeaders getWSPHeaders(WSPHeaders wspHeader) {
      byte[] headerData = null;
      int headerDataSize = (int)VarLengthInt.decode(super._PDU, 2 + this._sessionIdLength + this._capabilitiesLen);
      if (headerDataSize != 0) {
         headerData = new byte[headerDataSize];
         System.arraycopy(
            super._PDU, 2 + this._sessionIdLength + this._capabilitiesLen + this._headersLen + this._capbOctetCount, headerData, 0, headerData.length
         );
      }

      if (wspHeader == null) {
         return new WSPHeaders(headerData);
      }

      wspHeader.setAttributeList(headerData);
      return wspHeader;
   }

   public final WSPCapabilities getCapabilities(WSPCapabilities wspCapabilities) {
      byte[] capbData = null;
      if (this._capbOctetCount != 0) {
         capbData = new byte[this._capbOctetCount];
         System.arraycopy(super._PDU, 2 + this._sessionIdLength + this._capabilitiesLen + this._headersLen, capbData, 0, capbData.length);
         if (wspCapabilities == null) {
            wspCapabilities = new WSPCapabilities(true);
         }

         wspCapabilities.decodeCapabilitiesData(capbData);
      }

      return wspCapabilities;
   }

   public final long getSessionId() {
      return VarLengthInt.decode(super._PDU, 2);
   }

   private final void init() {
      this._sessionIdLength = VarLengthInt.getVarLengthCount(super._PDU, 2);
      this._capabilitiesLen = VarLengthInt.getVarLengthCount(super._PDU, 2 + this._sessionIdLength);
      this._headersLen = VarLengthInt.getVarLengthCount(super._PDU, 2 + this._sessionIdLength + this._capabilitiesLen);
      this._capbOctetCount = (int)VarLengthInt.decode(super._PDU, 2 + this._sessionIdLength);
   }
}
