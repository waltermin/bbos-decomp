package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PhoneListener;

class AbstractCallEventHandler implements PhoneListener {
   private int _order;
   private AbstractCallEventHandler _next;
   private boolean _registered;
   private static final long HANDLERS_GUID;
   private static AbstractCallEventHandler _first;

   protected AbstractCallEventHandler(int order) {
      this._order = order;
   }

   public final void register() {
      if (this._order >= 100 && this._order <= 2000) {
         internalRegister(this);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void deregister() {
      this._registered = false;
      this.onDeregistration();
   }

   public final boolean isRegistered() {
      return this._registered;
   }

   protected void onRegistration() {
   }

   protected void onDeregistration() {
   }

   protected final PhoneListener getNext() {
      AbstractCallEventHandler next = this._next;

      while (!next.isRegistered()) {
         next = next._next;
      }

      return next;
   }

   static void internalRegister(AbstractCallEventHandler handler) {
      if (handler._next != null) {
         if (!handler._registered) {
            handler._registered = true;
            handler.onRegistration();
         }
      } else {
         int order = handler._order;
         AbstractCallEventHandler previous = null;

         AbstractCallEventHandler next;
         for (next = getFirstHandler(); next != null; next = next._next) {
            if (next._order == order) {
               AbstractCallEventHandler removed = next;
               next = removed._next;
               removed._next = null;
               removed.deregister();
               break;
            }

            if (next._order > order) {
               break;
            }

            previous = next;
         }

         handler._next = next;
         if (previous == null) {
            setFirstHandler(handler);
         } else {
            previous._next = handler;
         }

         handler._registered = true;
         handler.onRegistration();
      }
   }

   private static AbstractCallEventHandler getFirstHandler() {
      if (_first == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _first = (AbstractCallEventHandler)ar.getOrWaitFor(-3955160615553205537L);
      }

      return _first;
   }

   private static void setFirstHandler(AbstractCallEventHandler handler) {
      _first = handler;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.replace(-3955160615553205537L, _first);
   }

   @Override
   public void dtmfData(int _1) {
      throw null;
   }

   @Override
   public void callOTAStatusUpdated(int _1, int _2) {
      throw null;
   }

   @Override
   public void callVoicePrivacyUpdated(int _1, boolean _2) {
      throw null;
   }

   @Override
   public void callTransferStateUpdated(int _1, int _2) {
      throw null;
   }

   @Override
   public void callTransferred(int _1, int _2) {
      throw null;
   }

   @Override
   public void callRemoved(int _1) {
      throw null;
   }

   @Override
   public void callAdded(int _1) {
      throw null;
   }

   @Override
   public void callResumed(int _1) {
      throw null;
   }

   @Override
   public void callHeld(int _1) {
      throw null;
   }

   @Override
   public void callDisconnected(int _1) {
      throw null;
   }

   @Override
   public void callManipulateFailed(int _1, int _2) {
      throw null;
   }

   @Override
   public void callDelivered(int _1) {
      throw null;
   }

   @Override
   public void callFailed(int _1, int _2) {
      throw null;
   }

   @Override
   public void callConnected(int _1) {
      throw null;
   }

   @Override
   public void callInitiated(int _1) {
      throw null;
   }

   @Override
   public void callWaiting(int _1) {
      throw null;
   }

   @Override
   public void callDisplayUpdated(int _1) {
      throw null;
   }

   @Override
   public void callIncoming(int _1) {
      throw null;
   }

   @Override
   public void callTimerUpdated(int _1, int _2) {
      throw null;
   }

   @Override
   public void alternateLinesUpdated() {
      throw null;
   }

   @Override
   public void voicemailCountUpdated(int _1, int _2) {
      throw null;
   }

   @Override
   public void voiceLineChanged(int _1) {
      throw null;
   }

   @Override
   public void responseEnableFDN(int _1) {
      throw null;
   }

   @Override
   public void featureReady() {
      throw null;
   }

   @Override
   public void ssUssDisplay(byte[] _1, int _2, boolean _3) {
      throw null;
   }

   @Override
   public void ssNotification(int _1) {
      throw null;
   }

   @Override
   public void ssUpdated(int _1, int _2) {
      throw null;
   }

   @Override
   public void ssPasswordRequested(int _1) {
      throw null;
   }

   @Override
   public void ssRequestInvalidPassword() {
      throw null;
   }

   @Override
   public void ssRequestReleased(boolean _1) {
      throw null;
   }

   @Override
   public void ssRequestRejected(boolean _1) {
      throw null;
   }

   @Override
   public void ssRequestFailed(int _1, int _2, boolean _3) {
      throw null;
   }

   @Override
   public void ssRequestSucceeded(int _1, int _2, int _3, int _4, boolean _5, boolean _6) {
      throw null;
   }
}
