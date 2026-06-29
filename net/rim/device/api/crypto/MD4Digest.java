package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class MD4Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeMD4();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 16;
   public static final int BLOCK_LENGTH = 64;

   @Override
   public final String getAlgorithm() {
      return "MD4";
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
      byte[] DIGEST_TEXT = new byte[]{-41, -98, 28, 48, -118, -91, -69, -51, -18, -88, -19, 99, -33, 65, 45, -87};
      byte[] target = new byte[16];

      label26:
      try {
         MD4Digest digest = new MD4Digest();
         digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
         digest.getDigest(target, 0);
      } finally {
         break label26;
      }

      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 16)) {
         throw new Object();
      }
   }

   static {
      long ID_TEST_DIGEST_MD4 = 6654798657758046330L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_MD4) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_MD4, appRegistry);
      }
   }
}
