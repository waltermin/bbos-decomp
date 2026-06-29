package net.rim.device.apps.internal.diagnostic;

import java.util.TimerTask;

public final class TestRadio$timerExpired extends TimerTask {
   private final TestRadio this$0;

   public TestRadio$timerExpired(TestRadio _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.timeoutFlag = true;
   }
}
