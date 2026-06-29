package net.rim.device.api.system;

import net.rim.vm.Message;

public class ModalEventThread extends Thread {
   private boolean _exiting;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      Application app = Application.getApplication();
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         Message lock = new Message();

         while (!this.shouldExit()) {
            app.processNextMessage(lock);
         }

         var12 = false;
      } finally {
         if (var12) {
            Object lock = app.getAppEventLock();
            synchronized (lock) {
               this._exiting = true;
               lock.notifyAll();
            }
         }
      }

      Object lock = app.getAppEventLock();
      synchronized (lock) {
         this._exiting = true;
         lock.notifyAll();
      }
   }

   public boolean isExiting() {
      return this._exiting;
   }

   protected boolean shouldExit() {
      throw null;
   }
}
