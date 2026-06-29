package net.rim.device.api.crypto.keystore;

class CertificateStatusManager$CleanRunnable implements Runnable {
   @Override
   public void run() {
      new StatusClean().start();
   }
}
