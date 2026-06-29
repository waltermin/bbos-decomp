package net.rim.device.api.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;

public class SyncManager {
   public static final long APP_REG_KEY = 8853100293560663175L;
   public static final long GUID_KICK_SERIAL_SYNC_DAEMON = -4783788168994715579L;
   public static final long GUID_SET_SERIAL_SYNC_STATUS_MESSAGE = -6549094840388549801L;

   protected SyncManager() {
   }

   public static SyncManager getInstance() {
      return (SyncManager)ApplicationRegistry.getApplicationRegistry().waitForStartup(8853100293560663175L);
   }

   public void enableSynchronization(SyncCollection _1) {
      throw null;
   }

   public void enableSynchronization(SyncCollection _1, boolean _2) {
      throw null;
   }

   public void enableSynchronization(SyncCollection _1, boolean _2, int _3) {
      throw null;
   }

   public void disableSynchronization(SyncCollection _1) {
      throw null;
   }

   public void allowOTASync(SyncCollection _1, boolean _2) {
      throw null;
   }

   public boolean isOTASyncAvailable(SyncCollection _1, boolean _2) {
      throw null;
   }

   public void enableOTASync(boolean _1) {
      throw null;
   }

   public void addSyncEventListener(SyncEventListener _1) {
      throw null;
   }

   public void removeSyncEventListener(SyncEventListener _1) {
      throw null;
   }

   public void syncImmediately(SyncCollection _1) {
      throw null;
   }

   public boolean isCollectionResetSupported() {
      throw null;
   }

   public boolean isSyncCompleted(SyncCollection _1) {
      throw null;
   }

   public void triggerSlowSync(SyncCollection _1) {
      throw null;
   }

   public boolean isSerialSyncInProgress() {
      throw null;
   }

   public void setSerialSyncStatusMessage(String _1) {
      throw null;
   }

   public String getLocalizedCollectionName(long _1, String _3, Locale _4) {
      throw null;
   }
}
