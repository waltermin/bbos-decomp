package net.rim.device.api.crypto;

public final class NullSignatureSigner implements SignatureSigner {
   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final String getDigestAlgorithm() {
      return "Null";
   }

   @Override
   public final void reset() {
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
}
