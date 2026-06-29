package net.rim.device.internal.crypto;

import net.rim.device.api.crypto.CryptoSelfTestError;
import net.rim.device.api.crypto.SelfTestModule;
import net.rim.device.internal.i18n.CommonResource;

public final class CryptoBlockSelfTestModule implements SelfTestModule {
   @Override
   public final int getNumTests(boolean startupTests) {
      return 1;
   }

   @Override
   public final void test(int testIndex) {
      switch (testIndex) {
         case 0:
            CryptoBlock.selfTest();
            return;
         default:
            throw new CryptoSelfTestError();
      }
   }

   @Override
   public final String[] getTestNames(boolean startupTests) {
      return CommonResource.getStringArray(10019);
   }
}
