package net.rim.device.internal.synchronization;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.synchronization.ota.OTASyncDaemon;

public final class Registration {
   public static final void SynchronizationMain() {
      OTASyncDaemon.initialize();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(8853100293560663175L, new SyncManagerImpl());
      ar.put(-8492808042306585331L, new SerialSyncManagerImpl());
      SerialSyncDaemon.initialize();
   }
}
