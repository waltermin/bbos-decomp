package net.rim.device.api.system;

import net.rim.device.api.listener.Event;

final class PersistentContent$Listeners$StateChangeEvent implements Event {
   int _state;
   int _lockGeneration;

   PersistentContent$Listeners$StateChangeEvent(int state, int lockGeneration) {
      this._state = state;
      this._lockGeneration = lockGeneration;
   }

   @Override
   public final Thread preUpdateEventListener() {
      return null;
   }

   @Override
   public final Thread updateEventListener(Object listener) {
      if (this._lockGeneration == PersistentContent.getLockGeneration()) {
         ((PersistentContentListener)listener).persistentContentStateChanged(this._state);
      }

      return null;
   }

   @Override
   public final Thread postUpdateEventListener() {
      return null;
   }
}
