package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataTicket;

public class CryptoSmartCardKeyStoreData implements KeyStoreData {
   private AssociatedData[] _associatedData;
   private String _label;
   private PrivateKey _privateKey;
   private SymmetricKey _symmetricKey;
   private int _securityLevel;
   private Certificate _certificate;
   private CertificateStatus _certStatus;
   private PublicKey _publicKey;
   private long _keyUsage;

   public PrivateKey getPrivateKey() {
      return this._privateKey;
   }

   public SymmetricKey getSymmetricKey() {
      return this._symmetricKey;
   }

   @Override
   public PrivateKey getPrivateKey(KeyStoreDataTicket ticket) {
      return this._privateKey;
   }

   @Override
   public SymmetricKey getSymmetricKey(KeyStoreDataTicket ticket) {
      return this._symmetricKey;
   }

   @Override
   public boolean isPrivateKeySet() {
      return this._privateKey != null;
   }

   @Override
   public boolean isSymmetricKeySet() {
      return this._symmetricKey != null;
   }

   @Override
   public PublicKey getPublicKey() {
      if (this._publicKey != null) {
         return this._publicKey;
      }

      if (this._certificate != null) {
         try {
            return this._certificate.getPublicKey();
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Certificate getCertificate() {
      return this._certificate;
   }

   @Override
   public void setLabel(String newLabel) {
      this._label = newLabel;
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   @Override
   public void changePassword() {
   }

   @Override
   public int queryKeyUsage(long purpose) {
      if (this._certificate != null) {
         return this._certificate.queryKeyUsage(purpose);
      } else {
         return (this._keyUsage & purpose) != 0 ? 1 : 0;
      }
   }

   @Override
   public byte[][][] getAssociatedData(long association) {
      if (this._associatedData != null) {
         for (int i = this._associatedData.length - 1; i >= 0; i--) {
            AssociatedData associatedData = this._associatedData[i];
            if (associatedData.getAssociation() == association) {
               return (byte[][][])associatedData.getData();
            }
         }
      }

      return (byte[][][])((byte[][])null);
   }

   @Override
   public AssociatedData[] getAssociatedData() {
      return this._associatedData;
   }

   @Override
   public KeyStoreDataTicket getTicket() {
      return new CryptoSmartCardKeyStoreData$SmartCardKeyStoreDataTicket();
   }

   @Override
   public KeyStoreDataTicket getTicket(String prompt) {
      return this.getTicket();
   }

   @Override
   public boolean checkTicket(KeyStoreDataTicket ticket) {
      return true;
   }

   @Override
   public int getSecurityLevel() {
      return this._securityLevel;
   }

   @Override
   public int getPasswordVersion() {
      return 0;
   }

   public CryptoSmartCardKeyStoreData(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      SymmetricKey symmetricKey,
      int securityLevel,
      Certificate certificate,
      CertificateStatus certStatus,
      PublicKey publicKey,
      long keyUsage
   ) {
      this._label = label;
      this._privateKey = privateKey;
      this._symmetricKey = symmetricKey;
      this._securityLevel = securityLevel;
      this._certificate = certificate;
      this._certStatus = certStatus;
      this._publicKey = publicKey;
      this._keyUsage = keyUsage;
      this._associatedData = associatedData;
   }
}
