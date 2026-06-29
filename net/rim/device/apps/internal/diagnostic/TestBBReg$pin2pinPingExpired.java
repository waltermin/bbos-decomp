package net.rim.device.apps.internal.diagnostic;

import java.util.TimerTask;

public final class TestBBReg$pin2pinPingExpired extends TimerTask {
   private final TestBBReg this$0;

   public TestBBReg$pin2pinPingExpired(TestBBReg _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.timeoutFlag = true;
   }
}
