package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import java.util.TimeZone;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.WritableLongMap;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.pim.TimeBasedObject;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.service.ServiceObject;
import net.rim.device.apps.api.sync.OTASyncIDProvider;

public interface Event
   extends BareEvent,
   EventInstance,
   ReadableLongMap,
   WritableLongMap,
   ReadableSet,
   Persistable,
   OTASyncIDProvider,
   TimeBasedObject,
   ServiceObject,
   Reminder {
   long ID = -1986287563994289176L;
   int LOCAL_STORE = 267390960;
   int REMOTE_STORE = -1;

   CalendarKey getCalendarKey();

   void setCalendarKey(CalendarKey var1);

   void setSubject(String var1);

   String getSubject();

   void setLocation(String var1);

   String getLocation();

   void setUID(int var1);

   int getUID();

   void setStoreID(int var1);

   int getStoreID();

   long getLUID();

   void setAllDayFlag(boolean var1);

   boolean getAllDayFlag();

   void setStartDate(long var1, TimeZone var3);

   long getStartDate(TimeZone var1);

   long getTrueStartDate();

   long getTrueEndDate();

   void setInstanceDuration(long var1);

   long getInstanceDuration();

   boolean getHandleBeforeTime(Recur$Handle var1, long var2, TimeZone var4);

   boolean getHandleAfterTime(Recur$Handle var1, long var2, TimeZone var4);

   boolean getHandleBefore(Recur$Handle var1, long var2);

   boolean getHandleAfter(Recur$Handle var1, long var2);

   long getStartFromHandle(long var1, TimeZone var3);

   long getDurationFromHandle(long var1, TimeZone var3);

   boolean conflictsWithNextInSeries(boolean var1, boolean var2, long var3, long var5, TimeZone var7);

   boolean isRecurring();

   Recur getRecurrenceCopy();

   Recur getReadOnlyRecurrence();

   void setRecurrence(Recur var1);

   void setNotes(String var1);

   String getNotes();

   boolean isMeeting();

   MeetingInfo getMeetingInfo();

   void setFreeBusy(byte var1);

   byte getFreeBusy();

   void setSensitivity(byte var1);

   byte getSensitivity();

   void setRelatedLUID(long var1);

   long getRelatedLUID();

   void setRelatedTime(long var1);

   long getRelatedTime();

   void setTimeZoneID(String var1);

   String getTimeZoneID();

   boolean isAnException(long var1);

   boolean isAnInclusion(long var1);

   void setICalID(String var1);

   String getICalID();

   int getColour();

   boolean isVisible();

   boolean isDisplayReminder();
}
