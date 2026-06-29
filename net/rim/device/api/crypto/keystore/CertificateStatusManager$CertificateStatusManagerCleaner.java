package net.rim.device.api.crypto.keystore;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;

final class CertificateStatusManager$CertificateStatusManagerCleaner implements MemoryCleanerListener {
   @Override
   public final boolean cleanNow(int event) {
      if (event == 5) {
         CertificateStatusManager manager = CertificateStatusManager.getInstance();
         manager.clean();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final String getDescription() {
      return KeyStoreResources.getString(6084);
   }
}
