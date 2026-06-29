package net.rim.device.api.crypto;

public final class NullDigest extends AbstractDigest implements Digest {
   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final void reset() {
   }

   @Override
   public final void update(int data) {
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
   }

   @Override
   public final int getDigestLength() {
      return 0;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      return 0;
   }
}
