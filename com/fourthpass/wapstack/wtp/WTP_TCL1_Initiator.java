package com.fourthpass.wapstack.wtp;

import com.fourthpass.wapstack.wtp.pdu.WTP_AckPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import java.io.InputStream;

final class WTP_TCL1_Initiator extends WTP_Transaction_Initiator {
   public WTP_TCL1_Initiator(WTPLayer wtpLayer, int TID, boolean TIDnew) {
      super(wtpLayer, TID, TIDnew, (byte)1);
      this.setState((byte)20);
   }

   @Override
   public final boolean sendInvokeRequest(byte[] userData) {
      super._wtpLayer.transmitInvokePDU(this, userData, false, false);
      this.setState((byte)1);
      this.addTimerTask((byte)1, (byte)1, true);
      return true;
   }

   @Override
   public final InputStream getResultIndication(int timeout) {
      return null;
   }

   @Override
   public final boolean receivedPDU(WTP_PDU wtpPDU) {
      switch (wtpPDU.getPDUType()) {
         case 3:
            if (super._trState == 1) {
               WTP_AckPDU ackpdu = (WTP_AckPDU)wtpPDU;
               if (ackpdu.isTveTokSet()) {
                  super._wtpLayer.transmitAckPDU(this, true);
                  super._ackPDUSent = true;
                  this.addTimerTask((byte)1, (byte)1, false);
                  return true;
               }

               this.removeTimerTasks();
               super._wtpLayer.unregisterTransaction(this);
               this.setState((byte)22);
               return true;
            } else {
               if (super._trState == 22) {
                  return true;
               }

               return false;
            }
         case 4:
         case 7:
            this.removeTimerTasks();
            super._wtpLayer.unregisterTransaction(this);
            this.setState((byte)21);
            return true;
         default:
            this.removeTimerTasks();
            super._wtpLayer.unregisterTransaction(this);
            super._wtpLayer.transmitAbortPDU(this, (byte)0, (byte)1);
            this.setState((byte)21);
            return true;
      }
   }

   @Override
   public final synchronized void timerExpired(byte eventId) {
      if (this.isActive()) {
         switch (eventId) {
            case 1:
               if (super._trState == 1) {
                  this.reTransmitPDU(eventId);
                  return;
               }
               break;
            case 22:
               this.removeTimerTasks();
               super._wtpLayer.unregisterTransaction(this);
               if (super._trState != eventId) {
                  this.setState((byte)21);
               }
         }
      }
   }
}
