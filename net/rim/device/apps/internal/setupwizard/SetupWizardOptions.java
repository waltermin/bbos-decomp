package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class SetupWizardOptions extends OptionsBase {
   private SetupWizardOptions$PersistedOptions _persistedOptions;
   private static final long SYNC_ITEM = 1589882223105669807L;
   private static final long PERSISTED_OPTIONS = -1311642877708701802L;
   private static SetupWizardOptions _options;

   private SetupWizardOptions() {
   }

   public static final SetupWizardOptions getOptions() {
      if (_options == null) {
         _options = new SetupWizardOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1311642877708701802L);
      synchronized (persistentObject) {
         this._persistedOptions = (SetupWizardOptions$PersistedOptions)persistentObject.getContents();
         if (this._persistedOptions == null) {
            this._persistedOptions = new SetupWizardOptions$PersistedOptions();
            persistentObject.setContents(this._persistedOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SetupWizardOptions$OptionsSyncItem syncItem = (SetupWizardOptions$OptionsSyncItem)ar.get(1589882223105669807L);
         if (syncItem == null) {
            syncItem = new SetupWizardOptions$OptionsSyncItem();
            ar.put(1589882223105669807L, syncItem);
         }

         return syncItem;
      }
   }

   final boolean getSetupWizardDesired() {
      return this._persistedOptions._offerSetupWizard;
   }

   final void setSetupWizardDesired(boolean val) {
      this._persistedOptions._offerSetupWizard = val;
   }

   final int[] getPageOptions(byte key) {
      int[] values = new int[0];
      if (this._persistedOptions._wizardPageOptions.containsKey(key)) {
         values = (int[])this._persistedOptions._wizardPageOptions.get(key);
      }

      return values;
   }

   final void setPageOptions(byte key, int[] values) {
      this._persistedOptions._wizardPageOptions.put(key, values);
   }

   final boolean getLoggingEnabled() {
      return this._persistedOptions._loggingEnabled;
   }

   final void setLoggingEnabled(boolean val) {
      this._persistedOptions._loggingEnabled = val;
   }

   final boolean getWizardCompleted() {
      return this._persistedOptions._wizardCompleted;
   }

   final void setWizardCompleted(boolean val) {
      this._persistedOptions._wizardCompleted = val;
   }
}
