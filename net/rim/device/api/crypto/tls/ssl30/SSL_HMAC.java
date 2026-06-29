package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.AbstractMAC;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.MAC;
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
            throw new Object();
         }
      } finally {
         throw new Object();
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
         throw new Object();
      }
   }

   @Override
   public final void reset() {
      try {
         this._innerDigest.reset();
         this._outerDigest.reset();
         this._outerDigest.update(this._key.getData());
         byte[] pad2;
         if (this._outerDigest instanceof Object) {
            pad2 = new byte[40];
         } else {
            if (!(this._outerDigest instanceof Object)) {
               throw new Object();
            }

            pad2 = new byte[48];
         }

         Arrays.fill(pad2, (byte)92);
         this._outerDigest.update(pad2);
         this._innerDigest.update(this._key.getData());
         byte[] pad1;
         if (this._innerDigest instanceof Object) {
            pad1 = new byte[40];
         } else {
            if (!(this._innerDigest instanceof Object)) {
               throw new Object();
            }

            pad1 = new byte[48];
         }

         Arrays.fill(pad1, (byte)54);
         this._innerDigest.update(pad1);
      } finally {
         throw new Object();
      }
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._innerDigest.update(data, offset, length);
   }
}
