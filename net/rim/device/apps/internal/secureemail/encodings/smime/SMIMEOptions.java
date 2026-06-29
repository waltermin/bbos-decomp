package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.SerialNumberIssuerKeyStoreIndex;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;

public final class SMIMEOptions extends SecureEmailOptions implements Persistable {
   private boolean _includeCertificatesFlag;
   private boolean _requestSignedReceipts;
   private byte[] _signingSerialNumber;
   private byte[] _signingIssuer;
   private byte[] _encryptionSerialNumber;
   private byte[] _encryptionIssuer;
   private static final int CONTENT_CIPHERS_BITFIELD_DEFAULT = 39;
   private static final boolean INCLUDE_CERTIFICATES_DEFAULT = true;
   private static final boolean REQUEST_SIGNED_RECEIPTS_DEFAULT = false;

   public SMIMEOptions() {
      this.reset();
      this.checkITPolicyConformance();
   }

   public SMIMEOptions(SecureEmailOptions smimeOptions) {
      this.copy(smimeOptions);
      this.checkITPolicyConformance();
   }

   @Override
   protected final void reset() {
      super.reset();
      super._contentCipherBitfield = 39;
      this._includeCertificatesFlag = true;
      this._requestSignedReceipts = false;
      this._signingSerialNumber = null;
      this._signingIssuer = null;
      this._encryptionSerialNumber = null;
      this._encryptionIssuer = null;
   }

