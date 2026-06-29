package net.rim.device.api.crypto;

public final class NullSignatureVerifier implements SignatureVerifier {
   private boolean _verify;

   public NullSignatureVerifier() {
   }

   public NullSignatureVerifier(boolean verify) {
      this._verify = verify;
   }

   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final void update(int data) {
   }

   @Override
   public final void update(byte[] data) {
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
   }

   @Override
   public final boolean verify() {
      return this._verify;
   }
}
