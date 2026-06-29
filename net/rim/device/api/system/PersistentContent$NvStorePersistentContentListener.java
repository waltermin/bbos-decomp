package net.rim.device.api.system;

import net.rim.device.internal.system.NvStore;

class PersistentContent$NvStorePersistentContentListener implements PersistentContentListener {
   private PersistentContent$NvStorePersistentContentListener() {
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      NvStore.persistentContentModeChanged();
   }

   PersistentContent$NvStorePersistentContentListener(PersistentContent$1 x0) {
      this();
   }
}
