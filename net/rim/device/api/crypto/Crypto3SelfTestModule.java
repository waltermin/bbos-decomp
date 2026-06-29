package net.rim.device.api.crypto;

import net.rim.device.internal.i18n.CommonResource;

public final class Crypto3SelfTestModule implements SelfTestModule {
   private static final int TOTAL_STARTUP_TESTS = 8;
   private static final int TOTAL_STARTUP_TESTS_NO_ECC = 7;
   private static final int TOTAL_AFTER_STARTUP = 22;
   private static final int TOTAL_AFTER_STARTUP_NO_ECC = 4;

   @Override
   public final int getNumTests(boolean startupTests) {
      int numTests = 0;
      if (startupTests) {
         return NativeEC.isSupported("EC163K1", 2) ? 8 : 7;
      } else {
         return NativeEC.isSupported("EC163K1", 2) ? 30 : 11;
      }
   }

   @Override
   public final void test(int testIndex) {
      boolean ecSupported = NativeEC.isSupported("EC163K1", 2);
      int numTestsToSkip = 0;
      if (!ecSupported && testIndex >= 5) {
         numTestsToSkip++;
      }

      testIndex += numTestsToSkip;
      switch (testIndex) {
         case -1:
            throw new Object();
         case 0:
         default:
            SoftwareSkipjackCryptoToken.selfTest();
            return;
         case 1:
            CBCMAC.selfTest();
            return;
         case 2:
            CFBDecryptor.selfTest();
            CFBEncryptor.selfTest();
            return;
         case 3:
            OFBPseudoRandomSource.selfTest();
            return;
         case 4:
            CTRPseudoRandomSource.selfTest();
            return;
         case 5:
            PSSSignatureSigner.selfTest();
            return;
         case 6:
            X931SignatureSigner.selfTest();
            return;
         case 7:
            SoftwareECCryptoToken.selfTest();
            return;
         case 8:
            RFC2631KDFPseudoRandomSource.selfTest();
            return;
         case 9:
            X942KDFPseudoRandomSource.selfTest();
            return;
         case 10:
            X963KDFPseudoRandomSource.selfTest();
            return;
         case 11:
            SPKMKDFPseudoRandomSource.selfTest();
            return;
         case 12:
            SoftwareECCryptoToken.selfTest("EC160R1");
            return;
         case 13:
            SoftwareECCryptoToken.selfTest("EC163K1");
            return;
         case 14:
            SoftwareECCryptoToken.selfTest("EC163K2");
            return;
         case 15:
            SoftwareECCryptoToken.selfTest("EC163R2");
            return;
         case 16:
            SoftwareECCryptoToken.selfTest("EC192R1");
            return;
         case 17:
            SoftwareECCryptoToken.selfTest("EC224R1");
            return;
         case 18:
            SoftwareECCryptoToken.selfTest("EC233K1");
            return;
         case 19:
            SoftwareECCryptoToken.selfTest("EC233R1");
            return;
         case 20:
            SoftwareECCryptoToken.selfTest("EC239K1");
            return;
         case 21:
            SoftwareECCryptoToken.selfTest("EC256R1");
            return;
         case 22:
            SoftwareECCryptoToken.selfTest("EC283K1");
            return;
         case 23:
            SoftwareECCryptoToken.selfTest("EC283R1");
            return;
         case 24:
            SoftwareECCryptoToken.selfTest("EC384R1");
            return;
         case 25:
            SoftwareECCryptoToken.selfTest("EC409K1");
            return;
         case 26:
            SoftwareECCryptoToken.selfTest("EC409R1");
            return;
         case 27:
            SoftwareECCryptoToken.selfTest("EC521R1");
            return;
         case 28:
            SoftwareECCryptoToken.selfTest("EC571K1");
            return;
         case 29:
            SoftwareECCryptoToken.selfTest("EC571R1");
      }
   }

   @Override
   public final String[] getTestNames(boolean startupTests) {
      String[] tests = CommonResource.getStringArray(10022);
      boolean ecSupported = NativeEC.isSupported("EC163K1", 2);
      int numTests = this.getNumTests(startupTests);
      int index = 0;
      if (!startupTests && (index <= 0 || !ecSupported)) {
         if (!ecSupported) {
            int firstSet = 7 - index;
            String[] temp = new Object[numTests];
            System.arraycopy(tests, index, temp, 0, firstSet);
            System.arraycopy(tests, 8, temp, firstSet, 4);
            return temp;
         } else {
            return tests;
         }
      } else {
         String[] temp = new Object[numTests];
         System.arraycopy(tests, index, temp, 0, temp.length);
         return temp;
      }
   }
}
