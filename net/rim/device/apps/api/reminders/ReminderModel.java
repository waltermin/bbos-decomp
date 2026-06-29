package net.rim.device.apps.api.reminders;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface ReminderModel extends PersistableRIMModel {
   long ID;
   long NO_REMINDER;
   byte RELATIVE;
   byte ABSOLUTE;

   boolean hasReminder();

   byte getType();

   long getTime();

   void setTime(long var1);

   long getReminderFiredFor();

   void setReminderFiredFor(long var1);

   void setState(int var1);

   int getState();
}
