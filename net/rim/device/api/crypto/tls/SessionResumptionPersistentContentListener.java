package net.rim.device.api.crypto.tls;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

final class SessionResumptionPersistentContentListener implements PersistentContentListener {
   @Override
   public final void persistentContentModeChanged(int generation) {
      if (PersistentContent.isEncryptionEnabled()) {
         new SessionResumption().removeAllSessions();
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }
}
