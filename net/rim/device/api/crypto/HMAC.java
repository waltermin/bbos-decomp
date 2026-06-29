package net.rim.device.api.crypto;

public final class HMAC extends AbstractMAC implements MAC {
   private Digest _digest;
   private HMACCryptoToken _cryptoToken;
   private CryptoTokenMACContext _context;

   public HMAC(HMACKey key, Digest digest) {
      if (key != null && digest != null) {
         this._digest = digest;
         this._cryptoToken = key.getHMACCryptoToken();
         this._context = this._cryptoToken.initialize(key.getCryptoTokenData(), this._digest);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "HMAC/" + this._digest.getAlgorithm();
   }

   @Override
   public final void reset() {
      this._cryptoToken.reset(this._context);
   }

   @Override
   public final int getLength() {
      return this._digest.getDigestLength();
   }

   @Override
   public final void update(int data) {
      this._digest.update(data);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._digest.update(data, offset, length);
   }

   @Override
   public final int getMAC(byte[] buffer, int offset, boolean reset) {
      return this._cryptoToken.getMAC(this._context, buffer, offset, reset);
   }
}
