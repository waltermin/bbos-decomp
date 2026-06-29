package net.rim.device.apps.internal.task;

import net.rim.device.apps.api.reminders.Reminder;

final class TaskReminderProvider$FireReminderRunnable implements Runnable {
   private Reminder _reminder;
   private long _reminderTime;
   private final TaskReminderProvider this$0;

   public TaskReminderProvider$FireReminderRunnable(TaskReminderProvider _1, Reminder reminder, long reminderTime) {
      this.this$0 = _1;
      this._reminder = reminder;
      this._reminderTime = reminderTime;
   }

   @Override
   public final void run() {
      Reminder reminder = this.this$0.updateReminderState(this._reminder.getReminderID(), 6, this._reminderTime);
      this.this$0._reminderManager.immediateReminder(this.this$0, reminder);
   }
}
