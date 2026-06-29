package net.rim.device.apps.internal.task;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class TaskOptions extends OptionsBase {
   private TaskOptions$PersistedTaskOptions _persistedTaskOptions;
   private static final long TASK_OPTIONS_SYNC_ITEM = 6732024294714330815L;
   private static final long PERSISTED_TASK_OPTIONS = -1892585988930736260L;
   public static int SORT_ORDER = 1;
   private static TaskOptions _options;

   private TaskOptions() {
   }

   public static final TaskOptions getOptions() {
      if (_options == null) {
         _options = new TaskOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1892585988930736260L);
      synchronized (persistentObject) {
         this._persistedTaskOptions = (TaskOptions$PersistedTaskOptions)persistentObject.getContents();
         if (this._persistedTaskOptions == null) {
            this._persistedTaskOptions = new TaskOptions$PersistedTaskOptions();
            persistentObject.setContents(this._persistedTaskOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(6732024294714330815L);
         if (syncItem == null) {
            syncItem = new TaskOptions$TaskOptionsSyncItem();
            ar.put(6732024294714330815L, syncItem);
         }

         return syncItem;
      }
   }

   final int getSortOrderIndex() {
      return this._persistedTaskOptions._sortOrderIndex;
   }

   final void setSortOrderIndex(int sortOrderIndex) {
      if (sortOrderIndex != this._persistedTaskOptions._sortOrderIndex) {
         this._persistedTaskOptions._sortOrderIndex = sortOrderIndex;
         this.fireOptionsChanged(SORT_ORDER);
      }
   }

   final boolean getConfirmDelete() {
      return this._persistedTaskOptions._confirmDelete;
   }

   final void setConfirmDelete(boolean confirmDelete) {
      this._persistedTaskOptions._confirmDelete = confirmDelete;
   }

   final long getSnoozeMillis() {
      return this._persistedTaskOptions._snoozeMillis;
   }

   final void setSnoozeMillis(long snoozeMillis) {
      this._persistedTaskOptions._snoozeMillis = snoozeMillis;
   }

   final boolean isWirelessSyncAllowed() {
      return this._persistedTaskOptions._allowWirelessSync;
   }

   final void allowWirelessSync(boolean allow) {
      if (this._persistedTaskOptions._allowWirelessSync != allow) {
         this._persistedTaskOptions._allowWirelessSync = allow;
         SyncManager.getInstance().allowOTASync(TaskCollectionImpl.getInstance(), allow);
      }
   }

   public static final void editOptions() {
      TaskOptionsScreen.showEditOptionsScreen();
   }
}
