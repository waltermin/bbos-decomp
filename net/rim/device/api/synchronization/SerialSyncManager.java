package net.rim.device.api.synchronization;

import net.rim.device.api.system.ApplicationRegistry;

public class SerialSyncManager {
   public static final long APP_REG_KEY = -8492808042306585331L;

   protected SerialSyncManager() {
   }

   public static SerialSyncManager getInstance() {
      return (SerialSyncManager)ApplicationRegistry.getApplicationRegistry().waitForStartup(-8492808042306585331L);
   }

   public void enableSynchronization(SyncCollection _1) {
      throw null;
   }

   public void disableSynchronization(SyncCollection _1) {
      throw null;
   }

   public void addSyncListener(SerialSyncListener _1) {
      throw null;
   }

   public void removeSyncListener(SerialSyncListener _1) {
      throw null;
   }

   public boolean isSyncInProgress() {
      throw null;
   }

   public void setStatusMessage(String _1) {
      throw null;
   }
}
