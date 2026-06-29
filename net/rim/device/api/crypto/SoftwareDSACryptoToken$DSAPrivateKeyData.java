package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDSACryptoToken$DSAPrivateKeyData implements CryptoTokenPrivateKeyData, Persistable {
   private SoftwareDSACryptoToken$DSACryptoSystemData _cryptoSystem;
   private byte[] _privateData;
   private int _hashCode;
   private byte[] _publicData;

   public SoftwareDSACryptoToken$DSAPrivateKeyData(SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (!CryptoByteArrayArithmetic.isZero(data) && data.length <= cryptoSystem.getPrivateKeyLength()) {
            this._cryptoSystem = cryptoSystem;
            this._privateData = CryptoUtilities.copyKey(data, cryptoSystem.getPrivateKeyLength());
            MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
            PersistentContent.markAsPlaintext(this._privateData);
            PersistentContent.markAsPlaintext(data);
            this.setHashCode();
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final byte[] copyPrivateKeyData() {
      byte[] data = Arrays.copy(this._privateData);
      PersistentContent.markAsPlaintext(data);
      return data;
   }

   private final byte[] getPrivateKeyData() {
      return this._privateData;
   }

   public final byte[] copyPublicKeyData() {
      return Arrays.copy(this.getPublicKeyData());
   }

   private final byte[] getPublicKeyData() {
      if (this._publicData == null) {
         this._publicData = new byte[this._cryptoSystem.getPublicKeyLength()];
         Certicom.assertAccessAllowed();
         NativeDL.generatePublicKey(this._cryptoSystem.getP(), this._cryptoSystem.getQ(), this._cryptoSystem.getG(), this._privateData, this._publicData);
      }

      return this._publicData;
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getDigest32(this._privateData);
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

      if (!(obj instanceof SoftwareDSACryptoToken$DSAPrivateKeyData)) {
         return false;
      }

      SoftwareDSACryptoToken$DSAPrivateKeyData other = (SoftwareDSACryptoToken$DSAPrivateKeyData)obj;
      return other.hashCode() == this._hashCode && this._cryptoSystem.equals(other._cryptoSystem) && Arrays.equals(this._privateData, other._privateData);
   }
}
