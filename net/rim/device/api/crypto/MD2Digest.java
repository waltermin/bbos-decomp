package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class MD2Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeMD2();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 16;
   public static final int BLOCK_LENGTH = 16;

   @Override
   public final String getAlgorithm() {
      return "MD2";
   }

   @Override
   public final void reset() {
      this._context.reset();
   }

   @Override
   public final void update(int data) {
      this._context.update(data);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         while (length > 0) {
            int updated = Math.min(length, 1024);
            this._context.update(data, offset, updated);
            offset += updated;
            length -= updated;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getDigestLength() {
      return 16;
   }

   @Override
   public final int getBlockLength() {
      return 16;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 16;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{78, -115, -33, -13, 101, 2, -110, -85, 90, 65, 8, -61, -86, 71, -108, 11};
      byte[] target = new byte[16];
      MD2Digest digest = new MD2Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 16)) {
         throw new CryptoSelfTestError();
      }
   }

   static {
      long ID_TEST_DIGEST_MD2 = 5757919794127722228L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_MD2) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_MD2, appRegistry);
      }
   }
}
