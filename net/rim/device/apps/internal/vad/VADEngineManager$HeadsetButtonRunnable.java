package net.rim.device.apps.internal.vad;

final class VADEngineManager$HeadsetButtonRunnable implements Runnable {
   private final VADEngineManager this$0;

   VADEngineManager$HeadsetButtonRunnable(VADEngineManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._headsetButtonInvokeLater != -1) {
         if (this.this$0._engineState == 3) {
            this.this$0.activate(0);
         }

         this.this$0._headsetButtonInvokeLater = -1;
      }
   }
}
