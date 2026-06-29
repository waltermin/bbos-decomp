package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.AbstractDigest;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.SHA1Digest;

public class SSLDigest extends AbstractDigest implements Digest {
   private MD5Digest _md5;
   private SHA1Digest _sha;
   public static final int DIGEST_LENGTH = 36;

   public SSLDigest() {
      this._md5 = (MD5Digest)(new Object());
      this._sha = (SHA1Digest)(new Object());
   }

   public SSLDigest(MD5Digest md5Digest, SHA1Digest shaDigest) {
      this._md5 = md5Digest;
      this._sha = shaDigest;
   }

   @Override
   public String getAlgorithm() {
      return "SSLDigest";
   }

   @Override
   public int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      if (buffer != null && buffer.length - 36 >= offset) {
         this._md5.getDigest(buffer, offset, resetDigest);
         this._sha.getDigest(buffer, offset + 16, resetDigest);
         return 36;
      } else {
         throw new Object();
      }
   }

   @Override
   public int getDigestLength() {
      return 36;
   }

   @Override
   public void reset() {
      this._md5.reset();
      this._sha.reset();
   }

   @Override
   public void update(int data) {
      this._md5.update(data);
      this._sha.update(data);
   }

   @Override
   public void update(byte[] data, int offset, int length) {
      this._md5.update(data, offset, length);
      this._sha.update(data, offset, length);
   }
}
