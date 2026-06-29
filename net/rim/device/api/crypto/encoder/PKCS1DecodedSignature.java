package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PKCS1SignatureVerifier;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SignatureVerifier;

final class PKCS1DecodedSignature extends DecodedSignature {
   private byte[] _data;
   private Digest _digest;

   public PKCS1DecodedSignature(byte[] data, Digest digest) {
      if (data != null && digest != null) {
         this._data = data;
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
      if (key instanceof RSAPublicKey) {
         return new PKCS1SignatureVerifier((RSAPublicKey)key, this._digest, this._data, 0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "RSA_PKCS1/" + this._digest.getAlgorithm();
   }
}
