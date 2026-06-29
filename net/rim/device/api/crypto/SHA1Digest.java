package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SHA1Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeSHA1();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 20;
   public static final int BLOCK_LENGTH = 64;

   @Override
   public final String getAlgorithm() {
      return "SHA1";
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
      byte[] DIGEST_TEXT = new byte[]{50, -47, 12, 123, -116, -7, 101, 112, -54, 4, -50, 55, -14, -95, -99, -124, 36, 13, 58, -119};
      byte[] target = new byte[20];
      SHA1Digest digest = new SHA1Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 20)) {
         throw new CryptoSelfTestError();
      }
   }

   public static final void hmacSelfTest() {
      byte[] HMAC_KEY_DATA = new byte[]{-86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86};
      byte[] HMAC_MAC = new byte[]{-124, -56, -117, -106, 91, -123, 123, -35, -122, -41, -8, -32, -13, 99, 52, 9, -83, 98, 76, 91};

      try {
         HMACKey key = new HMACKey(HMAC_KEY_DATA);
         HMAC hmac = new HMAC(key, new SHA1Digest());
         hmac.update(SelfTestData.PLAIN_TEXT_DIGEST);
         if (hmac.checkMAC(HMAC_MAC)) {
            return;
         }
      } catch (CryptoTokenException var4) {
      } catch (CryptoUnsupportedOperationException var5) {
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_DIGEST_SHA1 = 7020805999233882560L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_SHA1) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_SHA1, appRegistry);
      }
   }
}
