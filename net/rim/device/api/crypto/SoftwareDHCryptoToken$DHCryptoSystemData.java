package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDHCryptoToken$DHCryptoSystemData implements CryptoTokenCryptoSystemData, Persistable {
   private byte[] _p;
   private byte[] _q;
   private byte[] _g;
   private int _privateKeyMinRandomBits;
   private int _hashCode;
   private String _name;

   SoftwareDHCryptoToken$DHCryptoSystemData(byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) throws InvalidCryptoSystemException {
      if (p != null && g != null && privateKeyMinRandomBits > 0) {
         if (privateKeyMinRandomBits > p.length << 3) {
            privateKeyMinRandomBits = p.length << 3;
         }

         if (p.length >= 64 && g.length >= 1 && g.length <= p.length) {
            this._p = Arrays.copy(p);
            if (q != null) {
               if (privateKeyMinRandomBits > q.length << 3) {
                  privateKeyMinRandomBits = q.length << 3;
               }

               if (q.length < 20 || q.length > p.length) {
                  throw new InvalidCryptoSystemException();
               }

               this._q = Arrays.copy(q);
            }

            this._g = new byte[p.length];
            System.arraycopy(g, 0, this._g, this._g.length - g.length, g.length);
            this._privateKeyMinRandomBits = privateKeyMinRandomBits;
            this._name = name;
            this.setHashCode();
         } else {
            throw new InvalidCryptoSystemException();
         }
      } else {
         throw new IllegalArgumentException();
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

   private final byte[] getP() {
      return this._p;
   }

   final byte[] copyQ() {
      return this._q == null ? null : Arrays.copy(this._q);
   }

   private final byte[] getQ() {
      return this._q;
   }

   final byte[] copyG() {
      return Arrays.copy(this._g);
   }

   private final byte[] getG() {
      return this._g;
   }

   final int getPublicKeyLength() {
      return this._p.length;
   }

   final int getPrivateKeyLength() {
      return this._q != null ? this._q.length : this._p.length;
   }

   final int getPrivateKeyMinRandomBits() {
      return Math.min(this._privateKeyMinRandomBits, this.getPrivateKeyLength() << 3);
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

      if (obj instanceof SoftwareDHCryptoToken$DHCryptoSystemData) {
         SoftwareDHCryptoToken$DHCryptoSystemData other = (SoftwareDHCryptoToken$DHCryptoSystemData)obj;
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
