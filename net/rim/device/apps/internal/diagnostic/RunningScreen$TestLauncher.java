package net.rim.device.apps.internal.diagnostic;

public final class RunningScreen$TestLauncher extends Thread {
   RunningScreen screen;
   private final RunningScreen this$0;

   public RunningScreen$TestLauncher(RunningScreen _1, RunningScreen s) {
      this.this$0 = _1;
      this.screen = s;
   }

   @Override
   public final void run() {
      synchronized (this.screen) {
         this.this$0.initReport();
         this.this$0.tThread = new RunningScreen$TestSuites(this.this$0, this.screen);
         this.this$0.tThread.start();
         this.this$0.bThread = new RunningScreen$Blinking(this.this$0, this.screen);
         this.this$0.bThread.start();

         try {
            this.this$0.tThread.join();
            this.this$0.bThread.join();
         } finally {
            return;
         }
      }
   }
}
