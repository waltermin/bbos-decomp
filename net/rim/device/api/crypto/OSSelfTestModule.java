package net.rim.device.api.crypto;

import net.rim.device.internal.i18n.CommonResource;

public class OSSelfTestModule implements SelfTestModule {
   int _prngResult = 0;
   boolean _prngTestsDone = false;

   @Override
   public int getNumTests(boolean startupTests) {
      return 8;
   }

   @Override
   public void test(int testIndex) {
      if (testIndex > 1 && !this._prngTestsDone) {
         this._prngTestsDone = true;
         this._prngResult = PRNGTest.testRandomSource();
      }

      switch (testIndex) {
         case -1:
            throw new CryptoSelfTestError();
         case 0:
         default:
            SHA1Digest.selfTest();
            return;
         case 1:
            SHA256Digest.selfTest();
            return;
         case 2:
            SHA512Digest.selfTest();
            return;
         case 3:
            SHA1Digest.hmacSelfTest();
            return;
         case 4:
            SHA256Digest.hmacSelfTest();
            return;
         case 5:
            SHA512Digest.hmacSelfTest();
            return;
         case 6:
            ITPolicyAuthentication.selfTest();
            return;
         case 7:
            if ((this._prngResult & 1) == 0) {
               throw new CryptoSelfTestError("RS mono bit failure");
            }
            break;
         case 8:
            if ((this._prngResult & 2) == 0) {
               throw new CryptoSelfTestError("RS poker failure");
            }
      }
   }

   @Override
   public String[] getTestNames(boolean startupTests) {
      String[] testStrings = CommonResource.getStringArray(10020);
      String[] temp = new String[this.getNumTests(startupTests)];
      System.arraycopy(testStrings, 0, temp, 0, temp.length);
      return temp;
   }
}
