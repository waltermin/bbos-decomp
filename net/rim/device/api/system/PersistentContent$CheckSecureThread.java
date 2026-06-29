package net.rim.device.api.system;

class PersistentContent$CheckSecureThread extends Thread {
   private PersistentContent$CheckSecureThread() {
   }

   @Override
   public void run() {
      PersistentContent._instance.checkSecureLoop();
   }

   PersistentContent$CheckSecureThread(PersistentContent$1 x0) {
      this();
   }
}
