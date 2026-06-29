package net.rim.device.api.crypto.certificate;

class CleanCache implements Runnable {
   @Override
   public void run() {
      CertificateFactory.cleanCache();
   }
}
