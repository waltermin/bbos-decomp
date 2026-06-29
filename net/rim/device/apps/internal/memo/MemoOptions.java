package net.rim.device.apps.internal.memo;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class MemoOptions extends OptionsBase {
   private MemoOptions$PersistedMemoOptions _persistedMemoOptions;
   private static final long MEMO_OPTIONS_SYNC_ITEM = -8592906356569155066L;
   private static final long PERSISTED_MEMO_OPTIONS = -1985181723073357990L;
   private static MemoOptions _options;

   private MemoOptions() {
   }

   public static final MemoOptions getOptions() {
      if (_options == null) {
         _options = new MemoOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1985181723073357990L);
      synchronized (persistentObject) {
         this._persistedMemoOptions = (MemoOptions$PersistedMemoOptions)persistentObject.getContents();
         if (this._persistedMemoOptions == null) {
            this._persistedMemoOptions = new MemoOptions$PersistedMemoOptions();
            persistentObject.setContents(this._persistedMemoOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-8592906356569155066L);
         if (syncItem == null) {
            syncItem = new MemoOptions$MemoOptionsSyncItem();
            ar.put(-8592906356569155066L, syncItem);
         }

         return syncItem;
      }
   }

   final boolean getConfirmDelete() {
      return this._persistedMemoOptions._confirmDelete;
   }

   final void setConfirmDelete(boolean confirmDelete) {
      this._persistedMemoOptions._confirmDelete = confirmDelete;
   }

   final boolean isWirelessSyncAllowed() {
      return this._persistedMemoOptions._allowWirelessSync;
   }

   final void allowWirelessSync(boolean allow) {
      if (this._persistedMemoOptions._allowWirelessSync != allow) {
         this._persistedMemoOptions._allowWirelessSync = allow;
         SyncManager.getInstance().allowOTASync(MemoCollectionImpl.getInstance(), allow);
      }
   }

   public static final void editOptions() {
      MemoOptionsScreen.showEditOptionsScreen();
   }
}
