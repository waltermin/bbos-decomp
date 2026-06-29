package net.rim.device.apps.api.reminders;

import net.rim.device.apps.api.framework.profiles.ProfileIDProvider;

public class ReminderElement implements ProfileIDProvider {
   private long _reminderID;
   private ReminderProvider _reminderProvider;

   public ReminderProvider getReminderProvider() {
      return this._reminderProvider;
   }

   public long getReminderID() {
      return this._reminderID;
   }

   @Override
   public long getProfileID() {
      return this._reminderProvider != null ? this._reminderProvider.getProfileID(this._reminderID) : 0;
   }

   public ReminderElement(long reminderID, ReminderProvider reminderProvider) {
      this._reminderID = reminderID;
      this._reminderProvider = reminderProvider;
   }
}
