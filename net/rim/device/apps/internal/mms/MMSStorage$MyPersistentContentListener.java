package net.rim.device.apps.internal.mms;

import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollectionEncryptor;

final class MMSStorage$MyPersistentContentListener implements PersistentContentListener {
   private MMSStorage$MyPersistentContentListener() {
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      PersistedSortedCollectionEncryptor.crypt(3704547669295631919L, generation);
   }

   MMSStorage$MyPersistentContentListener(MMSStorage$1 x0) {
      this();
   }
}
