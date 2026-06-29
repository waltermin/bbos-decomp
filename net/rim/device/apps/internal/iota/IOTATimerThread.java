package net.rim.device.apps.internal.iota;

import net.rim.device.api.system.EventLogger;

final class IOTATimerThread extends Thread {
   private MMCProcessor _theProcessor;
   private long _timeoutVal;
   private boolean _timedOut = false;
   public static final long TIMEOUT_15_MIN;

   public IOTATimerThread(MMCProcessor proc, long time) {
      this._theProcessor = proc;
      this._timeoutVal = time;
   }

   public final synchronized void notifyDone() {
      if (!this._timedOut) {
         this.interrupt();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         Thread.sleep(this._timeoutVal);
         var3 = false;
      } finally {
         if (var3) {
            EventLogger.logEvent(4411276428801970910L, 1299475817);
            return;
         }
      }

      this._timedOut = true;
      ProvisioningServiceAgent.logEvents(
         ((StringBuffer)(new Object("Client Timed out after"))).append(this._timeoutVal / 60000).append(" minutes...").toString()
      );
      this._theProcessor.cancelProcessing();
   }
}
