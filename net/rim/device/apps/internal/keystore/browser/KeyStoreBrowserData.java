package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateIconProvider;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;

public final class KeyStoreBrowserData implements MatchProvider {
   private KeyStoreData _keyStoreData;
   private boolean _isDataLoaded;
   private KeyStore _keyStore;
   private KeyStore _trustedKeyStore;
   private CertificateStatusManager _certificateStatusManager;
   private CryptoSystemProperties _cryptoSystemProperties;
   private Certificate _certificate;
   private boolean _isRoot;
   private boolean _isCA;
   private boolean _isEndEntity;
   private Certificate[] _bestCertificateChain;
   private long _bestCertificateChainProperties;
   private String _label;
   private int _statusIcon;
   private static final int VERISIGN_WAP_WTLS_HASH_CODE = 2107895108;
   private static final String VERISIGN_WAP_WTLS_THUMBPRINT = "967C 1CC9 A2A0 2974 740F 7500 1FEA C6D6 C86D 9795";

   public final synchronized boolean loadDataIfNeeded() {
      if (this._isDataLoaded) {
         return false;
      }

      this.loadData();
      return true;
   }

   public final Certificate getCertificate() {
      return this._certificate;
   }

   @Override
   public final int match(Object searchCriteria) {
      if (searchCriteria instanceof Object) {
         String label = this.getKeyStoreData().getLabel();
         if (label != null && label.length() > 0) {
            String searchString = (String)searchCriteria;
            if (StringUtilities.regionMatches(label, true, 0, searchString, 0, searchString.length(), 1701707776)) {
               return 1;
            }
         }
      }

      return 0;
   }

   public final String[] getEmailAddresses() {
      if (this._certificate == null) {
         return null;
      }

      byte[][] emailAddresses = this._keyStoreData.getAssociatedData(-1124699153917633064L);
      if (emailAddresses == null) {
         return null;
      }

      int numAddresses = emailAddresses.length;
      String[] addresses = new Object[numAddresses];

      for (int i = 0; i < numAddresses; i++) {
         addresses[i] = (String)(new Object(emailAddresses[i]));
      }

      return addresses;
   }

   public final void invalidate() {
      this._isDataLoaded = false;
      this._label = null;
      this._statusIcon = 0;
      this._bestCertificateChain = null;
   }

   public final KeyStore getKeyStore() {
      return this._keyStore;
   }

   public final synchronized void verifyCertificateChainsBuilt() {
      if (this._bestCertificateChain == null) {
         Certificate[][] certificateChains = CertificateUtilities.buildCertificateChains(this._certificate, null, this._keyStore);
         long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
            certificateChains, this._keyStore, this._trustedKeyStore, System.currentTimeMillis(), this._cryptoSystemProperties
         );
         int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(certificateChainProperties);
         this._bestCertificateChain = certificateChains[bestCertificateChainIndex];
         this._bestCertificateChainProperties = certificateChainProperties[bestCertificateChainIndex];
         this._statusIcon = CertificateIconProvider.getCertificateIcon(this._bestCertificateChainProperties);
      }
   }

   public final synchronized Certificate[] getBestCertificateChain() {
      this.verifyCertificateChainsBuilt();
      return this._bestCertificateChain;
   }

   public final synchronized boolean isUntrusted() {
      this.verifyCertificateChainsBuilt();
      return (this._bestCertificateChainProperties & 8) != 0;
   }

   public final synchronized boolean isExplicitlyTrusted() {
      return this._trustedKeyStore.isMember(this.getCertificate());
   }

   public final synchronized int getStatusImageIndex() {
      return this._statusIcon;
   }

   public final synchronized KeyStoreData getKeyStoreData() {
      return this._keyStoreData;
   }

   public final synchronized boolean isPrivateKeySet() {
      return this._keyStoreData.isPrivateKeySet();
   }

   public final synchronized boolean isRoot() {
      return this._isRoot;
   }

   public final synchronized boolean isEndEntity() {
      return this._isEndEntity;
   }

   public final synchronized boolean isCA() {
      return this._isCA;
   }

   public final synchronized boolean getIsOnRevocationHold() {
      CertificateStatus status = this._certificateStatusManager.getStatus(this.getCertificate());
      return status != null ? status.getRevocationReason() == 6 : false;
   }

   public final synchronized boolean isRevoked() {
      CertificateStatus status = this._certificateStatusManager.getStatus(this.getCertificate());
      return status != null ? status.getStatus() == 1 : false;
   }

   public final synchronized boolean isDataLoaded() {
      return this._isDataLoaded;
   }

   public final String getLabel() {
      if (this._label == null) {
         this._label = this._keyStoreData.getLabel();
         Certificate cert = this.getCertificate();
         if (!cert.isRoot()) {
            String friendlyName = CertificateUtilities.getFriendlyName(cert.getIssuer());
            if (friendlyName != null) {
               this._label = ((StringBuffer)(new Object())).append(this._label).append(", ").append(friendlyName).toString();
            }
         }
      }

      return this._label;
   }

   public final void invalidateLabel() {
      this._label = null;
   }

   public final String getSerialNumber() {
      Certificate cert = this.getCertificate();
      if (cert == null) {
         return null;
      }

      byte[] serialNumber = cert.getSerialNumber();
      return serialNumber != null ? CertificateUtilities.getHexAsciiString(serialNumber) : null;
   }

   private final void loadData() {
      if (this._certificate == null) {
         this._certificate = this._keyStoreData.getCertificate();
      }

      if (this._certificate == null) {
         this._isDataLoaded = true;
      } else {
         this.verifyCertificateChainsBuilt();
         this._isDataLoaded = true;
      }
   }

   public KeyStoreBrowserData(
      KeyStoreData keyStoreData, KeyStore keyStore, KeyStore trustedKeyStore, CryptoSystemProperties cryptoSystemProperties, CertificateStatusManager manager
   ) {
      this._keyStoreData = keyStoreData;
      this._keyStore = keyStore;
      this._trustedKeyStore = trustedKeyStore;
      this._cryptoSystemProperties = cryptoSystemProperties;
      this._certificateStatusManager = manager;
      this._certificate = this._keyStoreData.getCertificate();
      this._isRoot = this._certificate.isRoot();
      this._isCA = this._certificate.isCA();
      if (this._certificate.hashCode() == 2107895108
         && CertificateUtilities.calculateThumbprint(this._certificate, (Digest)(new Object())).equals("967C 1CC9 A2A0 2974 740F 7500 1FEA C6D6 C86D 9795")) {
         this._isCA = true;
      }

      this._isEndEntity = !this._isRoot && !this._isCA;
      this._isDataLoaded = false;
   }
}
