package net.rim.device.apps.internal.mms;

import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.MMS;
import net.rim.device.apps.internal.mms.model.MMSSyncConverter;
import net.rim.device.internal.proxy.Proxy;

public final class MMSLibrary {
   public static final void libMain(String[] args) {
      ServiceBookSyncCollection sync = ServiceBookSyncCollection.getInstance();
      sync.registerCIDForRestoreDisable("MMS");
      if (MMS.isSupported()) {
         MMSStorage.registerOnceOnSystemStart();
         MMSSync mmsSync = new MMSSync(new MMSSyncConverter());
         SyncManager.getInstance().enableSynchronization(mmsSync);
         MMS.onEnabled(new MMSRegistrationRunnable());
         Proxy.getInstance().addGlobalEventListener(new MMSGlobalEventListener());
      }
   }
}
