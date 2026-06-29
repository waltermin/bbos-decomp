package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.wtp.WTPLayer;
import net.rim.device.api.util.LongHashtable;

public final class WSPLayer implements IWapStackLayer {
   private WTPLayer _wtpLayer;
   private IWapStackLayer _userLayer;
   private int _sessionTimeout;
   private LongHashtable _registeredSessions;

   public final void setSessionTimeout(int timeout) {
      this._sessionTimeout = timeout;
   }

   public final int getSessionTimeout() {
      return this._sessionTimeout;
   }

   public final void reregisterSession(WSPSession session, long oldSessionId) {
      this._registeredSessions.remove(oldSessionId);
      this._registeredSessions.put(session.getSessionId(), session);
   }

   public final WTPLayer getWTPLayerInstance() {
      return this._wtpLayer;
   }

   public final void registerSession(WSPSession session) {
      this._registeredSessions.put(session.getSessionId(), session);
   }

   public final void unregisterSession(WSPSession session) {
      this._registeredSessions.remove(session.getSessionId());
   }

   @Override
   public final void close() {
      this._userLayer = null;
      if (this._wtpLayer != null) {
         this._wtpLayer.close();
      }

      this._registeredSessions.clear();
   }

   @Override
   public final void eventOccured(Object event) {
      if (event instanceof UserDefinedEvent && this._userLayer != null) {
         this._userLayer.eventOccured(event);
      }
   }

   @Override
   public final void setUserLayer(IWapStackLayer userLayer) {
      this._userLayer = userLayer;
   }

   public WSPLayer(WTPLayer wtpLayer, WSPAddress destAddress) {
      this._wtpLayer = wtpLayer;
      this._registeredSessions = new LongHashtable();
      this._sessionTimeout = 60;
      this._wtpLayer.setUserLayer(this);
   }
}
