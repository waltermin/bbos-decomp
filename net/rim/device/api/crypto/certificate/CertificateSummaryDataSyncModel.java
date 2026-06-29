package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public class CertificateSummaryDataSyncModel implements PersistableRIMModel, ConversionProvider, SyncObject {
   private int _hashCode;
   private byte[] _certificateHash;
   private byte[] _certificateEncoding;
   protected static final int TAG_CERT_HASH = 1;
   protected static final int TAG_CERT_ENCODING = 2;
   protected static final int TAG_ISSUER = 3;
   protected static final int TAG_SERIAL_NUMBER = 4;
   protected static final int TAG_SUBJECT_KEY_ID = 5;
   protected static final int TAG_FLAGS = 6;
   protected static final int TAG_PGP_KEY_IDS = 7;
   protected static final int TAG_RECORD_TYPE = 127;
   protected static final byte[] RECORD_TYPE = new byte[]{88};

   public CertificateSummaryDataSyncModel(Certificate certificate, boolean isPrivateKeySet) {
      byte[] certificateEncoding = certificate.getEncoding();
      SHA1Digest sha1Digest = new SHA1Digest();
      sha1Digest.update(certificateEncoding);
      this._certificateHash = sha1Digest.getDigest();
      this._hashCode = 0;
      int certificateHashLength = this._certificateHash.length;
      int numHashCodeBytes = Math.min(4, certificateHashLength);

      for (int i = 0; i < numHashCodeBytes; i++) {
         this._hashCode <<= 8;
         this._hashCode = this._hashCode | this._certificateHash[i] & 255;
      }

      if (isPrivateKeySet) {
         this._certificateEncoding = certificateEncoding;
      }
   }

   public boolean correspondsToPrivateKey() {
      return this._certificateEncoding != null;
   }

   @Override
   public int getUID() {
      return this._hashCode;
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return other instanceof CertificateSummaryDataSyncModel
            ? Arrays.equals(this._certificateHash, ((CertificateSummaryDataSyncModel)other)._certificateHash)
            : false;
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(127, RECORD_TYPE);
         syncBuffer.addBytes(1, this._certificateHash);
         if (this._certificateEncoding != null) {
            syncBuffer.addBytes(2, this._certificateEncoding);
         }

         return true;
      } else {
         return false;
      }
   }
}
