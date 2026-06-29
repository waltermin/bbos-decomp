package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDHCryptoToken$DHPrivateKeyData implements CryptoTokenPrivateKeyData, Persistable {
   private SoftwareDHCryptoToken$DHCryptoSystemData _cryptoSystem;
   private byte[] _privateData;
   private int _hashCode;
   private byte[] _publicData;

   SoftwareDHCryptoToken$DHPrivateKeyData(SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (!CryptoByteArrayArithmetic.isZero(data) && data.length <= cryptoSystem.getPrivateKeyLength()) {
            this._cryptoSystem = cryptoSystem;
            byte[] q = cryptoSystem.getQ();
            this._privateData = CryptoUtilities.copyKey(data, q != null ? q.length : cryptoSystem.getPublicKeyLength());
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

   final byte[] copyPrivateKeyData() {
      byte[] data = Arrays.copy(this._privateData);
      PersistentContent.markAsPlaintext(data);
      return data;
   }

   private final byte[] getPrivateKeyData() {
      return this._privateData;
   }

   final byte[] copyPublicKeyData() {
      return Arrays.copy(this.getPublicKeyData());
   }

   final byte[] getPublicKeyData() {
      if (this._publicData == null) {
         this._publicData = new byte[this._cryptoSystem.getPublicKeyLength()];
         Certicom.assertAccessAllowed();
         NativeDL.generatePublicKey(this._cryptoSystem.getP(), this._cryptoSystem.getQ(), this._cryptoSystem.getG(), this._privateData, this._publicData);
      }

      return this._publicData;
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   public final void setHashCode() {
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

      if (obj instanceof SoftwareDHCryptoToken$DHPrivateKeyData) {
         SoftwareDHCryptoToken$DHPrivateKeyData other = (SoftwareDHCryptoToken$DHPrivateKeyData)obj;
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
