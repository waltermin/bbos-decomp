package net.rim.device.apps.internal.mms.service;

import net.rim.device.apps.internal.mms.api.MMSTask;

class BackgroundTaskThread$1TaskAppendingRunnable implements Runnable {
   private MMSTask _task;

   BackgroundTaskThread$1TaskAppendingRunnable(MMSTask task) {
      this._task = task;
   }

   @Override
   public void run() {
      BackgroundTaskThread.addTask(this._task);
   }
}
