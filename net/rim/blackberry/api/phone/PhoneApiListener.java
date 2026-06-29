package net.rim.blackberry.api.phone;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneApiListener implements PhoneEventListener {
   private Vector _listeners = (Vector)(new Object());
   private int _initCallID = -1;
   private static final long ID = 5619741197140605979L;
   private static final int CALL_ADDED = 0;
   private static final int CALL_CONNECTED = 1;
   private static final int CALL_DISCONNECTED = 2;

   static final PhoneApiListener getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      PhoneApiListener pal = (PhoneApiListener)ar.getOrWaitFor(5619741197140605979L);
      if (pal == null) {
         pal = new PhoneApiListener();
         ar.put(5619741197140605979L, pal);
      }

      return pal;
   }

   PhoneApiListener() {
      VoiceServices.addPhoneEventListener(this);
   }

   final void addListener(PhoneListener pl) {
      if (pl != null && !this._listeners.contains(pl)) {
         this._listeners.addElement(pl);
      }
   }

   final synchronized void removeListener(PhoneListener pl) {
      this._listeners.removeElement(pl);
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      for (int i = 0; i < this._listeners.size(); i++) {
         try {
            PhoneListener pl = (PhoneListener)this._listeners.elementAt(i);
            switch (eventId) {
               case 1000:
                  pl.callIncoming(callId);
                  break;
               case 1001:
                  pl.callConnected(callId);
                  break;
               case 1002:
                  pl.callDisconnected(callId);
                  this._initCallID = -1;
                  break;
               case 1003:
                  pl.callHeld(callId);
                  break;
               case 1004:
                  pl.callResumed(callId);
                  break;
               case 1005:
                  pl.callWaiting(callId);
                  break;
               case 1006:
                  if (context instanceof Object) {
                     int reason = context;
                     pl.callFailed(callId, reason);
                  }

                  this._initCallID = -1;
                  break;
               case 1007:
                  pl.callAdded(callId);
                  break;
               case 1008:
                  pl.callRemoved(callId);
                  break;
               case 1100:
                  if (callId != this._initCallID) {
                     pl.callInitiated(callId);
                     this._initCallID = callId;
                  }
                  break;
               case 1110:
                  pl.callAnswered(callId);
                  break;
               case 3001:
                  pl.callConferenceCallEstablished(callId);
                  break;
               case 3003:
                  pl.conferenceCallDisconnected(callId);
                  this._initCallID = -1;
                  break;
               case 3004:
                  pl.callEndedByUser(callId);
                  break;
               case 180000:
                  this._initCallID = -1;
                  break;
               case 201000:
                  pl.callDirectConnectConnected(callId);
                  break;
               case 201010:
                  pl.callDirectConnectDisconnected(callId);
                  this._initCallID = -1;
            }
         } finally {
            continue;
         }
      }
   }
}
