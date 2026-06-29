package net.rim.device.api.system;

import net.rim.device.api.listener.Event;

final class PersistentContent$Listeners$LockChangeEvent implements Event {
   int _lockGeneration;

   PersistentContent$Listeners$LockChangeEvent(int lockGeneration) {
      this._lockGeneration = lockGeneration;
   }

   @Override
   public final Thread preUpdateEventListener() {
      return null;
   }

   @Override
   public final Thread updateEventListener(Object listener) {
      return null;
   }

   @Override
   public final Thread postUpdateEventListener() {
      PersistentContent._instance.lock2(this._lockGeneration);
      return null;
   }
}
