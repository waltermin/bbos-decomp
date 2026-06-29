package net.rim.device.internal.crypto.fips;

import net.rim.device.api.crypto.SelfTestModule;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Radio;

class SelfTests$Tester extends Thread {
   private final SelfTests this$0;

   public SelfTests$Tester(SelfTests _1) {
      this.this$0 = _1;
   }

   private int testModule(SelfTestModule selfTestModule, int testIndex) {
      if (selfTestModule == null) {
         return testIndex;
      }

      int size = selfTestModule.getNumTests(this.this$0._startupRun);

      for (int i = 0; i < size; i++) {
         try {
            selfTestModule.test(i);
            if (this.this$0._startupRun) {
               this.this$0._startupDialog.setTestPassed(testIndex);
            } else {
               this.this$0._detailedDialog.setTestPassed(testIndex, true);
            }
         } catch (Throwable t) {
            EventLogger.logEvent(7146679653930594060L, t.toString().getBytes(), 2);
            this.this$0._testsFailed = true;
            if (this.this$0._startupRun) {
               this.this$0._startupDialog.setTestFailed(testIndex);
            } else {
               this.this$0._detailedDialog.setTestFailed(testIndex);
            }
         } finally {
            testIndex++;
         }
      }

      return testIndex;
   }

   @Override
   public void run() {
      int testIndex = 0;

      for (int i = 0; i < this.this$0._modules.length; i++) {
         testIndex = this.testModule(this.this$0._modules[i], testIndex);
      }

      if (this.this$0._testsFailed) {
         Radio.requestPowerOff();
         if (this.this$0._startupRun) {
            this.this$0._startupDialog.testsFailed();
         } else {
            this.this$0._detailedDialog.testsFailed();
         }
      } else if (this.this$0._startupRun) {
         this.this$0._startupDialog.testsPassed();
      } else {
         this.this$0._detailedDialog.testsPassed();
      }
   }
}
