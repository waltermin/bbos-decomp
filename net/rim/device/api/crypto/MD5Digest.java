package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class MD5Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeMD5();
   private static final int MAX_UPDATE;
   public static final int DIGEST_LENGTH;
   public static final int BLOCK_LENGTH;

   @Override
   public final String getAlgorithm() {
      return "MD5";
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
      return 64;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 16;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{-61, -4, -45, -41, 97, -110, -28, 0, 125, -5, 73, 108, -54, 103, -31, 59};
      byte[] target = new byte[16];
      MD5Digest digest = new MD5Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 16)) {
         throw new CryptoSelfTestError();
      }
   }

   static {
      long ID_TEST_DIGEST_MD5 = 5612994492217710341L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_MD5) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_MD5, appRegistry);
      }
   }
}
