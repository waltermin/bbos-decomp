package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.apps.internal.secureemail.KeyUsageEmailAddressKeyStoreIndex;
import net.rim.device.apps.internal.secureemail.KeyUsagePrivateKeysKeyStoreIndex;

class PGPEncodingRegistration$NonBlockingStartupThread extends Thread {
   public PGPEncodingRegistration$NonBlockingStartupThread() {
   }

   @Override
   public void run() {
      this.setPriority(1);
      KeyStoreIndex[] indexArray = new KeyStoreIndex[]{
         new CertificateKeyStoreIndex(), new PGPKeyIDKeyStoreIndex(), new KeyUsageEmailAddressKeyStoreIndex(), new KeyUsagePrivateKeysKeyStoreIndex()
      };
      PGPKeyStore.getInstance().addIndices(indexArray);
   }
}
