package net.rim.device.apps.internal.secureemail.cache;

public final class CachedMessageStatusData {
   private int _signatureStatus;
   private String _signerName;
   private String _signatureStatusDetails;
   private int _trustStatus;
   private String _trustStatusDetails;
   private String _encryptionAlgorithm;
   private int _encryptionBitLength;

   public final void setSignatureStatus(int signatureStatus, String signerName, String details) {
      this._signatureStatus = signatureStatus;
      this._signerName = signerName;
      this._signatureStatusDetails = details;
   }

   public final void setTrustStatus(int trustStatus, String details) {
      this._trustStatus = trustStatus;
      this._trustStatusDetails = details;
   }

   public final void setEncryptionStatus(String encryptionAlgorithm, int encryptionBitLength) {
      this._encryptionAlgorithm = encryptionAlgorithm;
      this._encryptionBitLength = encryptionBitLength;
   }

   public final int getSignatureStatus() {
      return this._signatureStatus;
   }

   public final String getSignerName() {
      return this._signerName;
   }

   public final String getSignatureStatusDetails() {
      return this._signatureStatusDetails;
   }

   public final int getTrustStatus() {
      return this._trustStatus;
   }

   public final String getTrustStatusDetails() {
      return this._trustStatusDetails;
   }

   public final String getPublicKeyEncryptionAlgorithm() {
      return this._encryptionAlgorithm;
   }

   public final int getPublicKeyEncryptionBitLength() {
      return this._encryptionBitLength;
   }
}
