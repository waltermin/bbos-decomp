package net.rim.device.api.crypto;

final class SoftwareHMACCryptoToken$HMACContext implements CryptoTokenMACContext {
   private Digest _digest;
   private Digest _digest2;
   private byte[] _ipad;
   private byte[] _opad;
   private static final byte IPAD_BYTE = 54;
   private static final byte OPAD_BYTE = 92;

   public SoftwareHMACCryptoToken$HMACContext(SoftwareHMACCryptoToken$HMACKeyData key, Digest digest) {
      if (key != null && digest != null && digest.getBlockLength() >= 0) {
         this._digest = digest;

         try {
            this._digest2 = DigestFactory.getInstance(this._digest.getAlgorithm());
         } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
         }

         int digestBlockLength = this._digest.getBlockLength();
         this._ipad = new byte[digestBlockLength];
         this._opad = new byte[digestBlockLength];
         byte[] keyData = key.getData();
         if (keyData.length > digestBlockLength) {
            digest.update(keyData);
            keyData = digest.getDigest();
         }

         System.arraycopy(keyData, 0, this._ipad, 0, keyData.length);
         System.arraycopy(keyData, 0, this._opad, 0, keyData.length);

         for (int i = 0; i < digestBlockLength; i++) {
            this._ipad[i] = (byte)(this._ipad[i] ^ 54);
            this._opad[i] = (byte)(this._opad[i] ^ 92);
         }

         this._digest.update(this._ipad);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void reset() {
      this._digest.reset();
      this._digest.update(this._ipad);
   }

   public final int getMAC(byte[] buffer, int offset, boolean reset) {
      if (buffer != null && offset >= 0 && buffer.length - this._digest.getDigestLength() >= offset) {
         byte[] h = this._digest.getDigest(reset);
         this._digest2.update(this._opad);
         this._digest2.update(h);
         int numBytes = this._digest2.getDigest(buffer, offset);
         if (reset) {
            this.reset();
         }

         return numBytes;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
