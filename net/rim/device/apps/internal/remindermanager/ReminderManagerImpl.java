package net.rim.device.apps.internal.remindermanager;

import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderRunnable;

final class ReminderManagerImpl extends ReminderManager {
   @Override
   public final void cancelNextReminder() {
      if (super._nextScheduledReminder != null) {
         ReminderDeepTimerScheduler.cancel(super._nextScheduledReminder);
      }
   }

   @Override
   public final boolean scheduleNextReminder(long reminderTime, ReminderRunnable nextScheduledReminder) {
      return ReminderDeepTimerScheduler.schedule(reminderTime, super._nextScheduledReminder);
   }

   @Override
   public final long getAdjustedCurrentTimeMillis() {
      return ReminderDeepTimerScheduler.getAdjustedCurrentTimeMillis();
   }

   @Override
   public final void cancelAllReminders() {
      ReminderDeepTimerScheduler.cancelAll();
   }
}
