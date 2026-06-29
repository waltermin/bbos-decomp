package com.fourthpass.wapstack.ota;

import com.fourthpass.wapstack.wtp.WTPLayer;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import java.io.InputStream;
import net.rim.device.api.io.http.PushInputStream;

public final class WAPPushInputStream extends PushInputStream {
   private int _type;
   private String _source;
   private WTPLayer _wtpLayer;
   private WTP_PDU _wtpPDU;

   public WAPPushInputStream(InputStream sub, int type, String source, WTPLayer wtpLayer, WTP_PDU wtpPDU) {
      super(sub);
      this._type = type;
      this._source = source;
      this._wtpLayer = wtpLayer;
      this._wtpPDU = wtpPDU;
   }

   @Override
   public final String getSource() {
      return this._source;
   }

   @Override
   public final int getConnectionType() {
      return this._type;
   }

   @Override
   public final void decline(int reasonCode) {
      if (this._wtpLayer != null && this._wtpPDU != null) {
         this._wtpLayer.transmitAbortPDU(this._wtpPDU.getTID(), (byte)1, (byte)reasonCode);
      }
   }

   @Override
   public final void accept() {
      if (this._wtpLayer != null && this._wtpPDU != null) {
         this._wtpLayer.transmitResultPDU(this._wtpPDU.getTID());
         this._wtpLayer.transmitAckPDU(this._wtpPDU.getTID(), true);
      }
   }

   @Override
   public final boolean isChannelEncrypted() {
      return false;
   }
}
