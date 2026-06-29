package net.rim.device.apps.api.calendar.caldb;

import java.util.TimeZone;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALStatistics;
import net.rim.device.apps.api.pim.TimeBasedObjectProvider;

public interface CalDB
   extends CollectionEventSource,
   ReadableSet,
   ReadableLongMap,
   WritableSet,
   NotificationSuspension,
   CICALStatistics,
   TimeBasedObjectProvider,
   PersistentContentListener {
   String CALENDAR_DATABASE_NAME;
   long ORGANIZING_DATA;
   long ID;
   int MORE_EVENTS_AFTER;
   int MORE_EVENTS_BEFORE;
   int ADD_ACTION_CHECKSUM_UPDATE;
   int ADD_ACTION_MARK_CLEAN;
   int ADD_ACTION_NO_NOTIFY;
   int ADD_NORMAL;
   int ADD_UPDATE_CHECKSUM_MARK_CLEAN_NO_NOTIFY;
   int ADD_JUST_MARK_CLEAN;
   int ADD_MARK_CLEAN_NO_NOTIFY;
   int PRIMARY_CALENDAR_ID;

   int getStoreIdentifier();

   Object getLockObject();

   void removePrior(long var1, TimeZone var3);

   void removePrior(long var1, TimeZone var3, int var4);

   boolean isDirty();

   void markClean();

   void addWithAction(Object var1, int var2);

   ReadableList getEventsSortedByStartDate();

   ReadableList getEventsSortedByStartDate(int var1);

   Object getElementWithEarliestEndDate();

   Object getElementWithEarliestEndDate(int var1);

   Object[] getAllEventsInRange(long var1, long var3);

   CICALConfiguration getCICALConfiguration();

   long getCalendarServiceID();

   void addLowPriorityListener(CollectionListener var1);

   int size(int var1);
}
