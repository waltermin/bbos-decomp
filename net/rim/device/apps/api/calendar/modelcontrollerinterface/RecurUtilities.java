package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.internal.commonmodels.pim.RecurImpl;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;

public class RecurUtilities {
   private static Calendar _calendar = Calendar.getInstance();
   private static Calendar _gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));

   private RecurUtilities() {
   }

   public static Event[] locateRelatedEvents(Event event) {
      return locateRelatedEvents((CalDB)null, event);
   }

   public static Event[] locateRelatedEvents(CalDB calDB, Event event) {
      if (!event.isRecurring()) {
         return null;
      }

      if (calDB == null) {
         CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
         calDB = calendarService.getCalendarDatabase();
      }

      Event[] events = new Event[20];
      int count = 0;
      long luidToDelete = event.getLUID();
      synchronized (calDB.getLockObject()) {
         Enumeration enumeration = calDB.getElements();

         while (enumeration.hasMoreElements()) {
            Object o = enumeration.nextElement();
            if (o instanceof Event) {
               Event e = (Event)o;
               if (e.getRelatedLUID() == luidToDelete) {
                  if (events.length == count) {
                     Array.resize(events, count + 20);
                  }

                  events[count++] = e;
               }
            }
         }
      }

      if (count == 0) {
         return null;
      }

      Array.resize(events, count);
      return events;
   }

   public static void scanAndDeleteRelatedEvents(Event event) {
      scanAndDeleteRelatedEvents((CalDB)null, event);
   }

   public static void scanAndDeleteRelatedEvents(CalDB calDB, Event event) {
      if (calDB == null) {
         CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
         calDB = calendarService.getCalendarDatabase();
      }

      CICALConfiguration cicalConfig = calDB.getCICALConfiguration();
      synchronized (calDB.getLockObject()) {
         if (cicalConfig != null) {
            cicalConfig.suspendOutboundOTATraffic(true);
         }

         Event[] events = locateRelatedEvents(calDB, event);
         if (events != null) {
            for (int i = events.length - 1; i >= 0; i--) {
               calDB.remove(events[i]);
            }
         }

         if (cicalConfig != null) {
            cicalConfig.suspendOutboundOTATraffic(false);
         }
      }
   }

   public static void ensureMarkedAsExclusion(CalDB calDB, Event event) {
      ensureMarkedAs(calDB, event, true);
   }

   public static void ensureMarkedAsExclusion(Event event) {
      ensureMarkedAs((CalDB)null, event, true);
   }

   public static void ensureMarkedAsDeleted(CalDB calDB, Event event) {
      ensureMarkedAs(calDB, event, false);
   }

   public static void ensureMarkedAsDeleted(Event event) {
      ensureMarkedAs((CalDB)null, event, false);
   }

   private static long adjustForAllDayEvent(long originalStart, TimeZone tz) {
      synchronized (_calendar) {
         ((CalendarExtensions)_calendar).setTimeLong(originalStart);
         ((CalendarExtensions)_gmtCalendar).setTimeLong(originalStart);
         if (_gmtCalendar.get(10) != 0) {
            _calendar.setTimeZone(tz);
            originalStart = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
         }

         return originalStart;
      }
   }

   private static void ensureMarkedAs(CalDB calDB, Event event, boolean childDate) {
      if (calDB == null) {
         CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
         calDB = calendarService.getCalendarDatabase();
      }

      long relatedLUID = event.getRelatedLUID();
      long originalStart = event.getRelatedTime();
      if (relatedLUID != 0 && originalStart != 0) {
         Event relatedEvent = (Event)calDB.get(relatedLUID);
         if (relatedEvent == null || !relatedEvent.isRecurring()) {
            return;
         }

         Recur recur = relatedEvent.getRecurrenceCopy();
         long[] exclusions = recur.getExclusions(null);
         long[] childDates = recur.getChildDates(null);
         long[] inclusions = recur.getInclusions(null);
         if (relatedEvent.isAllDay()) {
            originalStart = adjustForAllDayEvent(originalStart, TimeZone.getTimeZone(relatedEvent.getTimeZoneID()));
         }

         boolean changed = false;
         if (Arrays.binarySearch(exclusions, originalStart, 0, exclusions.length) < 0) {
            recur.addExclusion(originalStart);
            changed = true;
         }

         boolean foundChildInstance = Arrays.binarySearch(childDates, originalStart, 0, childDates.length) >= 0;
         if (childDate) {
            if (!foundChildInstance) {
               recur.addChildDate(originalStart);
               changed = true;
            }
         } else if (foundChildInstance) {
            recur.removeChildDate(originalStart);
            changed = true;
         }

         if (Arrays.binarySearch(inclusions, originalStart, 0, inclusions.length) >= 0) {
            recur.removeInclusion(originalStart);
            changed = true;
         }

         if (changed) {
            if (ObjectGroup.isInGroup(relatedEvent)) {
               relatedEvent = (Event)ObjectGroup.expandGroup(relatedEvent);
            }

            relatedEvent.setRecurrence(recur);
            int action = 3;
            calDB.addWithAction(relatedEvent, action);
         }
      }
   }

   public static boolean isAnException(Recur recur, long instance) {
      long[] exclusions = recur.getExclusions(null);
      return exclusions != null ? Arrays.binarySearch(exclusions, instance, 0, exclusions.length) < 0 : false;
   }

   public static boolean isAnInclusion(Recur recur, long instance) {
      long[] inclusions = recur.getInclusions(null);
      return inclusions != null ? Arrays.binarySearch(inclusions, instance, 0, inclusions.length) < 0 : false;
   }

   public static boolean isAChild(Recur recur, long instance) {
      long[] childDates = recur.getChildDates(null);
      return childDates != null ? Arrays.binarySearch(childDates, instance, 0, childDates.length) < 0 : false;
   }

   public static Event rebuildChildList(Event event, Event[] extraEvents) {
      return rebuildChildList(null, event, extraEvents);
   }

   public static Event rebuildChildList(CalDB calDB, Event event, Event[] extraEvents) {
      Event result = event;
      if (event.isRecurring()) {
         if (calDB == null) {
            CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
            calDB = calendarService.getCalendarDatabase();
         }

         Event[] events = locateRelatedEvents(calDB, event);
         if (extraEvents != null) {
            for (int i = 0; i < extraEvents.length; i++) {
               Event e = extraEvents[i];
               if (calDB.get(e.getLUID()) == null && e.getRelatedLUID() == event.getLUID()) {
                  if (events == null) {
                     events = new Event[0];
                  }

                  Array.resize(events, events.length + 1);
                  events[events.length - 1] = e;
               }
            }
         }

         if (events != null) {
            if (ObjectGroup.isInGroup(event)) {
               result = (Event)ObjectGroup.expandGroup(event);
            } else {
               result = event;
            }

            Recur recur = result.getRecurrenceCopy();

            for (int i = 0; i < events.length; i++) {
               long originalStart = events[i].getRelatedTime();
               if (events[i].isAllDay()) {
                  originalStart = adjustForAllDayEvent(originalStart, TimeZone.getTimeZone(events[i].getTimeZoneID()));
               }

               recur.addChildDate(originalStart);
            }

            result.setRecurrence(recur);
         }
      }

      return result;
   }

   public static Event rebuildExclusionList(Event event) {
      return rebuildExclusionList(null, event);
   }

   public static Event rebuildExclusionList(CalDB calDB, Event event) {
      Event result = event;
      if (event.isRecurring()) {
         if (calDB == null) {
            CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
            calDB = calendarService.getCalendarDatabase();
         }

         Event[] events = locateRelatedEvents(calDB, event);
         if (events != null) {
            if (ObjectGroup.isInGroup(event)) {
               result = (Event)ObjectGroup.expandGroup(event);
            } else {
               result = event;
            }

            Recur recur = result.getRecurrenceCopy();

            for (int i = 0; i < events.length; i++) {
               long originalStart = events[i].getRelatedTime();
               recur.addExclusion(originalStart);
            }

            result.setRecurrence(recur);
         }
      }

      return result;
   }

   public static Event removeUnreferencedChildren(CalendarService calendarService, Event event, long[] childDates) {
      if (calendarService == null) {
         calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
      }

      CalDB calDB = calendarService.getCalendarDatabase();
      CICALConfiguration cicalConfig = (CICALConfiguration)calendarService.getCICALConfiguration();
      Event result = event;
      if (event.isRecurring() && childDates != null) {
         Event[] events = locateRelatedEvents(calDB, event);
         if (events != null) {
            if (ObjectGroup.isInGroup(event)) {
               result = (Event)ObjectGroup.expandGroup(event);
            }

            Recur recur = event.getRecurrenceCopy();
            long[] exclusions = new long[0];
            recur.getExclusions(exclusions);
            synchronized (calDB.getLockObject()) {
               if (cicalConfig != null) {
                  cicalConfig.suspendOutboundOTATraffic(true);
               }

               for (int i = 0; i < events.length; i++) {
                  long originalStart = events[i].getRelatedTime();
                  if (events[i].isAllDay()) {
                     originalStart = adjustForAllDayEvent(originalStart, TimeZone.getTimeZone(events[i].getTimeZoneID()));
                  }

                  if (Arrays.binarySearch(childDates, originalStart, 0, childDates.length) < 0) {
                     calDB.remove(events[i]);
                  } else if (Arrays.binarySearch(exclusions, originalStart, 0, exclusions.length) < 0) {
                     recur.addExclusion(originalStart);
                  }
               }

               result.setRecurrence(recur);
               if (cicalConfig != null) {
                  cicalConfig.suspendOutboundOTATraffic(false);
               }
            }
         }
      }

      return result;
   }

   private static boolean areAllOccurrencesExclusions(Event recurringEvent, Recur recur, long startingPoint, boolean startingPointIsKnownExclusion, TimeZone tz) {
      long[] exclusions = recur.getExclusions(null);
      if (exclusions != null && exclusions.length != 0) {
         Recur$Handle handle = (Recur$Handle)(new Object());
         return startingPointIsKnownExclusion
            ? !recurringEvent.getHandleAfter(handle, startingPoint) && !recurringEvent.getHandleBefore(handle, startingPoint)
            : !recurringEvent.getHandleAfterTime(handle, startingPoint, tz) && !recurringEvent.getHandleBeforeTime(handle, startingPoint, tz);
      } else {
         return false;
      }
   }

   private static boolean relatedEventsExist(long recurringEventLUID, CalDB cal) {
      Enumeration enumeration = cal.getElements();

      while (enumeration.hasMoreElements()) {
         Object o = enumeration.nextElement();
         if (o instanceof Event && ((Event)o).getRelatedLUID() == recurringEventLUID) {
            return true;
         }
      }

      return false;
   }

   public static boolean deleteRecurringEventIfEmpty(Event recurringEvent, Recur recur, long startingPoint, boolean startingPointIsKnownExclusion, CalDB cal) {
      TimeZone tz = TimeZone.getTimeZone(recurringEvent.getTimeZoneID());
      if (areAllOccurrencesExclusions(recurringEvent, recur, startingPoint, startingPointIsKnownExclusion, tz)
         && !relatedEventsExist(recurringEvent.getLUID(), cal)) {
         cal.remove(recurringEvent);
         return true;
      } else {
         return false;
      }
   }

   public static int[] populateRecurrenceModifiers(long startDate, Recur recurInfo, String timeZoneID, int[] recurModifiers) {
      int dayofmonth = 0;
      int days = 0;
      int weeks = 0;
      int months = 0;
      boolean relative = false;
      synchronized (_calendar) {
         ((CalendarExtensions)_calendar).setTimeLong(startDate);
         _calendar.setTimeZone(TimeZone.getTimeZone(timeZoneID));
         byte recurType = recurInfo.getRecurType();
         switch (recurType) {
            case 0:
               break;
            case 1:
            default:
               byte var19 = true;
               break;
            case 2:
               recurType = 2;
               days = RecurUtil.getBitmapDaysOfWeek(recurInfo);
               break;
            case 3:
            case 4:
               int count = recurInfo.numModifierValues(1);
               if (count == 0) {
                  days = _calendar.get(5);
               } else {
                  if (count != 1) {
                     relative = true;
                  }

                  days = RecurUtil.getBitmapDaysOfWeek(recurInfo);
                  weeks = RecurUtil.getWeekOfMonth(recurInfo);
               }

               if (recurType == 4) {
                  recurType = 4;
                  months = RecurUtil.getMonthOfYear(recurInfo);
                  count = recurInfo.numModifierValues(2);
                  if (relative) {
                     months = (short)(_calendar.get(2) + 1);
                  } else if (count != 1) {
                     months = (short)(_calendar.get(2) + 1);
                     days = (byte)_calendar.get(5);
                  }
               } else {
                  recurType = 3;
                  dayofmonth = RecurUtil.getDayOfMonth(recurInfo);
               }
         }
      }

      if (recurModifiers == null) {
         recurModifiers = new int[4];
      }

      recurModifiers[0] = dayofmonth;
      recurModifiers[1] = days;
      recurModifiers[2] = weeks;
      recurModifiers[3] = months;
      return recurModifiers;
   }

   public static long[] getDeleteDates(Event event) {
      return getDeleteDates(null, event);
   }

   public static long[] getDeleteDates(CalDB calDB, Event event) {
      if (!event.isRecurring()) {
         return null;
      }

      if (calDB == null) {
         CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
         calDB = calendarService.getCalendarDatabase();
      }

      long[] deletedDates = event.getReadOnlyRecurrence().getExclusions(null);
      Event[] relatedEvents = locateRelatedEvents(calDB, event);
      if (relatedEvents != null) {
         for (int i = deletedDates.length - 1; i >= 0; i--) {
            long originalDate = deletedDates[i];
            if (event.getAllDayFlag()) {
               TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
               originalDate = EventUtilities.adjustAllDayDate(originalDate, tz);
            }

            for (int j = relatedEvents.length - 1; j >= 0; j--) {
               if (originalDate == relatedEvents[j].getRelatedTime()) {
                  int count = deletedDates.length - 1;
                  deletedDates[i] = deletedDates[count];
                  Array.resize(deletedDates, count);
                  break;
               }
            }
         }
      }

      return deletedDates;
   }

   public static boolean checkForAlteredRecurrence(CalendarService calendarService, Event newEvent) {
      if (calendarService == null) {
         calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(newEvent);
      }

      CalDB calDB = calendarService.getCalendarDatabase();
      CICALConfiguration cicalConfig = (CICALConfiguration)calendarService.getCICALConfiguration();
      Object obj = calDB.get(newEvent.getLUID());
      if (obj != null) {
         Event oldEvent = (Event)obj;
         if (oldEvent.isRecurring() && newEvent.isRecurring()) {
            RecurImpl oldRecur = (RecurImpl)oldEvent.getReadOnlyRecurrence();
            RecurImpl newRecur = (RecurImpl)newEvent.getReadOnlyRecurrence();
            long oldTime = oldEvent.getStartDate(null);
            long newTime = newEvent.getStartDate(null);
            long oldDuration = oldEvent.getDuration(null);
            long newDuration = newEvent.getDuration(null);
            boolean durationChanged = oldDuration != newDuration;
            boolean timeChanged = oldTime != newTime;
            boolean typeChanged = oldRecur.getRecurType() != newRecur.getRecurType();
            boolean periodChanged = oldRecur.getRecurPeriod() != newRecur.getRecurPeriod();
            boolean finiteChanged = oldRecur.isFinite() != newRecur.isFinite();
            boolean endDateChanged = !DateTimeUtilities.isSameDate(oldRecur.getEndDate(), newRecur.getEndDate());
            boolean modifiersChagned = !oldRecur.compareModifiers(newRecur);
            if (cicalConfig.isInfiniteRecurrenceAllowed()
               && (durationChanged || timeChanged || typeChanged || periodChanged || finiteChanged || endDateChanged || modifiersChagned)) {
               scanAndDeleteRelatedEvents(calDB, newEvent);
               newRecur.removeAllExclusions();
               newEvent.setRecurrence(newRecur);
               StringBuffer logCode = (StringBuffer)(new Object());
               if (durationChanged) {
                  logCode.append("!RDUR:");
                  logCode.append(oldDuration);
                  logCode.append(':');
                  logCode.append(newDuration);
               } else if (timeChanged) {
                  logCode.append("!RTIME:");
                  logCode.append(oldTime);
                  logCode.append(':');
                  logCode.append(newTime);
               } else if (typeChanged) {
                  logCode.append("!RTYPE:");
                  logCode.append(oldRecur.getRecurType());
                  logCode.append(':');
                  logCode.append(newRecur.getRecurType());
               } else if (periodChanged) {
                  logCode.append("!RPER:");
                  logCode.append(oldRecur.getRecurPeriod());
                  logCode.append(':');
                  logCode.append(newRecur.getRecurPeriod());
               } else if (finiteChanged) {
                  logCode.append("!RF:");
                  logCode.append(oldRecur.isFinite());
                  logCode.append(':');
                  logCode.append(newRecur.isFinite());
               } else if (endDateChanged) {
                  logCode.append("!REND:");
                  logCode.append(oldRecur.getEndDate());
                  logCode.append(':');
                  logCode.append(newRecur.getEndDate());
               } else if (modifiersChagned) {
                  logCode.append("!RMODR");
               } else {
                  logCode.append("!R-ERR");
                  logCode.append(oldDuration);
                  logCode.append(':');
                  logCode.append(newDuration);
               }

               EventLogger.logEvent(-256469206327664059L, logCode.toString().getBytes(), 0);
               return true;
            }
         } else if (oldEvent.isRecurring() && !newEvent.isRecurring()) {
            scanAndDeleteRelatedEvents(calDB, oldEvent);
            EventLogger.logEvent(-256469206327664059L, "!RNOT".getBytes(), 0);
            return true;
         }
      }

      return false;
   }

   public static Event processRecurrenceChanges(CalendarService calendarService, Event event) {
      if (calendarService == null) {
         calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
      }

      CalDB calDB = calendarService.getCalendarDatabase();
      CICALConfiguration cicalConfig = (CICALConfiguration)calendarService.getCICALConfiguration();
      if (event.isRecurring()) {
         if (cicalConfig.supportsRecurrenceOptimization()) {
            RecurImpl recurInfo = (RecurImpl)event.getRecurrenceCopy();
            long[] childDates = recurInfo.getChildDates(null);
            if (childDates != null) {
               event = removeUnreferencedChildren(calendarService, event, childDates);
            }

            event = rebuildExclusionList(calDB, event);
         } else {
            Object tmpObj = calDB.get(event.getLUID());
            if (tmpObj != null) {
               scanAndDeleteRelatedEvents(calDB, (Event)tmpObj);
            }
         }
      }

      checkForAlteredRecurrence(calendarService, event);
      return event;
   }
}
