package net.rim.device.api.crypto.keystore;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.cldc.io.ippp.SocketTransportBase;

public final class KeyStoreOptions {
   private static final long SINGLETON_ID;

   private KeyStoreOptions() {
   }

   public static final void setPassphraseTimeout(long timeout) {
      KeyStoreManagerHelper.getInstance().setPassphraseTimeout(timeout);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final long getPassphraseTimeout() {
      return KeyStoreManagerHelper.getInstance().getPassphraseTimeout();
   }

   public static final void setAllowBackupRestore(boolean allow) {
      KeyStoreManagerHelper.getInstance().setAllowBackupRestore(allow);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final boolean getAllowBackupRestore() {
      return KeyStoreManagerHelper.getInstance().getAllowBackupRestore();
   }

   public static final void setStaleTime(long staleTime) {
      KeyStoreManagerHelper.getInstance().setStaleTime(staleTime);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final void setAllowUnverifiedCRLs(boolean allow) {
      KeyStoreManagerHelper.getInstance().setAllowUnverifiedCRLs(allow);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final boolean getAllowUnverifiedCRLs() {
      return KeyStoreManagerHelper.getInstance().getAllowUnverifiedCRLs();
   }

   public static final void setKeyStoreAddressInjectorEnabled(boolean enabled) {
      KeyStoreManagerHelper.getInstance().setKeyStoreAddressInjectorEnabled(enabled);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final boolean getKeyStoreAddressInjectorEnabled() {
      return KeyStoreManagerHelper.getInstance().getKeyStoreAddressInjectorEnabled();
   }

   public static final String getCertificateServiceUID() {
      String certificateServiceUID = KeyStoreManagerHelper.getInstance().getCertificateServiceUID();
      if (certificateServiceUID != null && SocketTransportBase.getTypeByUid(certificateServiceUID) == 0) {
         return certificateServiceUID;
      } else {
         String firstCorporateServiceUID = SocketTransportBase.getFirstUidByIpppType(0);
         if (firstCorporateServiceUID != null) {
            setCertificateServiceUID(firstCorporateServiceUID);
            return firstCorporateServiceUID;
         } else {
            return null;
         }
      }
   }

   public static final void setCertificateServiceUID(String certificateServiceUID) {
      KeyStoreManagerHelper.getInstance().setCertificateServiceUID(certificateServiceUID);
      getKeyStoreOptionsSyncItem().fireSyncItemUpdated();
   }

   public static final void register() {
      KeyStoreOptions$KeyStoreOptionsSyncItem keyStoreOptionsSyncItem = getKeyStoreOptionsSyncItem();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(keyStoreOptionsSyncItem);
      }
   }

   private static final KeyStoreOptions$KeyStoreOptionsSyncItem getKeyStoreOptionsSyncItem() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      KeyStoreOptions$KeyStoreOptionsSyncItem keyStoreOptionsSyncItem = (KeyStoreOptions$KeyStoreOptionsSyncItem)registry.getOrWaitFor(-3208094400127735410L);
      if (keyStoreOptionsSyncItem == null) {
         keyStoreOptionsSyncItem = new KeyStoreOptions$KeyStoreOptionsSyncItem();
         registry.put(-3208094400127735410L, keyStoreOptionsSyncItem);
      }

      return keyStoreOptionsSyncItem;
   }
}
