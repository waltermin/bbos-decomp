package com.fourthpass.wapstack.util;

import java.util.Timer;
import net.rim.device.internal.proxy.Proxy;

public final class WTimer {
   private WTimerTask[] _taskList;
   private int _listEntryCount;
   private Timer _timer;

   public WTimer() {
      Proxy.getInstance().invokeAndWait(new WTimer$1(this));
      this._taskList = new WTimerTask[4];
   }

   public final void stopTimer() {
      this._timer.cancel();
   }

   public final synchronized void addTask(ITimerScheduler scheduler, int delay, byte taskId) {
      WTimerTask task = new WTimerTask(this, scheduler, taskId);
      if (this._listEntryCount + 1 == this._taskList.length) {
         WTimerTask[] queue = new WTimerTask[2 * this._taskList.length];
         System.arraycopy(this._taskList, 0, queue, 0, this._listEntryCount + 1);
         this._taskList = queue;
      }

      this._taskList[this._listEntryCount] = task;
      this._listEntryCount++;
      this._timer.schedule(task, delay);
   }

   public final synchronized void removeTask(ITimerScheduler scheduler) {
      if (this._listEntryCount > 0) {
         boolean done = false;

         while (!done) {
            int i = 0;

            while (i < this._listEntryCount && this._taskList[i]._scheduler != scheduler) {
               i++;
            }

            if (i < this._listEntryCount) {
               this.removeTaskByIndex(i, this._listEntryCount);
            } else {
               done = true;
            }
         }
      }
   }

   public final synchronized void removeTask(ITimerScheduler scheduler, byte taskId) {
      if (this._listEntryCount > 0) {
         int i = 0;

         while (i < this._listEntryCount && (this._taskList[i]._scheduler != scheduler || this._taskList[i]._schedulerId != taskId)) {
            i++;
         }

         if (i < this._listEntryCount) {
            this.removeTaskByIndex(i, this._listEntryCount);
         }
      }
   }

   private final void removeTaskByIndex(int index, int qLength) {
      WTimerTask[] queue = new WTimerTask[this._taskList.length];
      int qIndex = 0;

      for (int j = 0; j < qLength; j++) {
         if (j != index) {
            queue[qIndex] = this._taskList[j];
            qIndex++;
         } else {
            this._taskList[j].cancel();
         }
      }

      this._taskList = queue;
      this._listEntryCount--;
   }
}
