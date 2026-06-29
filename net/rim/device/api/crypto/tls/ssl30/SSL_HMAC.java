package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.AbstractMAC;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.util.Arrays;

final class SSL_HMAC extends AbstractMAC implements MAC {
   private Digest _innerDigest;
   private Digest _outerDigest;
   private HMACKey _key;

   public SSL_HMAC(HMACKey key, String digestType) {
      try {
         if (key != null && digestType != null) {
            this._key = key;
            if (digestType.equals("SHA1") || digestType.equals("MD5")) {
               this._innerDigest = DigestFactory.getInstance(digestType);
               this._outerDigest = DigestFactory.getInstance(digestType);
               this.reset();
            }
         } else {
            throw new IllegalArgumentException();
         }
      } finally {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "SSL_HMAC";
   }

   @Override
   public final int getLength() {
      return this._outerDigest.getDigestLength();
   }

   @Override
   public final int getMAC(byte[] buffer, int offset, boolean reset) {
      if (buffer != null && buffer.length - this._outerDigest.getDigestLength() >= offset) {
         byte[] innerDigest = this._innerDigest.getDigest(reset);
         this._outerDigest.update(innerDigest);
         this._outerDigest.getDigest(buffer, offset);
         if (reset) {
            this.reset();
         }

         return this._outerDigest.getDigestLength();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void reset() throws CryptoTokenException {
      try {
         this._innerDigest.reset();
         this._outerDigest.reset();
         this._outerDigest.update(this._key.getData());
         byte[] pad2;
         if (this._outerDigest instanceof SHA1Digest) {
            pad2 = new byte[40];
         } else {
            if (!(this._outerDigest instanceof MD5Digest)) {
               throw new IllegalArgumentException();
            }

            pad2 = new byte[48];
         }

         Arrays.fill(pad2, (byte)92);
         this._outerDigest.update(pad2);
         this._innerDigest.update(this._key.getData());
         byte[] pad1;
         if (this._innerDigest instanceof SHA1Digest) {
            pad1 = new byte[40];
         } else {
            if (!(this._innerDigest instanceof MD5Digest)) {
               throw new IllegalArgumentException();
            }

            pad1 = new byte[48];
         }

         Arrays.fill(pad1, (byte)54);
         this._innerDigest.update(pad1);
      } finally {
         throw new CryptoTokenException();
      }
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._innerDigest.update(data, offset, length);
   }
}
