package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PSSSignatureVerifier;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SignatureVerifier;

final class PSSDecodedSignature extends DecodedSignature {
   private byte[] _data;
   private Digest _digest;

   public PSSDecodedSignature(byte[] data, Digest digest) {
      if (data != null && digest != null) {
         this._data = data;
         this._digest = digest;
      } else {
         throw new Object();
      }
   }

   @Override
   public final void initialize(Digest digest) {
      if (digest != null && digest.getClass() == this._digest.getClass()) {
         this._digest = digest;
      } else {
         throw new Object();
      }
   }

   @Override
   public final SignatureVerifier getVerifier(PublicKey key) {
      if (key instanceof Object) {
         return new PSSSignatureVerifier((RSAPublicKey)key, this._digest, this._data, 0);
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("RSA_PSS/"))).append(this._digest.getAlgorithm()).toString();
   }
}
