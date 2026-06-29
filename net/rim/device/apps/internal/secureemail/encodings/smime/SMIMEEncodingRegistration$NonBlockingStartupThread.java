package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

class SMIMEEncodingRegistration$NonBlockingStartupThread extends Thread {
   public SMIMEEncodingRegistration$NonBlockingStartupThread() {
   }

   @Override
   public void run() {
      this.setPriority(1);
      KeyStoreIndex[] indexArray = new Object[]{new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object()};
      DeviceKeyStore.getInstance().addIndices(indexArray);
   }
}
