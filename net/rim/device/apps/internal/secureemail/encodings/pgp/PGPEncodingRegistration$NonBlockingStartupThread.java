package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.PGPKeyStore;

class PGPEncodingRegistration$NonBlockingStartupThread extends Thread {
   public PGPEncodingRegistration$NonBlockingStartupThread() {
   }

   @Override
   public void run() {
      this.setPriority(1);
      KeyStoreIndex[] indexArray = new Object[]{new Object(), new Object(), new Object(), new Object()};
      PGPKeyStore.getInstance().addIndices(indexArray);
   }
}
