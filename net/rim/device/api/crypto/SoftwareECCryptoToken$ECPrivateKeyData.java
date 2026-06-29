package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareECCryptoToken$ECPrivateKeyData implements CryptoTokenPrivateKeyData, Persistable {
   private SoftwareECCryptoToken$ECCryptoSystemData _cryptoSystem;
   private byte[] _privateData;
   private int _hashCode;
   private byte[] _publicData;

   public SoftwareECCryptoToken$ECPrivateKeyData(SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem, byte[] data) throws InvalidKeyException {
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
         this._publicData = new byte[this._cryptoSystem.getPublicKeyLength(true)];
         Certicom.assertAccessAllowed();
         NativeEC.generatePublicKey(this._cryptoSystem.getName(), this._privateData, this._publicData);
      }

      return this._publicData;
   }

   public final byte[] copyPublicKeyData(boolean compress) {
      this.getPublicKeyData();
      if (compress) {
         byte[] compressedData = new byte[this._cryptoSystem.getPublicKeyLength(true)];
         Certicom.assertAccessAllowed();
         NativeEC.compressPublicKey(this._cryptoSystem.getName(), this._publicData, compressedData);
         return compressedData;
      } else {
         byte[] uncompressedData = new byte[this._cryptoSystem.getPublicKeyLength(false)];
         Certicom.assertAccessAllowed();
         NativeEC.uncompressPublicKey(this._cryptoSystem.getName(), this._publicData, uncompressedData);
         return uncompressedData;
      }
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getDigest32(this._privateData);
      if (this._hashCode == 0) {
         this._hashCode = 1;
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

      if (obj instanceof SoftwareECCryptoToken$ECPrivateKeyData) {
         SoftwareECCryptoToken$ECPrivateKeyData other = (SoftwareECCryptoToken$ECPrivateKeyData)obj;
         if (this._hashCode == other._hashCode) {
            if (this._cryptoSystem.equals(other._cryptoSystem) && Arrays.equals(this._privateData, other._privateData)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
