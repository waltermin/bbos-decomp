package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.RIMGlobalMessagePoster;

final class DateTimeSetupWizard$ConnectionTimeOutThread extends Thread {
   private int _parentProcessID;
   private int _sleepPeriod;
   private boolean _run;

   DateTimeSetupWizard$ConnectionTimeOutThread(int parentProcessID, int waitPeriod) {
      this._parentProcessID = parentProcessID;
      this._sleepPeriod = waitPeriod;
      this._run = true;
   }

   @Override
   public final void run() {
      try {
         Thread.sleep(this._sleepPeriod);
         if (this._run) {
            RIMGlobalMessagePoster.postGlobalEvent(this._parentProcessID, DateTimeSetupWizard.WIZARD_WAIT_THREAD_UPDATE, 0, 0, null, null);
            return;
         }
      } finally {
         return;
      }
   }

   public final void stop() {
      this._run = false;
   }
}
