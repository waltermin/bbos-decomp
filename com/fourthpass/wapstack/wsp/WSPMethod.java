package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.wsp.pdu.WSP_ConfirmedPushPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_GetPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PostPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PushPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_ReplyPDU;
import com.fourthpass.wapstack.wtp.WTPLayer;
import com.fourthpass.wapstack.wtp.WTP_Transaction_Initiator;
import java.io.InputStream;

public final class WSPMethod implements IWapStackLayer {
   private byte _trClass;
   private WSP_PDU _methodPDU;
   private WTP_Transaction_Initiator _wtpTransaction;
   private byte _status = -1;

   public final void execute(IWapStackLayer wapSession, WTPLayer wtpLayer) {
      this._wtpTransaction = wtpLayer.requestTransaction(wapSession, this._trClass);
      wtpLayer.registerRequestor(this._wtpTransaction, this);
      this._wtpTransaction.sendInvokeRequest(this._methodPDU.getDataToBeTransmitted());
   }

   public final byte getAbortReason() {
      return this._wtpTransaction != null ? this._wtpTransaction.getAbortReason() : 0;
   }

   public final void abort() {
      if (this._wtpTransaction != null) {
         this._wtpTransaction.abortTransaction();
      }
   }

   public final byte getStatus() {
      return this._status;
   }

   public final InputStream getResult(WSPHeaders resultHeader) {
      if (this._wtpTransaction == null) {
         return null;
      }

      while (true) {
         synchronized (this._wtpTransaction._transactionStateSync) {
            if (this._wtpTransaction.isAborted()) {
               return null;
            }

            if (this._wtpTransaction.isComplete() || this._wtpTransaction.getState() == 2) {
               break;
            }

            try {
               this._wtpTransaction._transactionStateSync.wait();
            } finally {
               continue;
            }
         }
      }

      InputStream result = this._wtpTransaction.getResultIndication(120000);
      if (result == null) {
         return null;
      }

      try {
         WSP_ReplyPDU replyPDU = new WSP_ReplyPDU(false, result);
         replyPDU.getHeaderInfo(resultHeader);
         this._status = replyPDU.getStatusCode();
         return replyPDU.getData();
      } finally {
         ;
      }
   }

   @Override
   public final void close() {
   }

   @Override
   public final void eventOccured(Object object) {
   }

   @Override
   public final void setUserLayer(IWapStackLayer userLayer) {
      if (this._wtpTransaction != null) {
         this._wtpTransaction.getWTPLayerInstance().registerRequestor(this._wtpTransaction, this);
      }
   }

   public WSPMethod(IWapStackLayer wapSession, WTPLayer wtpLayer, byte[] URI, WSPHeaders header, byte methodType, byte[] pData) {
      if (wapSession instanceof WSPClientSession) {
         header = ((WSPClientSession)wapSession).processHeaders(header);
      }

      this.createMethodPDU(URI, header, methodType, pData);
   }

   private final void createMethodPDU(byte[] URI, WSPHeaders header, byte methodType, byte[] pData) {
      if (methodType >= 64 && methodType <= 68) {
         this._methodPDU = new WSP_GetPDU(false, (byte)0, methodType, header, URI);
         this._trClass = 2;
      } else if (methodType == 96 || methodType == 97) {
         this._methodPDU = new WSP_PostPDU(false, (byte)0, header, URI, pData);
         this._trClass = 2;
      } else if (methodType == 6) {
         this._methodPDU = new WSP_PushPDU(false, (byte)0, header, pData);
         this._trClass = 0;
      } else if (methodType == 7) {
         this._methodPDU = new WSP_ConfirmedPushPDU(false, (byte)0, header, pData);
         this._trClass = 1;
      }
   }
}
