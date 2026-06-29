package net.rim.device.apps.api.reminders;

import net.rim.device.apps.internal.api.quincy.QuincyManager;

final class ReminderManager$ReminderScanner extends Thread {
   long _reminderProviderID;
   private final ReminderManager this$0;

   public ReminderManager$ReminderScanner(ReminderManager _1) {
      this.this$0 = _1;
      this._reminderProviderID = Long.MAX_VALUE;
   }

   public final void notifyAllReminderProviders() {
      this.notifyReminderProviderThreaded(Long.MAX_VALUE, null, 0);
   }

   public final void notifyReminderProviderThreaded(long reminderProviderID, Reminder reminder, int action) {
      this.this$0._changeNotifier.addRunnable(new ReminderManager$ReminderScanner$ReminderScannerNotifier(this, reminderProviderID, reminder, action));
   }

   public final void notifyReminderProvider(long reminderProviderID) {
      if (this.this$0._reminderScanningEnabled) {
         synchronized (this.this$0._reminderProviders) {
            if (reminderProviderID == this.this$0._nextReminderProviderID) {
               this.this$0._nextReminderID = Long.MAX_VALUE;
               this.this$0._nextReminderProviderID = Long.MAX_VALUE;
               this.this$0._nextReminderTime = Long.MAX_VALUE;
            }

            this._reminderProviderID = reminderProviderID;
            this.clearReminder(this._reminderProviderID);
            this.this$0._reminderProviders.notify();
         }
      }
   }

   private final boolean shouldBeNextAlarm(Reminder reminder, long reminderProviderID, long currentTime) {
      boolean result = false;
      long rtime = reminder.getReminderTime(this.this$0._tz) / 1000;
      int seconds = (int)(rtime % 60);
      rtime = (rtime - seconds) * 1000;
      if (rtime < this.this$0._nextReminderTime && rtime > currentTime) {
         if (this.this$0._nextScheduledReminder != null
            && this.this$0._nextScheduledReminder._reminderTime == rtime
            && this.this$0._nextScheduledReminder._reminderID == reminder.getReminderID()
            && this.this$0._nextScheduledReminder._reminderProviderID == reminderProviderID) {
            this.this$0.logReminderEvent(1095975752, this.this$0.findReminderProvider(reminderProviderID), reminder);
         } else {
            result = true;
         }
      } else if (rtime <= currentTime) {
         ReminderModel rm = reminder.getReminderData();
         long firedfor = -1;
         if (rm != null) {
            firedfor = rm.getReminderFiredFor();
         }

         this.this$0.logReminderEvent(1111573586, this.this$0.findReminderProvider(reminderProviderID), reminder, firedfor, null);
      }

      return result;
   }

   public final void clearReminder(long reminderProviderID) {
      synchronized (this.this$0._reminderProviders) {
         for (int i = this.this$0._reminderProviders.length - 1; i >= 0; i--) {
            if (this.this$0._reminderProviders[i].getReminderProviderID() == reminderProviderID) {
               this.this$0._reminders[i] = Long.MAX_VALUE;
               break;
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      long adjustedCurrentTime = 0;
      synchronized (this.this$0._reminderProviders) {
         while (true) {
            try {
               label209:
               try {
                  this.this$0._reminderProviders.wait();
               } finally {
                  break label209;
               }

               if (this.this$0._reminderScanningEnabled) {
                  adjustedCurrentTime = this.this$0.getAdjustedCurrentTimeMillis();

                  for (int i = this.this$0._reminderProviders.length - 1; i >= 0; i--) {
                     if (this.this$0._reminderProviderEnabled[i]) {
                        Reminder r = this.this$0._reminderProviders[i].getReminder(this.this$0._reminders[i]);
                        long reminderProviderID = this.this$0._reminderProviders[i].getReminderProviderID();
                        if (r != null) {
                           long rtime = r.getReminderTime(this.this$0._tz) + 60000;
                           if (rtime >= adjustedCurrentTime) {
                              r = null;
                              this.this$0._reminders[i] = Long.MAX_VALUE;
                           }
                        }

                        if (r == null || this._reminderProviderID == Long.MAX_VALUE || this._reminderProviderID == reminderProviderID) {
                           Object o = this.this$0._reminderProviders[i].getNextReminder(adjustedCurrentTime, this.this$0._tz);
                           if (!(o instanceof Reminder)) {
                              this.this$0._reminders[i] = Long.MAX_VALUE;
                              r = null;
                           } else {
                              r = (Reminder)o;
                              this.this$0._reminders[i] = r.getReminderID();
                           }

                           o = null;
                        }

                        if (r != null) {
                           if (this.shouldBeNextAlarm(r, reminderProviderID, adjustedCurrentTime)) {
                              this.this$0.setNewAlarm(r, this.this$0._reminderProviders[i]);
                           }

                           Object var29 = null;
                        }
                     }
                  }

                  if (this.this$0._nextReminderTime == Long.MAX_VALUE) {
                     this.this$0.cancelAllReminders();
                  }
               }
            } catch (Throwable var25) {
               Throwable t = var25;

               try {
                  this.this$0.logReminderEvent(1178686540, null, null);
                  if (!(t instanceof OutOfMemoryError)) {
                     QuincyManager.sendJavaLogworthy("ReminderScanner");
                  }

                  ReminderManager.logEvent(1381193029, 2);
                  continue;
               } finally {
                  continue;
               }
            }
         }
      }
   }
}
