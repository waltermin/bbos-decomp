package net.rim.device.internal.crypto;

import net.rim.device.api.system.LED;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;

class CryptoBlock$CryptoBlockKey$MyPersistentContentListener implements PersistentContentListener {
   private CryptoBlock$CryptoBlockKey$MyPersistentContentListener() {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 1
         && (CryptoBlock$CryptoBlockKey._deviceKey == null || CryptoBlock$CryptoBlockKey._deviceKey[0] == null)
         && !CryptoBlock$CryptoBlockKey.areMasterKeysAvailable()) {
         Security.getInstance().deviceUnderAttack();
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      synchronized (CryptoBlock$CryptoBlockKey._persistentDeviceKey) {
         Object encoding = CryptoBlock$CryptoBlockKey._persistentDeviceKey.getContents();
         if (encoding != null) {
            boolean encrypt = CryptoBlock$CryptoBlockKey.getEncryptFlag();
            NvStore.setFlag(16, encrypt);
            if (!PersistentContent.checkEncoding(encoding, false, encrypt)) {
               encoding = PersistentContent.reEncode(encoding, false, encrypt);
               CryptoBlock$CryptoBlockKey.setDeviceKey(encoding, false);
            }

            if (!CryptoBlock$CryptoBlockKey.areMasterKeysAvailable()) {
               LED.setConfiguration(50, 950, 2);
               LED.setState(2);
            }
         }
      }
   }

   CryptoBlock$CryptoBlockKey$MyPersistentContentListener(CryptoBlock$1 x0) {
      this();
   }
}
