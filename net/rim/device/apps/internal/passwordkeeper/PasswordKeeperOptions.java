package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

public final class PasswordKeeperOptions extends OptionsBase {
   private PasswordKeeperOptions$PersistedPasswordKeeperOptions _persistedPasswordKeeperOptions = (PasswordKeeperOptions$PersistedPasswordKeeperOptions)this.getPersistentObject()
      .getContents();
   private static final long PASSWORD_KEEPER_OPTIONS;
   private static final long PASSWORD_KEEPER_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_PASSWORD_KEEPER_OPTIONS;

   private PasswordKeeperOptions() {
   }

   public static final PasswordKeeperOptions getOptions() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      PasswordKeeperOptions options = (PasswordKeeperOptions)registry.get(7004071661761945143L);
      if (options == null) {
         options = new PasswordKeeperOptions();
         registry.put(7004071661761945143L, options);
      }

      return options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(5195381784668223364L);
      synchronized (persistentObject) {
         Object persistedPasswordKeeperOptions = persistentObject.getContents();
         if (persistedPasswordKeeperOptions == null) {
            persistedPasswordKeeperOptions = new PasswordKeeperOptions$PersistedPasswordKeeperOptions(null);
            persistentObject.setContents(persistedPasswordKeeperOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-6828216187231796412L);
         if (syncItem == null) {
            syncItem = new PasswordKeeperOptions$PasswordKeeperOptionsSyncItem(null);
            ar.put(-6828216187231796412L, syncItem);
         }

         return syncItem;
      }
   }

   public final byte[] getHash() {
      return this._persistedPasswordKeeperOptions._hash;
   }

   public final void setHash(byte[] hash) {
      this._persistedPasswordKeeperOptions._hash = hash;
      this.commit();
   }

   public final int getCounter() {
      return this._persistedPasswordKeeperOptions._counter;
   }

   public final void resetCounter() {
      this._persistedPasswordKeeperOptions._counter = 1;
      this.commit();
   }

   public final void incrementCounter() {
      PasswordKeeperOptions$PersistedPasswordKeeperOptions.access$308(this._persistedPasswordKeeperOptions);
      this.commit();
   }

   public final int getPasswordThreshold() {
      return this._persistedPasswordKeeperOptions._threshold;
   }

   public final void setPasswordThreshold(int threshold) {
      this._persistedPasswordKeeperOptions._threshold = threshold;
      this.commit();
   }

   public final int getRandomPasswordLength() {
      return this._persistedPasswordKeeperOptions._randomPasswordLength;
   }

   public final void setRandomPasswordLength(int passwordLength) {
      this._persistedPasswordKeeperOptions._randomPasswordLength = passwordLength;
      this.commit();
   }

   public final boolean getAlpha() {
      return this._persistedPasswordKeeperOptions._alpha;
   }

   public final void setAlpha(boolean alpha) {
      this._persistedPasswordKeeperOptions._alpha = alpha;
      this.commit();
   }

   public final boolean getNumeric() {
      return this._persistedPasswordKeeperOptions._numeric;
   }

   public final void setNumeric(boolean numeric) {
      this._persistedPasswordKeeperOptions._numeric = numeric;
      this.commit();
   }

   public final boolean getSymbol() {
      return this._persistedPasswordKeeperOptions._symbol;
   }

   public final void setSymbol(boolean symbol) {
      this._persistedPasswordKeeperOptions._symbol = symbol;
      this.commit();
   }

   public final boolean getConfirmDelete() {
      return this._persistedPasswordKeeperOptions._confirm;
   }

   public final void setConfirmDelete(boolean confirm) {
      this._persistedPasswordKeeperOptions._confirm = confirm;
      this.commit();
   }

   public final boolean getAllowCopy() {
      return this._persistedPasswordKeeperOptions._allowCopy;
   }

   public final void setAllowCopy(boolean allowCopy) {
      this._persistedPasswordKeeperOptions._allowCopy = allowCopy;
      this.commit();
   }

   public final boolean getShowPassword() {
      return this._persistedPasswordKeeperOptions._showPassword;
   }

   public final void setShowPassword(boolean showPassword) {
      this._persistedPasswordKeeperOptions._showPassword = showPassword;
      this.commit();
   }

   public final boolean getOTASync() {
      return this._persistedPasswordKeeperOptions._otaSync;
   }

   public final void setOTASync(boolean otaSync) {
      this._persistedPasswordKeeperOptions._otaSync = otaSync;
      this.commit();
   }
}
