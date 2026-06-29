package net.rim.device.apps.api.reminders;

import java.util.TimeZone;

public interface Reminder {
   int NOT_PAST = 1;
   int PAST_NOT_HANDLED_BY_USER = 2;
   int PAST_AND_HANDLED = 3;
   int PAST_NOT_HANDLED_RETRY = 4;
   int QUEUED = 5;
   int FIRE_IMMEDIATELY = 6;
   int SNOOZED = 7;
   long REMINDER = 8440536289882481320L;
   long REMINDERTIME = 2006691216517637157L;
   int MINIMUM_DURATION = 60000;
   int REMINDER_DESCRIPTION_SIZE_LIMIT = 150;
   int SNOOZE_NONE = 0;
   int SNOOZE_1_MINUTE = 1;
   int SNOOZE_5_MINUTES = 2;
   int SNOOZE_10_MINUTES = 3;
   int SNOOZE_15_MINUTES = 4;
   int SNOOZE_30_MINUTES = 5;

   long getReminderID();

   long getReminderTime(TimeZone var1);

   long getReminderEndTime(TimeZone var1);

   ReminderModel getReminderData();

   boolean updateReminderData(ReminderModel var1);

   int getReminderState();

   String getReminderDescription();
}
