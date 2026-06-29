package net.rim.device.apps.internal.supl;

final class SuplSessionMonitor extends Thread {
   private SuplSession _runner = null;
   private SuplSessionManager _callback = null;

   public SuplSessionMonitor(SuplSession runner, SuplSessionManager callback) {
      this._runner = runner;
      this._callback = callback;
   }

   @Override
   public final void run() {
      label17:
      try {
         System.out.println("Entering SuplSession run()");
         this._runner.run();
         System.out.println("Leaving SuplSession run()");
      } finally {
         break label17;
      }

      this._callback.sessionFinished(this._runner);
   }
}
