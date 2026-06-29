package net.rim.device.apps.api.reminders;

final class ReminderManager$ReminderScanner$ReminderScannerNotifier implements Runnable {
   long _localreminderProviderID;
   Reminder _reminder;
   ReminderProvider _reminderProvider;
   int _action;
   private final ReminderManager$ReminderScanner this$1;

   public ReminderManager$ReminderScanner$ReminderScannerNotifier(ReminderManager$ReminderScanner _1, long reminderProviderID, Reminder reminder, int action) {
      this.this$1 = _1;
      this._localreminderProviderID = 0;
      this._reminder = null;
      this._reminderProvider = null;
      this._localreminderProviderID = reminderProviderID;
      this._reminder = reminder;
      this._action = action;
   }

   @Override
   public final void run() {
      synchronized (this.this$1.this$0._reminderProviders) {
         if (this.this$1.this$0._reminderScanningEnabled) {
            boolean scanRequired = false;
            this._reminderProvider = this.this$1.this$0.findActiveReminderProvider(this._localreminderProviderID);
            if (this._reminder != null && this._reminderProvider != null) {
               ReminderModel rm = this._reminder.getReminderData();
               if (this._action != 0
                  && (
                     this._action != 3
                        || this.this$1.this$0._nextScheduledReminder == null
                        || this.this$1.this$0._nextScheduledReminder._reminderID != this._reminder.getReminderID()
                  )) {
                  if (this._action == 1) {
                     if (this._reminder.getReminderState() == 3) {
                        scanRequired = true;
                     } else if (rm != null
                        && rm.hasReminder()
                        && this.this$1.shouldBeNextAlarm(this._reminder, this._localreminderProviderID, this.this$1.this$0.getAdjustedCurrentTimeMillis())) {
                        this.this$1.this$0.setNewAlarm(this._reminder, this._reminderProvider);
                     }
                  }
               } else {
                  scanRequired = true;
               }

               if (scanRequired) {
                  if (this._localreminderProviderID == this.this$1.this$0._nextReminderProviderID) {
                     this.this$1.this$0._nextReminderID = Long.MAX_VALUE;
                     this.this$1.this$0._nextReminderProviderID = Long.MAX_VALUE;
                     this.this$1.this$0._nextReminderTime = Long.MAX_VALUE;
                  }

                  this.this$1._reminderProviderID = this._localreminderProviderID;
                  this.this$1.clearReminder(this.this$1._reminderProviderID);
                  this.this$1.this$0._reminderProviders.notify();
               }
            } else {
               this.this$1.this$0._reminderProviders.notify();
            }
         }
      }
   }
}
