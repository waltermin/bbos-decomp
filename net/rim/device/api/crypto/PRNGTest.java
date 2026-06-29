package net.rim.device.api.crypto;

public final class PRNGTest {
   public static final int MONO_BIT_TEST_PASSED = 1;
   public static final int POKER_TEST_PASSED = 2;
   public static final int RUNS_TEST_PASSED = 4;
   public static final int LONG_RUNS_TEST_PASSED = 8;
   public static final int ALL_TESTS_PASSED = 15;
   public static final int ALL_FIPS_TESTS_PASSED = 3;

   private PRNGTest() {
   }

   public static final int testPRNG(PseudoRandomSource source) {
      if (source == null) {
         throw new IllegalArgumentException();
      } else {
         return testForRandomness(source.getBytes(2500));
      }
   }

   public static final int testRandomSource() {
      int result = 0;
      byte[] data = new byte[2500];

      for (int i = 0; i < 10; i++) {
         RandomSource.getBytes(data);
         result = testForRandomness(data);
         if (result == 15) {
            return result;
         }

         RandomSource.add(SelfTestData.RANDOM_DATA);
         RandomSource.add(data);
      }

      return result;
   }

   static final native int testForRandomness(byte[] var0);
}
