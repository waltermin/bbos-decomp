package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

final class QuickContactMain {
   static final long EVENT_LOG_GUID = 5258489903148434177L;

   public static final void libMain(String[] args) {
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(QuickContactList.getInstance());
      }

      RIMModelFactoryRepository.addFactory(9021823141602707590L, new QuickContactItemRegistrationFactory());
      EventLogger.register(5258489903148434177L, "quickcontacts", 2);
   }
}
