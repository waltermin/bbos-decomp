package net.rim.device.api.system;

final class PersistentContent$CheckSecureThreadLauncher implements Runnable {
   private PersistentContent$CheckSecureThreadLauncher() {
   }

   @Override
   public final void run() {
      synchronized (PersistentContent._instance) {
         if (PersistentContent._instance._checkSecureThread == null) {
            PersistentContent._instance._checkSecureThread = new PersistentContent$CheckSecureThread(null);
            PersistentContent._instance._checkSecureThread.start();
         }
      }
   }

   PersistentContent$CheckSecureThreadLauncher(PersistentContent$1 x0) {
      this();
   }
}
