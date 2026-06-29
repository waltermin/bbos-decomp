package net.rim.device.internal.crypto;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;

class WipeablePolicyCryptoBlock$MyPersistentContentListener implements PersistentContentListener {
   private WipeablePolicyCryptoBlock$MyPersistentContentListener() {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 1 && !WipeablePolicyCryptoBlock.isWLANKeyAvailable() && NvStore.readData(42) != null) {
         Security.getInstance().deviceUnderAttack();
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      synchronized (WipeablePolicyCryptoBlock._persistentWLANLock) {
         byte[] keyData = WipeablePolicyCryptoBlock.getWLANKeyData();
         Object encoding = PersistentContent.convertByteArrayToEncoding(keyData);
         if (encoding != null) {
            boolean encrypt = WipeablePolicyCryptoBlock.getEncryptFlag();
            if (!PersistentContent.checkEncoding(encoding, false, encrypt)) {
               encoding = PersistentContent.reEncode(encoding, false, encrypt);
               keyData = PersistentContent.convertEncodingToByteArray(encoding);
               WipeablePolicyCryptoBlock.setWLANKeyData(keyData);
            }
         }
      }
   }

   WipeablePolicyCryptoBlock$MyPersistentContentListener(WipeablePolicyCryptoBlock$1 x0) {
      this();
   }
}
