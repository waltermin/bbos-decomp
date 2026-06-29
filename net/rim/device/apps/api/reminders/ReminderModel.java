package net.rim.device.apps.api.reminders;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface ReminderModel extends PersistableRIMModel {
   long ID = 6784165211264038547L;
   long NO_REMINDER = -1L;
   byte RELATIVE = 1;
   byte ABSOLUTE = 2;

   boolean hasReminder();

   byte getType();

   long getTime();

   void setTime(long var1);

   long getReminderFiredFor();

   void setReminderFiredFor(long var1);

   void setState(int var1);

   int getState();
}
