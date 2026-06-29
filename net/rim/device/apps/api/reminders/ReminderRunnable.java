package net.rim.device.apps.api.reminders;

public final class ReminderRunnable extends Thread {
   long _reminderID = Long.MAX_VALUE;
   long _reminderProviderID = Long.MAX_VALUE;
   long _reminderTime = Long.MAX_VALUE;
   ReminderManager _reminderManager;

   public ReminderRunnable(long reminderID, long reminderProviderID, ReminderManager reminderManager) {
      this._reminderID = reminderID;
      this._reminderProviderID = reminderProviderID;
      this._reminderManager = reminderManager;
   }

   final long getReminderID() {
      return this._reminderID;
   }

   @Override
   public final void run() {
      this._reminderManager.doNotification(this._reminderID, this._reminderProviderID);
      this._reminderManager.notifyReminderProvider(this._reminderProviderID);
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("REMINDER:"))).append(this._reminderTime).toString();
   }
}
