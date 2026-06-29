package net.rim.device.api.system;

import net.rim.device.api.listener.Event;

final class PersistentContent$Listeners$ModeChangeEvent implements Event {
   private int _modeGeneration;
   private Object _ticket;

   PersistentContent$Listeners$ModeChangeEvent(int modeGeneration) {
      this._modeGeneration = modeGeneration;
      this._ticket = PersistentContent.getTicket();
   }

   @Override
   public final Thread preUpdateEventListener() {
      PersistentContent._instance.setMode(this._modeGeneration);
      return null;
   }

   @Override
   public final Thread updateEventListener(Object listener) {
      if (this._modeGeneration == PersistentContent.getModeGeneration()) {
         Thread thread = new PersistentContent$Listeners$ModeChangeThread((PersistentContentListener)listener, this._modeGeneration, this._ticket);
         thread.start();
         return thread;
      } else {
         return null;
      }
   }

   @Override
   public final Thread postUpdateEventListener() {
      PersistentContent._instance.setModeComplete(this._modeGeneration);
      this._ticket = null;
      return null;
   }
}
