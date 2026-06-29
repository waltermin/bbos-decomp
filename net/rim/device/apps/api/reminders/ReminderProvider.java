package net.rim.device.apps.api.reminders;

import java.util.TimeZone;
import net.rim.device.api.system.EncodedImage;

public interface ReminderProvider {
   Reminder getNextReminder(long var1, TimeZone var3);

   Reminder updateReminderState(long var1, int var3, long var4);

   Reminder getReminder(long var1);

   long getReminderProviderID();

   long getProfileID(long var1);

   void reminderQueued(long var1);

   void reminderHandled(long var1);

   long getSnoozeAmount();

   void displayReminderIndicator(boolean var1);

   EncodedImage getReminderIcon();

   String getName();

   void resetUnhandledCount();

   int getUnhandledCount();

   Object[] getUnhandledReminders();
}
