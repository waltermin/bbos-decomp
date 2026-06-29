package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.DeviceInfo;

class StatusClean extends Thread {
   private static final long IDLE_TIME;
   private static final long SLEEP_TIME;

   public StatusClean() {
      this.setPriority(1);
   }

   @Override
   public void run() {
      sleepIfNotIdle();
      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      manager.clean();
      manager.setStatusCleanTimer();
   }

   public static void sleepIfNotIdle() {
      while (DeviceInfo.getIdleTime() < 120) {
         try {
            Thread.sleep(120000);
         } finally {
            continue;
         }
      }
   }
}
