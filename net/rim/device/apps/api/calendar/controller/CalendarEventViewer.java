package net.rim.device.apps.api.calendar.controller;

import net.rim.device.apps.api.framework.verb.Verb;

public interface CalendarEventViewer {
   byte NONE;
   byte CHANGED;
   byte DELETED;
   byte RESET;
   int VALIDATE_TIMES;
   int VALIDATE_INVITEES;
   int VALIDATE_REMINDER;
   int VALIDATE_ON_SAVE_ONLY;
   int VALIDATE_ALL;

   void openViewer(String var1, Verb[] var2, int var3, int var4, boolean var5);

   void openViewer(String var1, Verb[] var2, int var3, int var4, int var5, boolean var6);

   void closeViewer(boolean var1);

   boolean isDirty();

   boolean isEventTimeDirty();

   byte getConcurrentModificationFlag();

   boolean validate(int var1);

   void updateEventContents();
}
