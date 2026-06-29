package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDSACryptoToken$DSACryptoSystemData implements CryptoTokenCryptoSystemData, Persistable {
   private byte[] _p;
   private byte[] _q;
   private byte[] _g;
   private int _hashCode;
   private String _name;

   public SoftwareDSACryptoToken$DSACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) throws InvalidCryptoSystemException {
      if (p != null && q != null && g != null) {
         if (p.length >= 64 && p.length <= 128 && (p.length & 7) == 0 && q.length == 20 && g.length >= 1 && g.length <= p.length) {
            this._p = Arrays.copy(p);
            this._q = Arrays.copy(q);
            this._g = new byte[p.length];
            System.arraycopy(g, 0, this._g, this._g.length - g.length, g.length);
            this._name = name;
            this.setHashCode();
         } else {
            throw new InvalidCryptoSystemException();
         }
      } else {
         throw new Object();
      }
   }

   public final int getBitLength() {
      return this._p.length << 3;
   }

   public final String getName() {
      return this._name;
   }

   public final byte[] getP() {
      return Arrays.copy(this._p);
   }

   public final byte[] getQ() {
      return Arrays.copy(this._q);
   }

   public final byte[] getG() {
      return Arrays.copy(this._g);
   }

   public final int getPublicKeyLength() {
      return this._p.length;
   }

   public final int getPrivateKeyLength() {
      return this._q.length;
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = HashCodeCalculator.getCRC32(this._p) ^ HashCodeCalculator.getCRC32(this._q) ^ HashCodeCalculator.getCRC32(this._g);
         if (this._hashCode == 0) {
            this._hashCode = 1;
         }
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         return false;
      }

      SoftwareDSACryptoToken$DSACryptoSystemData other = (SoftwareDSACryptoToken$DSACryptoSystemData)obj;
      return other.hashCode() == this._hashCode && Arrays.equals(this._p, other._p) && Arrays.equals(this._q, other._q) && Arrays.equals(this._g, other._g);
   }
}
