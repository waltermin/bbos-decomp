package net.rim.device.apps.api.reminders;

import java.util.TimeZone;

public interface Reminder {
   int NOT_PAST;
   int PAST_NOT_HANDLED_BY_USER;
   int PAST_AND_HANDLED;
   int PAST_NOT_HANDLED_RETRY;
   int QUEUED;
   int FIRE_IMMEDIATELY;
   int SNOOZED;
   long REMINDER;
   long REMINDERTIME;
   int MINIMUM_DURATION;
   int REMINDER_DESCRIPTION_SIZE_LIMIT;
   int SNOOZE_NONE;
   int SNOOZE_1_MINUTE;
   int SNOOZE_5_MINUTES;
   int SNOOZE_10_MINUTES;
   int SNOOZE_15_MINUTES;
   int SNOOZE_30_MINUTES;

   long getReminderID();

   long getReminderTime(TimeZone var1);

   long getReminderEndTime(TimeZone var1);

   ReminderModel getReminderData();

   boolean updateReminderData(ReminderModel var1);

   int getReminderState();

   String getReminderDescription();
}
