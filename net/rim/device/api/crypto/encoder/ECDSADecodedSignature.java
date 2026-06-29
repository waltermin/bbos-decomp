package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.ECDSASignatureVerifier;
import net.rim.device.api.crypto.ECPublicKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureVerifier;

final class ECDSADecodedSignature extends DecodedSignature {
   private byte[] _r;
   private byte[] _s;
   private Digest _digest;

   public ECDSADecodedSignature(byte[] r, byte[] s, Digest digest) {
      if (r != null && s != null && digest != null) {
         this._r = r;
         this._s = s;
         this._digest = digest;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void initialize(Digest digest) {
      if (digest != null && digest.getClass() == this._digest.getClass()) {
         this._digest = digest;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final SignatureVerifier getVerifier(PublicKey key) {
      if (key instanceof ECPublicKey) {
         return new ECDSASignatureVerifier((ECPublicKey)key, this._digest, this._r, 0, this._s, 0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "ECDSA/" + this._digest.getAlgorithm();
   }
}
