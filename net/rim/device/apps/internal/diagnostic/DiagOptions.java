package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class DiagOptions extends OptionsBase {
   private RecipientDepot persistedOptions = (RecipientDepot)this.getPersistentObject().getContents();
   private static final long DIAG_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_DIAG_OPTIONS;
   private static DiagOptions _options;

   public static final DiagOptions getOptions() {
      if (_options == null) {
         _options = new DiagOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(6230820399280227102L);
      synchronized (persistentObject) {
         Object persistentOptions = persistentObject.getContents();
         if (persistentOptions == null) {
            persistentOptions = new RecipientDepot();
            persistentObject.setContents(persistentOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-7659755219736269004L);
         if (syncItem == null) {
            syncItem = new DiagOptions$DiagOptionsSyncItem();
            ar.put(-7659755219736269004L, syncItem);
         }

         return syncItem;
      }
   }

   final void setEmailRecpt(String email) {
      this.persistedOptions.setEmailRecpt(email);
      _options.commit();
   }

   final void setPinRecpt(String pin) {
      this.persistedOptions.setPinRecpt(pin);
      _options.commit();
   }

   final String getEmailRecpt() {
      return this.persistedOptions.getEmailRecpt();
   }

   final String getPinRecpt() {
      return this.persistedOptions.getPinRecpt();
   }
}
