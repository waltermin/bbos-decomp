package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.crypto.tls.tls10.TLSP_hash;

final class WTLSPRF extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private TLSP_hash _hash;

   public WTLSPRF(int digest, byte[] secret, byte[] label, byte[] seed) {
      if (label != null && seed != null) {
         byte[] labelSeed = new byte[label.length + seed.length];
         System.arraycopy(label, 0, labelSeed, 0, label.length);
         System.arraycopy(seed, 0, labelSeed, label.length, seed.length);
         Digest hashDigest;
         switch (digest) {
            case 0:
            case 4:
               throw new Object();
            case 1:
            case 2:
            case 3:
            default:
               hashDigest = (Digest)(new Object());
               break;
            case 5:
            case 6:
            case 7:
               hashDigest = (Digest)(new Object());
         }

         if (secret == null) {
            secret = new byte[hashDigest.getDigestLength()];
         }

         this._hash = new TLSP_hash(hashDigest, secret, 0, secret.length, labelSeed);
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "WTLS_PRF";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      this._hash.xorBytes(buffer, offset, length);
   }

   @Override
   public final int getAvailable() {
      return this._hash.getAvailable();
   }

   @Override
   public final int getMaxAvailable() {
      return this._hash.getMaxAvailable();
   }
}
