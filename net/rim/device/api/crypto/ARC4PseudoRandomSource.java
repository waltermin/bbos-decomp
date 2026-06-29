package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class ARC4PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private NativeARC4PRNG _context;

   public ARC4PseudoRandomSource(byte[] seed) {
      this(seed, 0, seed == null ? 0 : seed.length);
   }

   public ARC4PseudoRandomSource(byte[] seed, int offset, int length) {
      this._context = new NativeARC4PRNG(seed, offset, length);
   }

   public ARC4PseudoRandomSource(ARC4Key key) {
      this(key == null ? null : key.getData(), 0, key == null ? 0 : key.getData().length);
   }

   @Override
   public final String getAlgorithm() {
      return "ARC4";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      this._context.xorBytes(buffer, offset, length);
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{116, -108, -62, -25, 16, 75, 8, 121};
      byte[] TEST_KEY = new byte[]{1, 35, 69, 103, -119, -85, -51, -17};
      int length = CIPHER_TEXT.length;

      try {
         ARC4PseudoRandomSource source = new ARC4PseudoRandomSource(new ARC4Key(TEST_KEY));
         byte[] cipherText = new byte[length];
         source.xorBytes(cipherText, 0, length);
         if (Arrays.equals(cipherText, 0, CIPHER_TEXT, 0, length)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_PRS_ARC4 = -2489129186296513914L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_PRS_ARC4) == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_ARC4, appRegistry);
      }
   }
}
