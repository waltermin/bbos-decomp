package net.rim.device.api.crypto.keystore;

class KeyStoreCertificateThread extends Thread {
   @Override
   public void run() {
      CertificateRepository.getInstance().expandCertificates();
   }
}