   @Override
   public final void copy(SecureEmailOptions s) {
      super.copy(s);
      SMIMEOptions other = (SMIMEOptions)s;
      this._includeCertificatesFlag = other._includeCertificatesFlag;
      this._requestSignedReceipts = other._requestSignedReceipts;
      this._signingSerialNumber = other._signingSerialNumber;
      this._signingIssuer = other._signingIssuer;
      this._encryptionSerialNumber = other._encryptionSerialNumber;
      this._encryptionIssuer = other._encryptionIssuer;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof SMIMEOptions)) {
         return false;
      }

      SMIMEOptions s = (SMIMEOptions)other;
      return super.equals(s)
         && this._includeCertificatesFlag == s._includeCertificatesFlag
         && this._requestSignedReceipts == s._requestSignedReceipts
         && Arrays.equals(this._signingSerialNumber, s._signingSerialNumber)
         && Arrays.equals(this._signingIssuer, s._signingIssuer)
         && Arrays.equals(this._encryptionSerialNumber, s._encryptionSerialNumber)
         && Arrays.equals(this._encryptionIssuer, s._encryptionIssuer);
   }

   @Override
   public final synchronized boolean isConfigured(int encodingAction, ServiceRecord serviceRecord) {
      if (serviceRecord != null && (encodingAction & 2) != 0) {
         String emsEmailAddress = new SMIMEUtilities().getEMSEmailAddress(serviceRecord.getUid());
         if (emsEmailAddress != null && emsEmailAddress.length() > 0 && super._signingKeyStoreData == null) {
            return false;
         }
      }

      return super.isConfigured(encodingAction, serviceRecord);
   }

   private final void checkITPolicyConformance() {
      SecureEmailUtilities utilities = SMIMEFactory.getInstance().getUtilities();
      if (!utilities.isCertificateAllowed(super._signingKeyStoreData, 4)) {
         super._signingKeyStoreData = null;
      }

      if (!utilities.isCertificateAllowed(super._encryptionKeyStoreData, 4)) {
         super._encryptionKeyStoreData = null;
      }

      super._contentCipherBitfield = super._contentCipherBitfield & utilities.getITPolicyContentCiphers();
   }

   public final boolean getIncludeCertificatesFlag() {
      return this._includeCertificatesFlag;
   }

   public final void setIncludeCertificatesFlag(boolean includeCertificates) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean getRequestSignedReceipts() {
      return this._requestSignedReceipts;
   }

   public final void setRequestSignedReceipts(boolean requestSignedReceipts) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      KeyStoreData keyStoreData = (KeyStoreData)element;
      boolean commitData = false;
      if (this.isMatchingKeyStoreData(keyStoreData, this._signingSerialNumber, this._signingIssuer)) {
         super._signingKeyStoreData = keyStoreData;
         this._signingSerialNumber = null;
         this._signingIssuer = null;
         commitData = true;
      }

      if (this.isMatchingKeyStoreData(keyStoreData, this._encryptionSerialNumber, this._encryptionIssuer)) {
         super._encryptionKeyStoreData = keyStoreData;
         this._encryptionSerialNumber = null;
         this._encryptionIssuer = null;
         commitData = true;
      }

      if (commitData) {
         SMIMEFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      boolean commitData = false;
      if (element.equals(super._signingKeyStoreData)) {
         super._signingKeyStoreData = null;
         commitData = true;
      }

      if (element.equals(super._encryptionKeyStoreData)) {
         super._encryptionKeyStoreData = null;
         commitData = true;
      }

      if (commitData) {
         SMIMEFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.checkITPolicyConformance();
         SMIMEFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final OTASyncCapableSyncItem getSyncItem() {
      return new SMIMEOptions$SMIMEOptionsSyncItem(this);
   }

   private final KeyStoreData findMatchingKeyStoreData(KeyStore keyStore, byte[] serialNumber, byte[] issuer) {
      if (keyStore != null && serialNumber != null && issuer != null) {
         if (!keyStore.existsIndex(-6470299966859493514L)) {
            keyStore.addIndex(new SerialNumberIssuerKeyStoreIndex());
         }

         try {
            Object alias = SerialNumberIssuerKeyStoreIndex.getAlias(serialNumber, new X509DistinguishedName(issuer));
            Enumeration enumeration = keyStore.elements(-6470299966859493514L, alias);

            while (enumeration.hasMoreElements()) {
               KeyStoreData currentKeyStoreData = (KeyStoreData)enumeration.nextElement();
               if (this.checkPrivateKeyAndITPolicy(currentKeyStoreData)) {
                  return currentKeyStoreData;
               }
            }
         } finally {
            return null;
         }

         return null;
      } else {
         return null;
      }
   }

   private final boolean isMatchingKeyStoreData(KeyStoreData keyStoreData, byte[] serialNumber, byte[] issuer) {
      if (serialNumber != null && issuer != null) {
         Certificate certificate = keyStoreData.getCertificate();
         return certificate != null
            && Arrays.equals(serialNumber, certificate.getSerialNumber())
            && Arrays.equals(issuer, certificate.getIssuer().getEncoding())
            && this.checkPrivateKeyAndITPolicy(keyStoreData);
      } else {
         return false;
      }
   }

   private final boolean checkPrivateKeyAndITPolicy(KeyStoreData keyStoreData) {
      return keyStoreData.isPrivateKeySet() && SMIMEFactory.getInstance().getUtilities().isCertificateAllowed(keyStoreData, 4);
   }

   static final boolean access$200(SMIMEOptions x0) {
      return x0._promptProblemPersonalCerts;
   }

   static final KeyStoreData access$300(SMIMEOptions x0) {
      return x0._signingKeyStoreData;
   }

   static final KeyStoreData access$400(SMIMEOptions x0) {
      return x0._signingKeyStoreData;
   }

   static final KeyStoreData access$500(SMIMEOptions x0) {
      return x0._encryptionKeyStoreData;
   }

   static final KeyStoreData access$600(SMIMEOptions x0) {
      return x0._encryptionKeyStoreData;
   }

   static final int access$700(SMIMEOptions x0) {
      return x0._contentCipherBitfield;
   }

   static final boolean access$800(SMIMEOptions x0) {
      return x0._showMessageDetails;
   }

   static final boolean access$902(SMIMEOptions x0, boolean x1) {
      return x0._promptProblemPersonalCerts = x1;
   }

   static final int access$1402(SMIMEOptions x0, int x1) {
      return x0._contentCipherBitfield = x1;
   }

   static final boolean access$1502(SMIMEOptions x0, boolean x1) {
      return x0._showMessageDetails = x1;
   }

   static final KeyStoreData access$1602(SMIMEOptions x0, KeyStoreData x1) {
      return x0._signingKeyStoreData = x1;
   }

   static final KeyStoreData access$1802(SMIMEOptions x0, KeyStoreData x1) {
      return x0._encryptionKeyStoreData = x1;
   }

   static final KeyStoreData access$1900(SMIMEOptions x0) {
      return x0._signingKeyStoreData;
   }

   static final KeyStoreData access$2000(SMIMEOptions x0) {
      return x0._encryptionKeyStoreData;
   }
}
