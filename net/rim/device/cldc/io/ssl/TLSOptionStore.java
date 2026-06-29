package net.rim.device.cldc.io.ssl;

import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.CloneableVector;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.FIPSPolicy;

public final class TLSOptionStore implements Persistable {
   private boolean _sessionResumption;
   private boolean _promptForCertificate;
   private boolean _promptForCertificateOne;
   private boolean _promptForNoClientCertificate;
   private boolean _promptForDomainName;
   private boolean _promptForCertificateTrust;
   private boolean _disableUntrustedConnections;
   private boolean _disableInvalidConnections;
   private int _minimumStrongRSAKeySize;
   private int _minimumStrongDHKeySize;
   private int _minimumStrongECKeySize;
   private int _minimumStrongDSAKeySize;
   private boolean _allowExport;
   private boolean _restrictFIPS;
   private int _defaultImplementation;
   private boolean _useTLS;
   private boolean _useSSL;
   private boolean _doRedirection;
   private Vector _trustedHosts = (Vector)(new Object());
   private static final long ID;
   private static final long SYNC_ID;
   public static final int PROXY_TLS;
   public static final int DEVICE_TLS;
   private static final int MIN_STRONG_RSA_KEY_LENGTH_DEFAULT;
   private static final int MIN_STRONG_DH_KEY_LENGTH_DEFAULT;
   private static final int MIN_STRONG_ECC_KEY_LENGTH_DEFAULT;
   private static final int MIN_STRONG_DSA_KEY_LENGTH_DEFAULT;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(-8454003508201959L);

   private TLSOptionStore() {
   }

   private final void resetOptions() {
      this._sessionResumption = true;
      this._promptForCertificate = false;
      this._promptForCertificateOne = false;
      this._promptForNoClientCertificate = true;
      this._promptForDomainName = true;
      this._promptForCertificateTrust = true;
      this._disableUntrustedConnections = false;
      this._disableInvalidConnections = false;
      this._minimumStrongRSAKeySize = 1000;
      this._minimumStrongDHKeySize = 1024;
      this._minimumStrongECKeySize = 163;
      this._minimumStrongDSAKeySize = 1024;
      this._allowExport = true;
      this._restrictFIPS = false;
      this._defaultImplementation = 1;
      this._useTLS = true;
      this._useSSL = true;
      _persist.commit();
   }

