package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.wtp.WTP_Transaction;
import java.util.Enumeration;
import java.util.Vector;

public class WSPSession implements IWapStackLayer {
   protected Vector _registeredMethods;
   protected Vector _registeredTransactions;
   protected WSPCapabilities _targetCapabilities;
   protected WSPHeaders _targetHeader;
   protected byte _statusCode;
   protected long _sessionId;
   protected WSPLayer _wspLayer;
   protected IWapStackLayer _userLayer;
   protected WSPAddressQuad _peerAddressQuad;
   protected byte _sessionState;
   protected Object _sessionStateSync = new Object();

   public void unregisterMethod(WSPMethod method) {
      if (this._registeredMethods != null) {
         this._registeredMethods.removeElement(method);
      }
   }

   public WSPCapabilities getTargetCapabilities() {
      return this._targetCapabilities;
   }

   public void fireUserEvent(Object event) {
      if (event instanceof UserDefinedEvent && this._userLayer != null) {
         this._userLayer.eventOccured(event);
      } else {
         this._wspLayer.eventOccured(event);
      }
   }

   public void setState(byte state) {
      if (this._sessionState != state) {
         synchronized (this._sessionStateSync) {
            this._sessionState = state;
            this._sessionStateSync.notifyAll();
         }

         this.fireUserEvent(this);
      }
   }

   public void disconnectRegisteredMethods() {
      Enumeration en = this._registeredMethods.elements();

      while (en.hasMoreElements()) {
         WSPMethod method = (WSPMethod)en.nextElement();
         method.abort();
      }

      this._registeredMethods.removeAllElements();
   }

   public void disconnectRegisteredTransactions() {
      Enumeration en = this._registeredTransactions.elements();

      while (en.hasMoreElements()) {
         WTP_Transaction transactions = (WTP_Transaction)en.nextElement();
         transactions.abortTransaction();
      }

      this._registeredTransactions.removeAllElements();
   }

   public long getSessionId() {
      return this._sessionId;
   }

   @Override
   public void close() {
      this._wspLayer.close();
      this._registeredMethods.removeAllElements();
      this._registeredTransactions.removeAllElements();
      this._sessionId = -1;
   }

   @Override
   public void setUserLayer(IWapStackLayer userLayer) {
      this._userLayer = userLayer;
   }

   @Override
   public void eventOccured(Object _1) {
      throw null;
   }

   public WSPSession() {
   }

   public WSPSession(IWapStackLayer user, WSPLayer wspLayer, WSPAddress localAddress, WSPAddress destAddress) {
      this._userLayer = user;
      this._wspLayer = wspLayer;
      this._peerAddressQuad = new WSPAddressQuad(localAddress, destAddress);
      this._statusCode = 0;
      this._sessionState = 0;
      this._registeredMethods = (Vector)(new Object());
      this._registeredTransactions = (Vector)(new Object());
      this._sessionId = -1;
   }
}
