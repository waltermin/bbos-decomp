package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;

final class KeyStoreDataHelper implements SyncObject {
   private AssociatedData[] _associatedData;
   private CertificateStatus _certStatus;
   private String _label;
   private byte[] _privateKey;
   private String _privateKeyEncodingAlgorithm;
   private byte[] _symmetricKey;
   private String _symmetricKeyEncodingAlgorithm;
   private PublicKey _publicKey;
   private long _keyUsage;
   private byte[] _certificate;
   private String _certificateType;
   private int _uid;
   private int[] _hashes;
   private long[] _indices;
   private long[] _notUsed;

   public final String getLabel() {
      return this._label;
   }

   public final byte[] getPrivateKey() {
      return this._privateKey;
   }

   public final String getPrivateKeyEncodingAlgorithm() {
      return this._privateKeyEncodingAlgorithm;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final String getSymmetricKeyEncodingAlgorithm() {
      return this._symmetricKeyEncodingAlgorithm;
   }

   public final PublicKey getPublicKey() {
      return this._publicKey;
   }

   public final long getKeyUsage() {
      return this._keyUsage;
   }

   public final byte[] getCertificate() {
      return this._certificate;
   }

   public final String getCertificateType() {
      return this._certificateType;
   }

   public final AssociatedData[] getAssociatedData() {
      return this._associatedData;
   }

   public final byte[] getSymmetricKey() {
      return this._symmetricKey;
   }

   public final CertificateStatus getCertificateStatus() {
      return this._certStatus;
   }

   public final int[] getHashes() {
      return this._hashes;
   }

   public final long[] getIndices() {
      return this._indices;
   }

   public final long[] getNotUsed() {
      return this._notUsed;
   }

   public KeyStoreDataHelper(
      AssociatedData[] associatedData,
      String label,
      byte[] privateKey,
      String privateKeyEncodingAlgorithm,
      byte[] symmetricKey,
      String symmetricKeyEncodingAlgorithm,
      PublicKey publicKey,
      long keyUsage,
      byte[] certificate,
      String certificateType,
      CertificateStatus certStatus,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed
   ) {
      this._associatedData = associatedData;
      this._certStatus = certStatus;
      this._label = label;
      this._privateKey = privateKey;
      this._privateKeyEncodingAlgorithm = privateKeyEncodingAlgorithm;
      this._symmetricKey = symmetricKey;
      this._symmetricKeyEncodingAlgorithm = symmetricKeyEncodingAlgorithm;
      this._publicKey = publicKey;
      this._keyUsage = keyUsage;
      this._certStatus = certStatus;
      this._certificate = certificate;
      this._certificateType = certificateType;
      this._uid = uid;
      this._hashes = hashes;
      this._indices = indices;
      this._notUsed = notUsed;
      if (this._hashes != null && this._indices != null) {
         Arrays.sort(this._indices, 0, this._indices.length, this._hashes);
      }

      if (this._notUsed != null) {
         Arrays.sort(this._notUsed, 0, this._notUsed.length);
      }
   }
}