   public static final void register() {
      TLSOptionStore$TLSOptionsSyncItem tlsOptionsSyncItem = getOptions().getTLSOptionsSyncItem();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(tlsOptionsSyncItem);
      }
   }

   private final TLSOptionStore$TLSOptionsSyncItem getTLSOptionsSyncItem() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      TLSOptionStore$TLSOptionsSyncItem tlsOptionsSyncItem = (TLSOptionStore$TLSOptionsSyncItem)registry.getOrWaitFor(-7331796252605079226L);
      if (tlsOptionsSyncItem == null) {
         tlsOptionsSyncItem = new TLSOptionStore$TLSOptionsSyncItem(this);
         registry.put(-7331796252605079226L, tlsOptionsSyncItem);
      }

      return tlsOptionsSyncItem;
   }

   public static final TLSOptionStore getOptions() {
      return (TLSOptionStore)_persist.getContents();
   }

   public final boolean getSessionResumption() {
      return this._sessionResumption;
   }

   public final boolean getPromptForCertificate() {
      return this._promptForCertificate;
   }

   public final boolean getPromptForCertificateOne() {
      return this._promptForCertificateOne;
   }

   public final boolean getPromptForNoClientCertificate() {
      return this._promptForNoClientCertificate;
   }

   public final int getMinimumStrongRSAKeySize() {
      int minSize = ITPolicy.getInteger(28, 3, 512);
      return Math.max(this._minimumStrongRSAKeySize, minSize);
   }

   public final int getMinimumStrongDHKeySize() {
      int minSize = ITPolicy.getInteger(28, 4, 512);
      return Math.max(this._minimumStrongDHKeySize, minSize);
   }

   public final int getMinimumStrongECKeySize() {
      int minSize = ITPolicy.getInteger(28, 5, 160);
      return Math.max(this._minimumStrongECKeySize, minSize);
   }

   public final int getMinimumStrongDSAKeySize() {
      int minSize = ITPolicy.getInteger(28, 8, 512);
      return Math.max(this._minimumStrongDSAKeySize, minSize);
   }

   public final boolean allowExportCipherSuites() {
      int value = ITPolicy.getInteger(28, 1, 2);
      return value == 2 ? this._allowExport : value == 1;
   }

   public final boolean restrictFIPSCipherSuites() {
      return FIPSPolicy.getBoolean(28, 7, this._restrictFIPS, true);
   }

   public final boolean getPromptForDomainName() {
      return this._promptForDomainName;
   }

   public final boolean getPromptForCertificateTrust() {
      return this._promptForCertificateTrust;
   }

   public final boolean getDisableUntrustedConnections() {
      return this._disableUntrustedConnections;
   }

   public final boolean getDisableInvalidConnections() {
      return this._disableInvalidConnections;
   }

   public final int getDefaultImplementation() {
      return this._defaultImplementation;
   }

   public final boolean useTLS() {
      return this._useTLS;
   }

   public final boolean useSSL() {
      return this._useSSL;
   }

   private final void commit() {
      _persist.commit();
      this.getTLSOptionsSyncItem().fireSyncItemUpdated();
   }

   public final void setSessionResumption(boolean sessionResumption) {
      this._sessionResumption = sessionResumption;
      this.commit();
   }

   public final void setPromptForCertificate(boolean prompt) {
      this._promptForCertificate = prompt;
      this.commit();
   }

   public final void setPromptForCertificateOne(boolean prompt) {
      this._promptForCertificateOne = prompt;
      this.commit();
   }

   public final void setPromptForNoClientCertificate(boolean prompt) {
      this._promptForNoClientCertificate = prompt;
      this.commit();
   }

   public final void setMinimumStrongRSAKeySize(int keySize) {
      this._minimumStrongRSAKeySize = keySize;
      this.commit();
   }

   public final void setMinimumStrongDHKeySize(int keySize) {
      this._minimumStrongDHKeySize = keySize;
      this.commit();
   }

   public final void setMinimumStrongECKeySize(int keySize) {
      this._minimumStrongECKeySize = keySize;
      this.commit();
   }

   public final void setMinimumStrongDSAKeySize(int keySize) {
      this._minimumStrongDSAKeySize = keySize;
      this.commit();
   }

   public final void setAllowExportCipherSuites(boolean allowExport) {
      this._allowExport = allowExport;
      this.commit();
   }

   public final void setRestrictFIPSCipherSuites(boolean restrictFIPS) {
      this._restrictFIPS = restrictFIPS;
      this.commit();
   }

   public final void setPromptForDomainName(boolean prompt) {
      this._promptForDomainName = prompt;
      this.commit();
   }

   public final void setPromptForCertificateTrust(boolean prompt) {
      this._promptForCertificateTrust = prompt;
      this.commit();
   }

   public final void setDisableUntrustedConnections(boolean disable) {
      this._disableUntrustedConnections = disable;
      this.commit();
   }

   public final void setDisableInvalidConnections(boolean disable) {
      this._disableInvalidConnections = disable;
      this.commit();
   }

   public final void setDefaultImplementation(int defaultImplementation) {
      this._defaultImplementation = defaultImplementation;
      this.commit();
   }

   public final void useTLS(boolean use) {
      this._useTLS = use;
      this.commit();
   }

   public final void useSSL(boolean use) {
      this._useSSL = use;
      this.commit();
   }

   public final boolean getDoRedirection() {
      return this._doRedirection;
   }

   public final Vector getTrustedHosts() {
      return CloneableVector.clone(this._trustedHosts);
   }

   public final boolean isTrustedHost(String host) {
      return this._trustedHosts.contains(host);
   }

   public final void setDoRedirection(boolean doRedirection) {
      this._doRedirection = doRedirection;
      this.commit();
   }

   public final void setTrustedHosts(Vector trustedHosts) {
      this._trustedHosts = trustedHosts;
      this.commit();
   }

   public final void addTrustedHost(String host) {
      if (!this._trustedHosts.contains(host)) {
         this._trustedHosts.addElement(host);
         this.commit();
      }
   }

   public final void removeTrustedHost(String host) {
      this._trustedHosts.removeElement(host);
      this.commit();
   }

   public final SSLConnectionOptions getDefaultConnectionOptions() {
      return new SSLConnectionOptions();
   }

   static {
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            TLSOptionStore store = new TLSOptionStore();
            _persist.setContents(store, 51);
            store.resetOptions();
         }
      }
   }
}
