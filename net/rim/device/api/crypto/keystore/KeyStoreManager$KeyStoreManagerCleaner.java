package net.rim.device.api.crypto.keystore;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;

final class KeyStoreManager$KeyStoreManagerCleaner implements MemoryCleanerListener {
   @Override
   public final boolean cleanNow(int event) {
      if (event != 4 && event != 6 && event != 0) {
         return false;
      }

      KeyStorePasswordManager passwordManager = KeyStorePasswordManager.getInstance();
      return passwordManager.clean();
   }

   @Override
   public final String getDescription() {
      return KeyStoreResources.getString(6048);
   }
}
