package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareKEACryptoToken$KEACryptoSystemData implements CryptoTokenCryptoSystemData, Persistable {
   private byte[] _p;
   private byte[] _q;
   private byte[] _g;
   private int _hashCode;
   private String _name;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   SoftwareKEACryptoToken$KEACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) {
      if (p != null && g != null && q != null) {
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            g = CryptoByteArrayArithmetic.ensureLength(g, p.length);
            var7 = false;
         } finally {
            if (var7) {
               throw new Object();
            }
         }

         if (p.length == 128 && g.length >= 1 && g.length <= p.length && q.length == 20) {
            this._p = Arrays.copy(p);
            this._q = Arrays.copy(q);
            this._g = new byte[p.length];
            System.arraycopy(g, 0, this._g, this._g.length - g.length, g.length);
            this._name = name;
            this.setHashCode();
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   final int getBitLength() {
      return this._p.length << 3;
   }

   final String getName() {
      return this._name;
   }

   final byte[] copyP() {
      return Arrays.copy(this._p);
   }

   final byte[] getP() {
      return this._p;
   }

   final byte[] copyQ() {
      return Arrays.copy(this._q);
   }

   final byte[] getQ() {
      return this._q;
   }

   final byte[] copyG() {
      return Arrays.copy(this._g);
   }

   final byte[] getG() {
      return this._g;
   }

   final int getPublicKeyLength() {
      return this._p.length;
   }

   final int getPrivateKeyLength() {
      return this._q.length;
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getCRC32(this._p) ^ HashCodeCalculator.getCRC32(this._q) ^ HashCodeCalculator.getCRC32(this._g);
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SoftwareKEACryptoToken$KEACryptoSystemData) {
         SoftwareKEACryptoToken$KEACryptoSystemData other = (SoftwareKEACryptoToken$KEACryptoSystemData)obj;
         if (this._hashCode == other._hashCode) {
            if (Arrays.equals(this._p, other._p) && Arrays.equals(this._q, other._q) && Arrays.equals(this._g, other._g)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
