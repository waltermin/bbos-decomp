package net.rim.device.apps.internal.profiles;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class ProfilesOptions extends OptionsBase {
   private ProfilesOptions$PersistedProfilesOptions _persistedProfilesOptions;
   private static final long PROFILES_OPTIONS_SYNC_ITEM = -6120236007087344572L;
   private static final long PERSISTED_PROFILES_OPTIONS = 373376368149331352L;
   private static ProfilesOptions _options;

   private ProfilesOptions() {
   }

   public static final ProfilesOptions getOptions() {
      if (_options == null) {
         _options = new ProfilesOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(373376368149331352L);
      synchronized (persistentObject) {
         this._persistedProfilesOptions = (ProfilesOptions$PersistedProfilesOptions)persistentObject.getContents();
         if (this._persistedProfilesOptions == null) {
            this._persistedProfilesOptions = new ProfilesOptions$PersistedProfilesOptions();
            persistentObject.setContents(this._persistedProfilesOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-6120236007087344572L);
         if (syncItem == null) {
            syncItem = new ProfilesOptions$ProfilesOptionsSyncItem();
            ar.put(-6120236007087344572L, syncItem);
         }

         return syncItem;
      }
   }

   final void removeSyncItem() {
      SyncItem syncItem = this.getSyncItem();
      if (syncItem != null) {
         SyncManager manager = SyncManager.getInstance();
         if (manager != null) {
            label35:
            try {
               manager.disableSynchronization(syncItem);
            } finally {
               break label35;
            }
         }

         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         synchronized (ar) {
            ar.remove(-6120236007087344572L);
         }
      }
   }

   public final boolean getConfirmDelete() {
      return this._persistedProfilesOptions._confirmDelete;
   }

   public final void setConfirmDelete(boolean confirmDelete) {
      this._persistedProfilesOptions._confirmDelete = confirmDelete;
   }

   public final boolean isBacklightOptionEnabled() {
      return this._persistedProfilesOptions._isBacklightOptionEnabled;
   }

   public final void enableBackLightOption(boolean enabled) {
      this._persistedProfilesOptions._isBacklightOptionEnabled = enabled;
   }
}
