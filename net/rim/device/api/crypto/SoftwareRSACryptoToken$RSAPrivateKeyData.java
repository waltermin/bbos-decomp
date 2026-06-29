package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRSACryptoToken$RSAPrivateKeyData implements CryptoTokenPrivateKeyData, Persistable {
   private RSACryptoSystem _cryptoSystem;
   private byte[] _e;
   private byte[] _d;
   private byte[] _n;
   private byte[] _p;
   private byte[] _q;
   private byte[] _dModPm1;
   private byte[] _dModQm1;
   private byte[] _qInvModP;
   private int _hashCode;

   public SoftwareRSACryptoToken$RSAPrivateKeyData(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n) {
      if (cryptoSystem != null && d != null && n != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (d.length > modulusLength || n.length != modulusLength) {
            throw new Object();
         }

         if (e.length == 1 && e[0] == 1) {
            throw new Object();
         }

         this._cryptoSystem = cryptoSystem;
         this._e = Arrays.copy(e);
         this._d = CryptoUtilities.copyKey(d, modulusLength);
         this._n = CryptoUtilities.copyKey(n, modulusLength);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._d);
         PersistentContent.markAsPlaintext(d);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public SoftwareRSACryptoToken$RSAPrivateKeyData(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] p, byte[] q) {
      if (cryptoSystem != null && d != null && p != null && q != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (d.length > modulusLength || p.length + q.length != modulusLength) {
            throw new Object();
         }

         if (e.length == 1 && e[0] == 1) {
            throw new Object();
         }

         this._cryptoSystem = cryptoSystem;
         this._e = Arrays.copy(e);
         this._d = CryptoUtilities.copyKey(d, modulusLength);
         this._p = CryptoUtilities.copyKey(p, p.length);
         this._q = CryptoUtilities.copyKey(q, q.length);
         int numberOfBitsInP = CryptoByteArrayArithmetic.getNumBits(this._p);
         int numberOfBitsInQ = CryptoByteArrayArithmetic.getNumBits(this._q);
         this._n = new byte[(numberOfBitsInP + numberOfBitsInP + 7) / 8];
         CryptoByteArrayArithmetic.multiply(this._p, this._q, numberOfBitsInP + numberOfBitsInQ, this._n);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._d);
         PersistentContent.markAsPlaintext(d);
         PersistentContent.markAsPlaintext(this._p);
         PersistentContent.markAsPlaintext(p);
         PersistentContent.markAsPlaintext(this._q);
         PersistentContent.markAsPlaintext(q);
         PersistentContent.markAsPlaintext(this._n);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public SoftwareRSACryptoToken$RSAPrivateKeyData(RSACryptoSystem cryptoSystem, byte[] e, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP) {
      if (cryptoSystem != null && p != null && q != null && dModPm1 != null && dModQm1 != null && qInvModP != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (p.length + q.length != modulusLength || dModPm1.length > p.length || dModQm1.length > q.length || qInvModP.length > p.length) {
            throw new Object();
         }

         if (e.length == 1 && e[0] == 1) {
            throw new Object();
         }

         this._cryptoSystem = cryptoSystem;
         this._e = Arrays.copy(e);
         this._p = CryptoUtilities.copyKey(p, p.length);
         this._q = CryptoUtilities.copyKey(q, q.length);
         this._dModPm1 = CryptoUtilities.copyKey(dModPm1, p.length);
         this._dModQm1 = CryptoUtilities.copyKey(dModQm1, q.length);
         this._qInvModP = CryptoUtilities.copyKey(qInvModP, p.length);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._p);
         PersistentContent.markAsPlaintext(p);
         PersistentContent.markAsPlaintext(this._q);
         PersistentContent.markAsPlaintext(q);
         PersistentContent.markAsPlaintext(this._dModPm1);
         PersistentContent.markAsPlaintext(dModPm1);
         PersistentContent.markAsPlaintext(this._dModQm1);
         PersistentContent.markAsPlaintext(dModQm1);
         PersistentContent.markAsPlaintext(this._qInvModP);
         PersistentContent.markAsPlaintext(qInvModP);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public SoftwareRSACryptoToken$RSAPrivateKeyData(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) {
      if (cryptoSystem != null && e != null && d != null && n != null && p != null && q != null && dModPm1 != null && dModQm1 != null && qInvModP != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (e.length <= 0
            || e.length > modulusLength
            || d.length > modulusLength
            || n.length != modulusLength
            || p.length + q.length != modulusLength
            || dModPm1.length > p.length
            || dModQm1.length > q.length
            || qInvModP.length > p.length) {
            throw new Object();
         }

         if (e.length == 1 && e[0] == 1) {
            throw new Object();
         }

         this._cryptoSystem = cryptoSystem;
         this._e = Arrays.copy(e);
         this._d = CryptoUtilities.copyKey(d, modulusLength);
         this._n = CryptoUtilities.copyKey(n, modulusLength);
         this._p = CryptoUtilities.copyKey(p, p.length);
         this._q = CryptoUtilities.copyKey(q, q.length);
         this._dModPm1 = CryptoUtilities.copyKey(dModPm1, p.length);
         this._dModQm1 = CryptoUtilities.copyKey(dModQm1, q.length);
         this._qInvModP = CryptoUtilities.copyKey(qInvModP, p.length);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._d);
         PersistentContent.markAsPlaintext(d);
         PersistentContent.markAsPlaintext(this._p);
         PersistentContent.markAsPlaintext(p);
         PersistentContent.markAsPlaintext(this._q);
         PersistentContent.markAsPlaintext(q);
         PersistentContent.markAsPlaintext(this._dModPm1);
         PersistentContent.markAsPlaintext(dModPm1);
         PersistentContent.markAsPlaintext(this._dModQm1);
         PersistentContent.markAsPlaintext(dModQm1);
         PersistentContent.markAsPlaintext(this._qInvModP);
         PersistentContent.markAsPlaintext(qInvModP);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public final byte[] copyE() {
      return this._e == null ? null : Arrays.copy(this._e);
   }

   public final byte[] copyD() {
      if (this._d != null) {
         byte[] data = Arrays.copy(this._d);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] copyN() {
      if (this._n != null) {
         return Arrays.copy(this._n);
      } else if (this._p != null && this._q != null) {
         byte[] modulus = new byte[this._p.length + this._q.length];
         CryptoByteArrayArithmetic.multiply(this._p, this._q, modulus.length * 8, modulus);
         return modulus;
      } else {
         return null;
      }
   }

   public final byte[] copyP() {
      if (this._p != null) {
         byte[] data = Arrays.copy(this._p);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] copyQ() {
      if (this._q != null) {
         byte[] data = Arrays.copy(this._q);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] copyDModPm1() {
      if (this._dModPm1 != null) {
         byte[] data = Arrays.copy(this._dModPm1);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] copyDModQm1() {
      if (this._dModQm1 != null) {
         byte[] data = Arrays.copy(this._dModQm1);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] copyQInvModP() {
      if (this._qInvModP != null) {
         byte[] data = Arrays.copy(this._qInvModP);
         PersistentContent.markAsPlaintext(data);
         return data;
      } else {
         return null;
      }
   }

   public final byte[] getE() {
      return this._e;
   }

   public final byte[] getD() {
      return this._d;
   }

   public final byte[] getN() {
      return this._n;
   }

   public final byte[] getP() {
      return this._p;
   }

   public final byte[] getQ() {
      return this._q;
   }

   public final byte[] getDModPm1() {
      return this._dModPm1;
   }

   public final byte[] getDModQm1() {
      return this._dModQm1;
   }

   public final byte[] getQInvModP() {
      return this._qInvModP;
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = this._cryptoSystem.hashCode();
         if (this._e != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getCRC32(this._e);
         }

         if (this._d != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._d);
         }

         if (this._n != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getCRC32(this._n);
         }

         if (this._p != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._p);
         }

         if (this._q != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._q);
         }

         if (this._dModPm1 != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._dModPm1);
         }

         if (this._dModQm1 != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._dModQm1);
         }

         if (this._qInvModP != null) {
            this._hashCode = this._hashCode ^ HashCodeCalculator.getDigest32(this._qInvModP);
         }

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

      if (!(obj instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         return false;
      }

      SoftwareRSACryptoToken$RSAPrivateKeyData other = (SoftwareRSACryptoToken$RSAPrivateKeyData)obj;
      return other.hashCode() == this._hashCode
         && this._cryptoSystem.equals(other._cryptoSystem)
         && (this._e == null && other._e == null || this._e != null && other._e != null && CryptoByteArrayArithmetic.compare(this._e, other._e) == 0)
         && (this._d == null && other._d == null || this._d != null && other._d != null && CryptoByteArrayArithmetic.compare(this._d, other._d) == 0)
         && (this._n == null && other._n == null || this._n != null && other._n != null && CryptoByteArrayArithmetic.compare(this._n, other._n) == 0)
         && (this._p == null && other._p == null || this._p != null && other._p != null && CryptoByteArrayArithmetic.compare(this._p, other._p) == 0)
         && (this._q == null && other._q == null || this._q != null && other._q != null && CryptoByteArrayArithmetic.compare(this._q, other._q) == 0)
         && (
            this._dModPm1 == null && other._dModPm1 == null
               || this._dModPm1 != null && other._dModPm1 != null && CryptoByteArrayArithmetic.compare(this._dModPm1, other._dModPm1) == 0
         )
         && (
            this._dModQm1 == null && other._dModQm1 == null
               || this._dModQm1 != null && other._dModQm1 != null && CryptoByteArrayArithmetic.compare(this._dModQm1, other._dModQm1) == 0
         )
         && (
            this._qInvModP == null && other._qInvModP == null
               || this._qInvModP != null && other._qInvModP != null && CryptoByteArrayArithmetic.compare(this._qInvModP, other._qInvModP) == 0
         );
   }
}
