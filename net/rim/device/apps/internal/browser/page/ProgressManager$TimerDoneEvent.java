package net.rim.device.apps.internal.browser.page;

final class ProgressManager$TimerDoneEvent implements Runnable {
   private final ProgressManager this$0;

   ProgressManager$TimerDoneEvent(ProgressManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.update(6);
   }
}
