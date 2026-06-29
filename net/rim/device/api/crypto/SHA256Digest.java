package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SHA256Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeSHA256();
   private static final int MAX_UPDATE;
   public static final int DIGEST_LENGTH;
   public static final int BLOCK_LENGTH;

   @Override
   public final String getAlgorithm() {
      return "SHA256";
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
      return 32;
   }

   @Override
   public final int getBlockLength() {
      return 64;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 32;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{
         113, -60, -128, -33, -109, -42, -82, 47, 30, -6, -47, 68, 124, 102, -55, 82, 94, 49, 98, 24, -49, 81, -4, -115, -98, -40, 50, -14, -38, -15, -117, 115
      };
      byte[] target = new byte[32];
      SHA256Digest digest = new SHA256Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 32)) {
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
         -86
      };
      byte[] HMAC_MAC = new byte[]{
         68, -34, -94, -121, 67, -53, -8, 33, 5, -95, 86, -44, -127, 44, -90, 5, 126, 97, 64, 82, -128, -121, -108, 78, 32, -100, 49, -11, -98, -121, 18, -121
      };

      try {
         HMACKey key = new HMACKey(HMAC_KEY_DATA);
         HMAC hmac = new HMAC(key, new SHA256Digest());
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
      long ID_TEST_DIGEST_SHA256 = 6108807167582128612L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_SHA256) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_SHA256, appRegistry);
      }
   }
}
