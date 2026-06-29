package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.FIPSPolicy;

public class WTLSOptionStore implements Persistable {
   private boolean _sessionResumption;
   private int _minimumStrongRSAKeySize;
   private int _minimumStrongDHKeySize;
   private int _minimumStrongECCKeySize;
   private boolean _displayServerWarnings;
   private boolean _allowExport;
   private static final int MIN_STRONG_RSA_KEY_LENGTH_DEFAULT = 1000;
   private static final int MIN_STRONG_DH_KEY_LENGTH_DEFAULT = 1024;
   private static final int MIN_STRONG_ECC_KEY_LENGTH_DEFAULT = 163;
   public static final int IPV4_DEVICE_ADDRESS = 0;
   public static final int IPV4_ADDRESS = 1;
   public static final int GSM_DEVICE_MSISDN = 2;
   public static final int GSM_MSISDN = 3;
   public static final int CDMA_DEVICE_MDN = 4;
   public static final int CDMA_DEVICE_15_IMSI = 5;
   public static final int CDMA_IMSI = 6;
   public static final int CDMA_DEVICE_10_IMSI = 7;
   private static final long ID = 8072646474259989019L;
   private static final long SYNC_ID = 2414747479154393285L;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(8072646474259989019L);

   private WTLSOptionStore() {
   }

   private void resetOptions() {
      this._sessionResumption = true;
      this._minimumStrongRSAKeySize = 1000;
      this._minimumStrongDHKeySize = 1024;
      this._minimumStrongECCKeySize = 163;
      this._displayServerWarnings = true;
      this._allowExport = true;
      _persist.commit();
   }

   public static void register() {
      WTLSOptionStore$WTLSOptionsSyncItem wtlsOptionsSyncItem = getOptions().getWTLSOptionsSyncItem();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(wtlsOptionsSyncItem);
      }
   }

   private WTLSOptionStore$WTLSOptionsSyncItem getWTLSOptionsSyncItem() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      WTLSOptionStore$WTLSOptionsSyncItem wtlsOptionsSyncItem = (WTLSOptionStore$WTLSOptionsSyncItem)registry.getOrWaitFor(2414747479154393285L);
      if (wtlsOptionsSyncItem == null) {
         wtlsOptionsSyncItem = new WTLSOptionStore$WTLSOptionsSyncItem(this);
         registry.put(2414747479154393285L, wtlsOptionsSyncItem);
      }

      return wtlsOptionsSyncItem;
   }

   public static WTLSOptionStore getOptions() {
      return (WTLSOptionStore)_persist.getContents();
   }

   public boolean getSessionResumption() {
      return this._sessionResumption;
   }

   public int getMinimumStrongRSAKeySize() {
      int minSize = ITPolicy.getInteger(29, 3, 512);
      return Math.max(this._minimumStrongRSAKeySize, minSize);
   }

   public int getMinimumStrongDHKeySize() {
      int minSize = ITPolicy.getInteger(29, 4, 512);
      return Math.max(this._minimumStrongDHKeySize, minSize);
   }

   public int getMinimumStrongECCKeySize() {
      int minSize = ITPolicy.getInteger(29, 5, 160);
      return Math.max(this._minimumStrongECCKeySize, minSize);
   }

   public boolean allowExportCipherSuites() {
      int value = ITPolicy.getInteger(29, 1, 2);
      return value == 2 ? this._allowExport : value == 1;
   }

   public boolean restrictFIPSCipherSuites() {
      return FIPSPolicy.getBoolean(29, 7, false, true);
   }

   public boolean getDisplayServerCertificateWarnings() {
      return this._displayServerWarnings;
   }

   private void commit() {
      _persist.commit();
      this.getWTLSOptionsSyncItem().fireSyncItemUpdated();
   }

   public void setSessionResumption(boolean sessionResumption) {
      this._sessionResumption = sessionResumption;
      this.commit();
   }

   public void setMinimumStrongRSAKeySize(int keySize) {
      this._minimumStrongRSAKeySize = keySize;
      this.commit();
   }

   public void setMinimumStrongDHKeySize(int keySize) {
      this._minimumStrongDHKeySize = keySize;
      this.commit();
   }

   public void setMinimumStrongECCKeySize(int keySize) {
      this._minimumStrongECCKeySize = keySize;
      this.commit();
   }

   public void setAllowExportCipherSuites(boolean allowExport) {
      this._allowExport = allowExport;
      this.commit();
   }

   public void setDisplayServerCertificateWarnings(boolean value) {
      this._displayServerWarnings = value;
      this.commit();
   }

   static {
      synchronized (_persist) {
         if (!(_persist.getContents() instanceof WTLSOptionStore)) {
            WTLSOptionStore optionsStore = new WTLSOptionStore();
            _persist.setContents(optionsStore, 4801362);
            optionsStore.resetOptions();
         }
      }
   }
}
