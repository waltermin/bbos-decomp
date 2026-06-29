package net.rim.device.api.crypto;

public final class DSASignatureSigner implements SignatureSigner {
   private DSAPrivateKey _key;
   private Digest _digest;
   private int _privateKeyLength;

   public final void sign(byte[] r, int rOffset, byte[] s, int sOffset) {
      this._key
         .getDSACryptoToken()
         .signDSA(
            this._key.getDSACryptoSystem().getCryptoTokenData(),
            this._key.getCryptoTokenData(),
            this._digest.getDigest(false),
            0,
            this._digest.getDigestLength(),
            r,
            rOffset,
            s,
            sOffset
         );
   }

   public final int getSLength() {
      return this._privateKeyLength;
   }

   public final int getRLength() {
      return this._privateKeyLength;
   }

   @Override
   public final void update(int data) {
      this._digest.update(data);
   }

   @Override
   public final void update(byte[] data) {
      this.update(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._digest.update(data, offset, length);
   }

   @Override
   public final void reset() {
      this._digest.reset();
   }

   @Override
   public final String getDigestAlgorithm() {
      return this._digest.getAlgorithm();
   }

   @Override
   public final String getAlgorithm() {
      return "DSA/" + this._digest.getAlgorithm();
   }

   public DSASignatureSigner(DSAPrivateKey key, Digest digest) {
      if (key != null && digest != null) {
         this._key = key;
         this._digest = digest;
         if (this._digest.getDigestLength() != 20) {
            throw new IllegalArgumentException();
         }

         this._privateKeyLength = key.getDSACryptoSystem().getPrivateKeyLength();
         if (digest.getDigestLength() > this._privateKeyLength) {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public DSASignatureSigner(DSAPrivateKey key) {
      this(key, new SHA1Digest());
   }
}
