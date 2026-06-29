package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.util.RedirectEvent;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.wsp.pdu.WSP_ConnectPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_ConnectReplyPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_DisconnectPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_RedirectPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_ReplyPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_ResumePDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_SuspendPDU;
import com.fourthpass.wapstack.wtp.WTP_Transaction_Initiator;
import net.rim.device.api.io.http.HttpHeaders;

public final class WSPClientSession extends WSPSession {
   private WTP_Transaction_Initiator _connectionTr;
   private WTP_Transaction_Initiator _resumeTr;
   private UserDefinedEvent _redirectionEvent;
   private WSP_ReplyPDU _replyPDU;
   private HttpHeaders _cachedHeaders;
   private static final int CONNECT;
   private static final int RESUME;
   private static final int METHOD;

   public WSPClientSession(WSPLayer instance, IWapStackLayer requestor, WSPAddress localAddress, WSPAddress destAddress) {
      super(requestor, instance, localAddress, destAddress);
   }

   public final void invokeMethod(WSPMethod method) {
      method.execute(this, super._wspLayer.getWTPLayerInstance());
      super._registeredMethods.addElement(method);
   }

   public final boolean connectRequest(WSPHeaders headers, WSPCapabilities capabilities) {
      this._replyPDU = null;
      this._connectionTr = super._wspLayer.getWTPLayerInstance().requestTransaction(this, (byte)2);
      super._registeredTransactions.addElement(this._connectionTr);
      this._cachedHeaders = headers.getHeaders();
      this.setState((byte)1);
      WSP_ConnectPDU connPDU = new WSP_ConnectPDU(false, (byte)0, headers, capabilities);
      super._wspLayer.registerSession(this);
      this._connectionTr.sendInvokeRequest(connPDU.getDataToBeTransmitted());
      long timeoutPoint = super._wspLayer.getSessionTimeout() * 1000 + System.currentTimeMillis();
      boolean redirect = false;

      while (true) {
         synchronized (super._sessionStateSync) {
            switch (super._sessionState) {
               case 0:
               case 3:
               case 21:
               case 22:
                  return false;
               case 2:
                  return true;
               case 5:
                  redirect = true;
            }

            if (!redirect) {
               long systime = System.currentTimeMillis();
               if (systime >= timeoutPoint) {
                  return false;
               }

               try {
                  super._sessionStateSync.wait(timeoutPoint - systime);
                  continue;
               } finally {
                  continue;
               }
            }
         }

         if (redirect) {
            this.eventOccured(this._redirectionEvent);
         }

         return false;
      }
   }

   public final boolean ConnectionCnf() {
      return super._sessionState == 2;
   }

   public final boolean ConnectionFailed() {
      if (this._connectionTr == null) {
         return true;
      } else {
         return this._replyPDU != null ? true : this._connectionTr.isAborted();
      }
   }

   public final WSP_ReplyPDU getLastReplyPDU() {
      return this._replyPDU;
   }

   public final void disconnectRequest() {
      if (super._sessionState != 21 && super._sessionState != 22) {
         this.disconnectRegisteredMethods();
         this.disconnectRegisteredTransactions();
         WTP_Transaction_Initiator trInit = super._wspLayer.getWTPLayerInstance().requestTransaction(this, (byte)0);
         super._registeredTransactions.addElement(trInit);
         WSP_DisconnectPDU disconnPDU = new WSP_DisconnectPDU(super._sessionId);
         trInit.sendInvokeRequest(disconnPDU.getDataToBeTransmitted());
         super._wspLayer.unregisterSession(this);
         this.setState((byte)21);
      }
   }

   public final void suspendRequest() {
      if (super._sessionState != 3 && super._sessionState != 22) {
         this.disconnectRegisteredMethods();
         this.disconnectRegisteredTransactions();
         WTP_Transaction_Initiator trInit = super._wspLayer.getWTPLayerInstance().requestTransaction(this, (byte)0);
         super._registeredTransactions.addElement(trInit);
         WSP_SuspendPDU suspendPDU = new WSP_SuspendPDU(super._sessionId);
         trInit.sendInvokeRequest(suspendPDU.getDataToBeTransmitted());
         this.setState((byte)3);
      }
   }

   public final boolean resumeRequest(long sessionId, WSPHeaders headers) {
      this.setState((byte)3);
      super._sessionId = sessionId;
      this._cachedHeaders = headers.getHeaders();
      this._resumeTr = super._wspLayer.getWTPLayerInstance().requestTransaction(this, (byte)2);
      super._registeredTransactions.addElement(this._resumeTr);
      this.setState((byte)4);
      WSP_ResumePDU resumePDU = new WSP_ResumePDU(super._sessionId, null);
      this._resumeTr.sendInvokeRequest(resumePDU.getDataToBeTransmitted());
      long timeoutPoint = super._wspLayer.getSessionTimeout() * 1000 + System.currentTimeMillis();

      while (true) {
         synchronized (super._sessionStateSync) {
            switch (super._sessionState) {
               case 0:
               case 3:
               case 21:
               case 22:
                  return false;
               case 2:
                  return true;
            }

            long systime = System.currentTimeMillis();
            if (systime >= timeoutPoint) {
               return false;
            }

            try {
               super._sessionStateSync.wait(timeoutPoint - systime);
            } finally {
               continue;
            }
         }
      }
   }

