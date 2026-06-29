package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class FIPS186PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private NativeFIPSPRNG _context;

   public FIPS186PseudoRandomSource(byte[] seed) {
      this(seed, 0, seed == null ? 0 : seed.length);
   }

   public FIPS186PseudoRandomSource(byte[] seed, int offset, int length) {
      this(seed, offset, length, null, 0, 0, true);
   }

   public FIPS186PseudoRandomSource(byte[] seed, byte[] additionalSeed) {
      this(seed, 0, seed == null ? 0 : seed.length, additionalSeed, 0, additionalSeed == null ? 0 : additionalSeed.length, true);
   }

   public FIPS186PseudoRandomSource(
      byte[] seed, int offset, int length, byte[] additionalSeed, int additionalSeedOffset, int additionalSeedLength, boolean useRevisedAlgorithm
   ) {
      if (seed != null
         && offset >= 0
         && length >= 0
         && seed.length - length >= offset
         && (
            additionalSeed == null
               || additionalSeedOffset >= 0 && additionalSeedLength >= 0 && additionalSeed.length - additionalSeedLength >= additionalSeedOffset
         )) {
         this._context = NativeFIPSPRNG.initialize(seed, offset, length, additionalSeed, additionalSeedOffset, additionalSeedLength, useRevisedAlgorithm);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "FIPS186";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         this._context.xorBytes(buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
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
      byte[] result = new byte[]{
         -124,
         31,
         -122,
         64,
         49,
         -116,
         33,
         -19,
         63,
         -105,
         40,
         19,
         -100,
         9,
         47,
         63,
         -102,
         -104,
         -18,
         26,
         -101,
         -91,
         -86,
         90,
         -12,
         -40,
         -4,
         -111,
         -69,
         -101,
         22,
         -27
      };

      try {
         FIPS186PseudoRandomSource source = new FIPS186PseudoRandomSource(SelfTestData.RANDOM_DATA);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            return;
         }
      } catch (CryptoException var3) {
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_PRS_FIPS186 = -6504736308298979555L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_PRS_FIPS186) == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_FIPS186, appRegistry);
      }
   }
}
