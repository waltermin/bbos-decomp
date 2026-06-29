package net.rim.device.api.crypto;

import net.rim.device.internal.i18n.CommonResource;

public final class Crypto1SelfTestModule implements SelfTestModule {
   int _prngResult = 0;
   boolean _prngTestsDone = false;

   @Override
   public final int getNumTests(boolean startupTests) {
      return startupTests ? 12 : 18;
   }

   @Override
   public final void test(int testIndex) {
      switch (testIndex) {
         case -1:
            throw new Object();
         case 0:
         default:
            FIPS186PseudoRandomSource.selfTest();
            return;
         case 1:
            SoftwareDSACryptoToken.selfTest();
            return;
         case 2:
            SoftwareRSACryptoToken.selfTest();
            return;
         case 3:
            CBCDecryptorEngine.selfTest();
            CBCEncryptorEngine.selfTest();
            return;
         case 4:
            SHA224Digest.selfTest();
            return;
         case 5:
            SHA384Digest.selfTest();
            return;
         case 6:
            SHA224Digest.hmacSelfTest();
            return;
         case 7:
            SHA384Digest.hmacSelfTest();
            return;
         case 8:
            SoftwareAESCryptoToken.selfTest();
            return;
         case 9:
            SoftwareDESCryptoToken.selfTest();
            return;
         case 10:
            SoftwareTripleDESCryptoToken.selfTest();
            return;
         case 11:
            PKCS1SignatureSigner.selfTest();
            return;
         case 12:
            PKCS5FormatterEngine.selfTest();
            PKCS5UnformatterEngine.selfTest();
            return;
         case 13:
            PKCS1MGF1PseudoRandomSource.selfTest();
            return;
         case 14:
            PKCS5KDF1PseudoRandomSource.selfTest();
            return;
         case 15:
            PKCS5KDF2PseudoRandomSource.selfTest();
            return;
         case 16:
            P1363KDF1PseudoRandomSource.selfTest();
            return;
         case 17:
            ARC4PseudoRandomSource.selfTest();
      }
   }

   @Override
   public final String[] getTestNames(boolean startupTests) {
      String[] testNames = CommonResource.getStringArray(10021);
      if (startupTests) {
         String[] names = new Object[this.getNumTests(true)];
         System.arraycopy(testNames, 0, names, 0, names.length);
         return names;
      } else {
         return testNames;
      }
   }
}
