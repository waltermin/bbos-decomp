package net.rim.device.apps.internal.diagnostic;

import java.util.TimerTask;

public final class TestEmailService$emailExpired extends TimerTask {
   private final TestEmailService this$0;

   public TestEmailService$emailExpired(TestEmailService _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.timeoutFlag = true;
   }
}
