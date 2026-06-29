package net.rim.device.api.crypto;

public final class ECNRSignatureVerifier implements SignatureVerifier {
   private ECPublicKey _key;
   private Digest _digest;
   private byte[] _r;
   private byte[] _s;

   public ECNRSignatureVerifier(ECPublicKey key, byte[] r, int rOffset, byte[] s, int sOffset) {
      this(key, (Digest)(new Object()), r, rOffset, s, sOffset);
   }

   public ECNRSignatureVerifier(ECPublicKey key, Digest digest, byte[] r, int rOffset, byte[] s, int sOffset) {
      if (key != null && digest != null && r != null && s != null) {
         this._key = key;
         this._digest = digest;
         int privateKeyLength = key.getECCryptoSystem().getPrivateKeyLength();
         if (rOffset >= 0 && rOffset <= r.length - 1 && sOffset >= 0 && sOffset <= s.length - 1) {
            try {
               this._r = new byte[privateKeyLength];
               if (rOffset <= 0 && r != s) {
                  this._r = r;
               } else {
                  System.arraycopy(r, rOffset, this._r, 0, Math.min(privateKeyLength, r.length - rOffset));
               }

               this._r = CryptoByteArrayArithmetic.ensureLength(this._r, privateKeyLength);
               this._s = new byte[privateKeyLength];
               if (sOffset <= 0 && r != s) {
                  this._s = s;
               } else {
                  System.arraycopy(s, sOffset, this._s, 0, Math.min(privateKeyLength, s.length - sOffset));
               }

               this._s = CryptoByteArrayArithmetic.ensureLength(this._s, privateKeyLength);
            } finally {
               throw new Object();
            }
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("ECNR/"))).append(this._digest.getAlgorithm()).toString();
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
   public final boolean verify() {
      return this._key
         .getECCryptoToken()
         .verifyECNR(
            this._key.getECCryptoSystem().getCryptoTokenData(),
            this._key.getCryptoTokenData(),
            this._digest.getDigest(false),
            0,
            this._digest.getDigestLength(),
            this._r,
            0,
            this._s,
            0
         );
   }
}
