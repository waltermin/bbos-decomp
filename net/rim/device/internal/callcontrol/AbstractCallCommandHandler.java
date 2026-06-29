package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Phone;

class AbstractCallCommandHandler extends Phone {
   private int _order;
   private boolean _registered;
   private AbstractCallCommandHandler _next;
   private static final long HANDLERS_GUID;
   private static AbstractCallCommandHandler _first;

   protected AbstractCallCommandHandler(int order) {
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

   protected final Phone getNext() {
      AbstractCallCommandHandler next = this._next;

      while (!next.isRegistered()) {
         next = next._next;
      }

      return next;
   }

   static void internalRegister(AbstractCallCommandHandler handler) {
      if (handler._next != null) {
         if (!handler._registered) {
            handler._registered = true;
            handler.onRegistration();
         }
      } else {
         int order = handler._order;
         AbstractCallCommandHandler previous = null;

         AbstractCallCommandHandler next;
         for (next = getFirstHandler(); next != null; next = next._next) {
            if (next._order == order) {
               AbstractCallCommandHandler removed = next;
               next = removed._next;
               removed._next = null;
               removed.deregister();
               break;
            }

            if (next._order < order) {
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

   private static AbstractCallCommandHandler getFirstHandler() {
      if (_first == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _first = (AbstractCallCommandHandler)ar.getOrWaitFor(-6648725956671550232L);
      }

      return _first;
   }

   private static void setFirstHandler(AbstractCallCommandHandler handler) {
      _first = handler;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.replace(-6648725956671550232L, _first);
   }

   @Override
   public final String getCallName(int callId) {
      return this.getCallName(callId, false);
   }

   @Override
   public final String getCallPhoneNumber(int callId) {
      return this.getCallPhoneNumber(callId, false);
   }
}
