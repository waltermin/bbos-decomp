package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareKEACryptoToken$KEAPrivateKeyData implements CryptoTokenPrivateKeyData, Persistable {
   private SoftwareKEACryptoToken$KEACryptoSystemData _cryptoSystem;
   private byte[] _privateData;
   private int _hashCode;
   private byte[] _publicData;

   SoftwareKEACryptoToken$KEAPrivateKeyData(SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem, byte[] data) throws InvalidKeyException {
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

   final byte[] getPrivateKeyData() {
      byte[] data = Arrays.copy(this._privateData);
      PersistentContent.markAsPlaintext(data);
      return data;
   }

   final byte[] getPublicKeyData() {
      if (this._publicData == null) {
         this._publicData = new byte[this._cryptoSystem.getPublicKeyLength()];
         Certicom.assertAccessAllowed();
         NativeDL.generatePublicKey(this._cryptoSystem.getP(), this._cryptoSystem.getQ(), this._cryptoSystem.getG(), this._privateData, this._publicData);
      }

      return Arrays.copy(this._publicData);
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getDigest32(this._privateData);
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SoftwareKEACryptoToken$KEAPrivateKeyData) {
         SoftwareKEACryptoToken$KEAPrivateKeyData other = (SoftwareKEACryptoToken$KEAPrivateKeyData)obj;
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
