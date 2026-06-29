package com.fourthpass.wapstack.wtp;

import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import java.io.InputStream;

final class WTP_TCL0_Initiator extends WTP_Transaction_Initiator {
   public WTP_TCL0_Initiator(WTPLayer wtpLayer, int TID, boolean TIDnew) {
      super(wtpLayer, TID, TIDnew, (byte)0);
      this.setState((byte)20);
   }

   @Override
   public final boolean sendInvokeRequest(byte[] userData) {
      super._wtpLayer.transmitInvokePDU(this, userData, false, false);
      this.setState((byte)22);
      super._wtpLayer.unregisterTransaction(this);
      return true;
   }

   @Override
   public final InputStream getResultIndication(int timeout) {
      return null;
   }

   @Override
   public final boolean receivedPDU(WTP_PDU wtpPDU) {
      super._wtpLayer.transmitAbortPDU(this, (byte)0, (byte)1);
      return false;
   }

   @Override
   public final void timerExpired(byte eventId) {
   }
}