   public final boolean resumeCnf() {
      return this.resumeInd();
   }

   public final boolean resumeInd() {
      return super._sessionState == 2;
   }

   @Override
   public final void eventOccured(Object event) {
      if (event instanceof UserDefinedEvent) {
         this.fireUserEvent(event);
      } else if (event instanceof WTP_Transaction_Initiator) {
         if (!super._registeredTransactions.contains(event)) {
            super._registeredTransactions.addElement(event);
         }

         WTP_Transaction_Initiator trInit = (WTP_Transaction_Initiator)event;
         this.processTransactionPDU(trInit, WSP_PDU.PDUFactory(false, trInit.getResultIndication(super._wspLayer.getSessionTimeout() * 1000)));
         if (trInit.isAborted()) {
            this.disconnectRegisteredMethods();
            this.disconnectRegisteredTransactions();
            if (super._sessionState == 4) {
               this.setState((byte)3);
               return;
            }

            this.setState((byte)0);
         }
      }
   }

   private final boolean processTransactionPDU(WTP_Transaction_Initiator trInit, WSP_PDU wspPDU) {
      if (wspPDU == null) {
         return false;
      }

      switch (wspPDU.getPDUType()) {
         case 2:
         default:
            if (super._sessionState == 1) {
               WSP_ConnectReplyPDU replyPDU = (WSP_ConnectReplyPDU)wspPDU;
               super._targetHeader = replyPDU.getWSPHeaders(null);
               super._targetCapabilities = replyPDU.getCapabilities(null);
               long oldId = super._sessionId;
               super._sessionId = replyPDU.getSessionId();
               super._wspLayer.reregisterSession(this, oldId);
               this.setState((byte)2);
            }

            return true;
         case 3:
            if (super._sessionState == 1) {
               WSP_RedirectPDU redirectPdu = (WSP_RedirectPDU)wspPDU;
               WSPAddress[] addresses = redirectPdu.getRedirectAddress();
               RedirectEvent event = new RedirectEvent(redirectPdu.getRedirectAddress(), super._peerAddressQuad.getDestAddress(), redirectPdu.getFlag());
               this._redirectionEvent = new UserDefinedEvent((short)1, event);
               this.setState((byte)5);
            }

            return true;
         case 4:
            this._replyPDU = (WSP_ReplyPDU)wspPDU;
            if (super._sessionState == 1) {
               this.setState((byte)22);
               return false;
            } else if (super._sessionState == 4) {
               this.setState((byte)2);
            }
         case 1:
            return false;
      }
   }

   public final WSPHeaders processHeaders(WSPHeaders headers) {
      if (this._cachedHeaders != null && headers != null && this._cachedHeaders != headers.getHeaders()) {
         HttpHeaders newSet = headers.getHeaders();

         for (int cachedIndex = this._cachedHeaders.size() - 1; cachedIndex >= 0; cachedIndex--) {
            String newSetValue = newSet.getPropertyValue(this._cachedHeaders.getPropertyKey(cachedIndex));
            if (newSetValue != null && newSetValue.equals(this._cachedHeaders.getPropertyValue(cachedIndex))) {
               newSet.removeProperties(this._cachedHeaders.getPropertyKey(cachedIndex));
            }
         }
      }

      return headers;
   }

   public final WSPContextObject invokeConnectCommand(WSPHeaders headers, WSPCapabilities capabilities) {
      WSPContextObject context = new WSPContextObject();
      context.setConnectObjects(headers, capabilities);
      WSPClientSession$WSPExecuteThread thread = new WSPClientSession$WSPExecuteThread(this, 0, context);
      thread.start();
      return context;
   }

   public final WSPContextObject invokeResumeCommand(long sessionId, WSPHeaders headers) {
      WSPContextObject context = new WSPContextObject();
      context.setResumeObjects(sessionId, headers);
      WSPClientSession$WSPExecuteThread thread = new WSPClientSession$WSPExecuteThread(this, 1, context);
      thread.start();
      return context;
   }

   public final WSPContextObject invokeMethodCommand(WSPMethod method, WSPHeaders resultHeaders) {
      WSPContextObject context = new WSPContextObject();
      context.setMethodObjects(method, resultHeaders);
      WSPClientSession$WSPExecuteThread thread = new WSPClientSession$WSPExecuteThread(this, 2, context);
      thread.start();
      return context;
   }
}
