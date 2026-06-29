package net.rim.device.apps.api.calendar.controller;

import net.rim.device.apps.api.framework.verb.Verb;

public interface CalendarEventViewer {
   byte NONE = 0;
   byte CHANGED = 1;
   byte DELETED = 2;
   byte RESET = 4;
   int VALIDATE_TIMES = 1;
   int VALIDATE_INVITEES = 2;
   int VALIDATE_REMINDER = 4;
   int VALIDATE_ON_SAVE_ONLY = 8;
   int VALIDATE_ALL = 255;

   void openViewer(String var1, Verb[] var2, int var3, int var4, boolean var5);

   void openViewer(String var1, Verb[] var2, int var3, int var4, int var5, boolean var6);

   void closeViewer(boolean var1);

   boolean isDirty();

   boolean isEventTimeDirty();

   byte getConcurrentModificationFlag();

   boolean validate(int var1);

   void updateEventContents();
}
