package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class RIPEMD160Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeRIPEMD160();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 20;
   public static final int BLOCK_LENGTH = 64;

   @Override
   public final String getAlgorithm() {
      return "RIPEMD160";
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
         throw new Object();
      }
   }

   @Override
   public final int getDigestLength() {
      return 20;
   }

   @Override
   public final int getBlockLength() {
      return 64;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 20;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{-9, 28, 39, 16, -100, 105, 44, 27, 86, -69, -36, -21, 91, -99, 40, 101, -77, 112, -115, -68};
      byte[] target = new byte[20];
      RIPEMD160Digest digest = new RIPEMD160Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 20)) {
         throw new Object();
      }
   }

   static {
      long ID_TEST_DIGEST_RIPEMD160 = -4410375853813048947L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_RIPEMD160) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_RIPEMD160, appRegistry);
      }
   }
}
