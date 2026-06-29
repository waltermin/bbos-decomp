package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SHA512Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeSHA512();
   private static final int MAX_UPDATE;
   public static final int DIGEST_LENGTH;
   public static final int BLOCK_LENGTH;

   @Override
   public final String getAlgorithm() {
      return "SHA512";
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
      return 64;
   }

   @Override
   public final int getBlockLength() {
      return 128;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 64;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{
         77,
         -65,
         -8,
         108,
         -62,
         -54,
         27,
         -82,
         30,
         22,
         70,
         -118,
         5,
         -53,
         -104,
         -127,
         -55,
         127,
         23,
         83,
         -68,
         -29,
         97,
         -112,
         52,
         -119,
         -113,
         -86,
         26,
         -85,
         -28,
         41,
         -107,
         90,
         27,
         -8,
         -20,
         72,
         61,
         116,
         33,
         -2,
         60,
         22,
         70,
         97,
         58,
         89,
         -19,
         84,
         65,
         -5,
         15,
         50,
         19,
         -119,
         -9,
         127,
         72,
         -88,
         121,
         -57,
         -79,
         -15
      };
      byte[] target = new byte[64];
      SHA512Digest digest = new SHA512Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 64)) {
         throw new CryptoSelfTestError();
      }
   }

   public static final void hmacSelfTest() {
      byte[] HMAC_KEY_DATA = new byte[]{
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86,
         -86
      };
      byte[] HMAC_MAC = new byte[]{
         -2,
         -38,
         38,
         -31,
         74,
         58,
         49,
         97,
         -120,
         54,
         -30,
         -88,
         -92,
         44,
         107,
         61,
         126,
         -77,
         60,
         -85,
         -56,
         125,
         53,
         -25,
         66,
         -23,
         -27,
         65,
         124,
         2,
         15,
         -74,
         -127,
         73,
         126,
         -62,
         74,
         77,
         -16,
         -27,
         -113,
         2,
         -13,
         -82,
         71,
         17,
         -83,
         55,
         -20,
         51,
         -19,
         -110,
         92,
         60,
         -49,
         -51,
         -72,
         73,
         70,
         -36,
         -81,
         -22,
         -46,
         16
      };

      try {
         HMACKey key = new HMACKey(HMAC_KEY_DATA);
         HMAC hmac = new HMAC(key, new SHA512Digest());
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
      long ID_TEST_DIGEST_SHA512 = 8487396757118110419L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_SHA512) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_SHA512, appRegistry);
      }
   }
}
