package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.RIMPersistentStore;

class MessageBulkMarkOldManager$Worker implements Runnable {
   private Object[] _messagesToMarkOld;
   private boolean _running = false;

   private MessageBulkMarkOldManager$Worker() {
   }

   public boolean init(Object[] messagesToMarkOld) {
      if (messagesToMarkOld != null && !this._running) {
         this._messagesToMarkOld = messagesToMarkOld;
         return true;
      } else {
         return false;
      }
   }

   public boolean isRunning() {
      return this._running;
   }

   @Override
   public void run() {
      synchronized (RIMPersistentStore.getSynchObject()) {
         if (this._messagesToMarkOld != null && this._messagesToMarkOld.length > 0) {
            this._running = true;
            Thread.currentThread().setPriority(1);
            int size = this._messagesToMarkOld.length;

            for (int i = 0; i < size; i++) {
               Object message = this._messagesToMarkOld[i];
               MessageBulkMarkOldManager.markOld(message);
            }

            this._running = false;
            this._messagesToMarkOld = null;
         }
      }
   }

   MessageBulkMarkOldManager$Worker(MessageBulkMarkOldManager$1 x0) {
      this();
   }
}
