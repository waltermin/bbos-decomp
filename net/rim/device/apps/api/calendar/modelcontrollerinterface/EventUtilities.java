package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.vm.Array;

public class EventUtilities {
   private static final long EXTERNAL_ACTION_VERBS = -8609276785593847844L;
   private static AttendeeUtilities$EmailAddressComparator _emailAddressComparator = new AttendeeUtilities$EmailAddressComparator();
   private static OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
   private static DataBuffer _scratchBuffer = (DataBuffer)(new Object(true));
   protected static TimeZone _gmtTimeZone = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static Calendar _calendar = Calendar.getInstance();
   private static Calendar _gmtCalendar = Calendar.getInstance(_gmtTimeZone);
   private static int[] _dateTimeFields1 = new int[7];
   private static Object _moveEventsLockObject;
   private static final long CALENDAR_MOVE_EVENTS_LOCK_OBJECT_LUID = -6052896484446833044L;
   static int[] _recurModifiers = new int[4];
   static LongHashtable _externalActionVerbs = null;

   EventUtilities() {
   }

   public static String generateStringUID() {
      return Long.toString(UIDGenerator.makeLUID(UIDGenerator.getUID(), UIDGenerator.getUID()));
   }

   public static String getFreeTimeDescription(long startTime, long endTime) {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      long duration = endTime - startTime;
      String messageFormatString = rb.getString(643);
      String hourString = " ";
      String minuteString = "";
      int minutes = (int)(duration / 60000);
      int hours = minutes / 60;
      if (hours > 0) {
         minutes %= 60;
         if (hours > 1) {
            hourString = ((StringBuffer)(new Object(" "))).append(hours).append(rb.getString(609)).toString();
         } else {
            hourString = ((StringBuffer)(new Object(" "))).append(hours).append(rb.getString(644)).toString();
         }
      }

      if (minutes > 0) {
         if (minutes > 1) {
            minuteString = ((StringBuffer)(new Object())).append(minutes).append(rb.getString(610)).toString();
         } else {
            minuteString = ((StringBuffer)(new Object())).append(minutes).append(rb.getString(611)).toString();
         }
      }

      return MessageFormat.format(messageFormatString, new Object[]{hourString, minuteString});
   }

   public static boolean doesEventHaveIntTimeOverflow(Event event) {
      long start = convertEventStartTime(event) / 1000;
      long end = convertEventEndTime(event) / 1000;
      long recurEnd = event.isRecurring() ? event.getReadOnlyRecurrence().getEndDate() / 1000 : 0;
      return start > Integer.MAX_VALUE || end > Integer.MAX_VALUE || recurEnd > Integer.MAX_VALUE;
   }

   public static long convertEventStartTime(Event event) {
      return convertEventStartEndTime(event, true);
   }

   public static long convertEventEndTime(Event event) {
      return convertEventStartEndTime(event, false);
   }

   private static long convertEventStartEndTime(Event event, boolean startTime) {
      TimeZone timeZone = TimeZone.getTimeZone(event.getTimeZoneID());
      long endDate;
      long startDate;
      if (event.getAllDayFlag()) {
         startDate = event.getStartDate(_gmtTimeZone);
         endDate = adjustAllDayDate(startDate + event.getInstanceDuration(), timeZone);
         startDate = adjustAllDayDate(event.getStartDate(_gmtTimeZone), timeZone);
      } else {
         startDate = event.getStartDate(timeZone);
         endDate = startDate + event.getInstanceDuration();
      }

      return !startTime ? endDate : startDate;
   }

   public static byte[] getHashData(Event event) {
      return getHashData(event, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static byte[] getHashData(Event event, DataBuffer recordHash) {
      CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
      CalDB calDB = calendarService.getCalendarDatabase();
      CICALConfiguration cicalConfiguration = (CICALConfiguration)calendarService.getCICALConfiguration();
      byte[] encoding = cicalConfiguration.getEncodingData();
      synchronized (calDB.getLockObject()) {
         boolean var35 = false /* VF: Semaphore variable */;

         byte[] var10000;
         try {
            var35 = true;
            DataBuffer tempBuffer = recordHash;
            if (tempBuffer == null) {
               tempBuffer = new Object(true);
            }

            DataBuffer hashBuffer = (DataBuffer)(new Object(true));
            long startDate = convertEventStartTime(event);
            long endDate = convertEventEndTime(event);
            ((DataBuffer)tempBuffer).setPosition(0);
            synchronized (_scratchBuffer) {
               _scratchBuffer.reset();
               _scratchBuffer.writeInt((int)(startDate / 1000));
               _scratchBuffer.trim();
               int hashValue = CRC16.update(0, _scratchBuffer.toArray());
               hashBuffer.writeShort((short)hashValue);
               _scratchBuffer.reset();
               _scratchBuffer.writeInt((int)(endDate / 1000));
               _scratchBuffer.trim();
               hashValue = CRC16.update(0, _scratchBuffer.toArray());
               hashBuffer.writeShort((short)hashValue);
            }

            String subject = event.getSubject();
            int var40 = 0;
            if (subject != null) {
               byte[] bytes = getStringBytes(encoding, subject);
               if (bytes.length > 256) {
                  var40 = CRC16.update(0, getStringBytes(encoding, subject), 0, 256);
               } else {
                  var40 = CRC16.update(0, getStringBytes(encoding, subject));
               }
            }

            hashBuffer.writeShort((short)var40);
            ((DataBuffer)tempBuffer).reset();
            String tempString = event.getLocation();
            if (tempString != null && tempString.length() > 0) {
               byte[] bytes = getStringBytes(encoding, tempString);
               if (bytes.length > 70) {
                  ((DataBuffer)tempBuffer).write(bytes, 0, 70);
               } else {
                  ((DataBuffer)tempBuffer).write(bytes);
               }
            } else {
               ((DataBuffer)tempBuffer).writeByte(0);
            }

            tempString = event.getNotes();
            if (tempString != null && tempString.length() > 0) {
               byte[] bytes = getStringBytes(encoding, tempString);
               if (bytes.length > 1024) {
                  ((DataBuffer)tempBuffer).write(bytes, 0, 1024);
               } else {
                  ((DataBuffer)tempBuffer).write(bytes);
               }
            } else {
               ((DataBuffer)tempBuffer).writeByte(0);
            }

            ((DataBuffer)tempBuffer).writeByte(event.getAllDayFlag() ? 1 : 0);
            ((DataBuffer)tempBuffer).writeByte(event.getFreeBusy());
            long reminder = -1;
            ReminderModel reminderObject = event.getReminderData();
            if (reminderObject != null) {
               reminder = reminderObject.getTime();
            }

            if (reminder != -1) {
               ((DataBuffer)tempBuffer).writeInt((int)(reminder / 60000));
            } else {
               ((DataBuffer)tempBuffer).writeByte(0);
            }

            ((DataBuffer)tempBuffer).writeByte(event.getSensitivity());
            String timeZoneID = event.getTimeZoneID();
            TimeZone tz = TimeZone.getTimeZone(timeZoneID);
            Recur recurInfo = event.getRecurrenceCopy();
            byte recurType = recurInfo.getRecurType();
            if (recurInfo.getRecurType() != 0) {
               ((DataBuffer)tempBuffer).writeByte(recurInfo.getRecurType());
               ((DataBuffer)tempBuffer).writeShort((short)recurInfo.getRecurPeriod());
               if (recurInfo.isFinite()) {
                  long recurUntil = recurInfo.getEndDate();
                  if (event.getAllDayFlag()) {
                     ((CalendarExtensions)_gmtCalendar).setTimeLong(recurUntil);
                     _calendar.setTimeZone(tz);
                     recurUntil = DateTimeUtilities.copyCalendar(_gmtCalendar, _calendar);
                  }

                  ((DataBuffer)tempBuffer).writeInt((int)(recurUntil / 1000));
               } else {
                  ((DataBuffer)tempBuffer).writeInt(0);
               }

               RecurUtilities.populateRecurrenceModifiers(startDate, recurInfo, timeZoneID, _recurModifiers);
               byte dayofmonth = (byte)_recurModifiers[0];
               byte days = (byte)_recurModifiers[1];
               short byPosition = (short)_recurModifiers[2];
               byte months = (byte)_recurModifiers[3];
               if (days != 0) {
                  if (recurType == 4 && byPosition == 0) {
                     dayofmonth = days;
                     days = 0;
                  } else if (recurType == 3 && byPosition == 0) {
                     days = 0;
                  }
               }

               ((DataBuffer)tempBuffer).writeByte(dayofmonth != -1 ? dayofmonth : 0);
               ((DataBuffer)tempBuffer).writeByte(days != -1 ? days : 0);
               ((DataBuffer)tempBuffer).writeByte(months != -1 ? months : 0);
               ((DataBuffer)tempBuffer).writeShort(byPosition != -1 ? byPosition : 0);
               ((DataBuffer)tempBuffer).writeShort((short)TimeService.getTimeService().getSerialSyncID(event.getTimeZoneID()));
               long[] deletedDates = RecurUtilities.getDeleteDates(calDB, event);
               long[] inclusionDates = recurInfo.getInclusions(null);
               if (event.getAllDayFlag()) {
                  if (deletedDates != null) {
                     for (int i = 0; i < deletedDates.length; i++) {
                        deletedDates[i] = adjustAllDayDate(deletedDates[i], tz);
                     }
                  }

                  if (inclusionDates != null) {
                     for (int i = 0; i < inclusionDates.length; i++) {
                        inclusionDates[i] = adjustAllDayDate(inclusionDates[i], tz);
                     }
                  }
               }

               writeLongDates((DataBuffer)tempBuffer, deletedDates);
               writeLongDates((DataBuffer)tempBuffer, inclusionDates);
            }

            if (event.isMeeting()) {
               int count = 0;
               MeetingInfo meetingInfo = event.getMeetingInfo();
               Enumeration enumeration = meetingInfo.getAttendees();
               String[] emailAddresses = new Object[meetingInfo.getAttendeeCount()];

               while (enumeration.hasMoreElements()) {
                  Attendee attendee = (Attendee)enumeration.nextElement();
                  Object o = attendee.getAddress();
                  EmailAddressModel emailAddress = (EmailAddressModel)o;
                  String address = emailAddress.getAddress().toLowerCase();
                  emailAddresses[count] = address;
                  count++;
               }

               Arrays.sort(emailAddresses, _emailAddressComparator);

               for (int i = 0; i < emailAddresses.length; i++) {
                  if (StringUtilities.compareToIgnoreCase(emailAddresses[i], "[Truncated]") != 0) {
                     Attendee attendee = meetingInfo.getAttendee(emailAddresses[i]);
                     if (attendee != null) {
                        writeStringToBuffer(encoding, (DataBuffer)tempBuffer, emailAddresses[i]);
                        int attendeeType = 0;
                        byte var60;
                        switch (attendee.getType()) {
                           case -1:
                           case 1:
                              var60 = 10;
                              break;
                           case 0:
                           default:
                              var60 = 6;
                              break;
                           case 2:
                              var60 = 7;
                              break;
                           case 3:
                              var60 = 8;
                              break;
                           case 4:
                              var60 = 9;
                        }

                        ((DataBuffer)tempBuffer).writeByte(var60);
                     }
                  }
               }
            }

            ((DataBuffer)tempBuffer).trim();
            byte[] tempArray = ((DataBuffer)tempBuffer).toArray();
            var40 = CRC32.update(0, tempArray);
            hashBuffer.writeInt(var40);
            hashBuffer.trim();
            var10000 = hashBuffer.toArray();
            var35 = false;
         } finally {
            if (var35) {
               return null;
            }
         }

         return var10000;
      }
   }

   private static byte[] getStringBytes(byte[] encoding, String str) {
      byte[] tempByteArray = encoding;
      String textType = null;
      if (tempByteArray != null && !ConverterUtilities.isIntellisyncCompatible(str)) {
         textType = "UTF-8\r";
      }

      try {
         return CMIMEUtilities.getTextByteArray(str, textType);
      } finally {
         ;
      }
   }

   public static void writeStringToBuffer(byte[] encoding, DataBuffer buffer, String str) {
      byte[] tempByteArray = getStringBytes(encoding, str);
      if (tempByteArray != null) {
         buffer.write(tempByteArray, 0, tempByteArray.length);
      } else {
         buffer.write(0);
      }
   }

   public static void writeLongDates(DataBuffer buffer, long[] dates) {
      if (dates != null && dates.length != 0) {
         for (int i = 0; i < dates.length; i++) {
            buffer.writeInt((int)(dates[i] / 1000));
         }
      } else {
         buffer.writeByte(0);
      }
   }

   public static long adjustAllDayDate(long dateToAdjust, TimeZone tz) {
      long result = 0;
      synchronized (_calendar) {
         int[] fields = _dateTimeFields1;
         _calendar.setTimeZone(_gmtTimeZone);
         ((CalendarExtensions)_calendar).setTimeLong(dateToAdjust);
         fields = DateTimeUtilities.getCalendarFields(_calendar, fields);
         fields[5] = fields[5] = 0;
         _calendar.setTimeZone(tz);
         DateTimeUtilities.setCalendarFields(_calendar, fields);
         return ((CalendarExtensions)_calendar).getTimeLong();
      }
   }

   public static void removeRelatedEvents(CalDB calDB, Event event, boolean removeSyncData) {
      if (calDB == null) {
         calDB = CalendarServiceManager.getInstance().findCalendarDatabase(event);
      }

      if (calDB != null) {
         synchronized (calDB.getLockObject()) {
            Event[] relatedEvents = RecurUtilities.locateRelatedEvents(calDB, event);
            if (relatedEvents != null) {
               for (int i = relatedEvents.length - 1; i >= 0; i--) {
                  Event relatedEvent = relatedEvents[i];
                  if (removeSyncData) {
                     _otaSyncDataManager.remove(relatedEvent);
                  }

                  calDB.remove(relatedEvent);
               }
            }
         }
      }
   }

   public static void removeEvent(CalDB calDB, Event event, boolean removeSyncData) {
      if (calDB == null) {
         calDB = CalendarServiceManager.getInstance().findCalendarDatabase(event);
      }

      if (calDB != null) {
         synchronized (calDB.getLockObject()) {
            if (removeSyncData) {
               _otaSyncDataManager.remove(event);
            }

            calDB.remove(event);
            if (event.isRecurring()) {
               removeRelatedEvents(calDB, event, true);
            }

            if (event.getRelatedLUID() != 0) {
               RecurUtilities.ensureMarkedAsDeleted(calDB, event);
            }
         }
      }
   }

   public static void removeEvent(Event event, boolean removeSyncData) {
      removeEvent(null, event, removeSyncData);
   }

   public static void incrementDeviceSequence(OTASyncData syncData, Event event) {
      CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(event);
      synchronized (calDB.getLockObject()) {
         syncData.incrementDeviceSequence();
      }

      PersistentObject.commit(syncData);
   }

   public static int getCalendarProcessId() {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_calendar")) {
            return appManager.getProcessId(descriptors[i]);
         }
      }

      return 0;
   }

   public static void registerExternalCalendarActionVerb(long key, Verb verb) {
      synchronized (_externalActionVerbs) {
         _externalActionVerbs.put(key, verb);
      }
   }

   public static void deRegisterExternalCalendarActionVerb(long key) {
      synchronized (_externalActionVerbs) {
         _externalActionVerbs.remove(key);
      }
   }

   public static void getExternalCalendarActionVerbs(Verb[] verbs) {
      synchronized (_externalActionVerbs) {
         if (_externalActionVerbs.size() > 0) {
            Array.resize(verbs, _externalActionVerbs.size());
            Enumeration verbEnum = _externalActionVerbs.elements();

            for (int count = 0; verbEnum.hasMoreElements(); count++) {
               Verb v = (Verb)verbEnum.nextElement();
               verbs[count] = v;
            }
         }
      }
   }

   public static Event createEventInstanceFromRecurrence(Event originalEvent, long instanceTime) {
      Event eventToReturn = null;
      if (originalEvent instanceof Object) {
         if (ObjectGroup.isInGroup(originalEvent)) {
            eventToReturn = (Event)((Copyable)ObjectGroup.expandGroup(originalEvent)).copy();
         } else {
            eventToReturn = (Event)((Copyable)originalEvent).copy();
         }
      } else {
         eventToReturn = (Event)FactoryUtil.createInstance(-1986287563994289176L, null);
      }

      long relatedTime = instanceTime;
      TimeZone tz = TimeZone.getTimeZone(originalEvent.getTimeZoneID());
      if (originalEvent.getAllDayFlag()) {
         TimeZone gmtTZ = TimeZone.getTimeZone(DateTimeUtilities.GMT);
         Calendar cal = Calendar.getInstance(gmtTZ);
         ((CalendarExtensions)cal).setTimeLong(relatedTime);
         int[] fields = DateTimeUtilities.getCalendarFields(cal, null);
         cal.setTimeZone(tz);
         DateTimeUtilities.setCalendarFields(cal, fields);
         relatedTime = ((CalendarExtensions)cal).getTimeLong();
         tz = gmtTZ;
      }

      eventToReturn.setStartDate(instanceTime, tz);
      eventToReturn.setRelatedLUID(originalEvent.getLUID());
      eventToReturn.setRelatedTime(relatedTime);
      eventToReturn.setRecurrence(null);
      return eventToReturn;
   }

   public static ServiceRecord getCICALServiceFromOtherService(ServiceRecord sr) {
      return ServiceBook.getSB().getCIDAssociatedWithService("CICAL", sr);
   }

   public static String getEmailAddress(ServiceRecord sr) {
      return sr != null ? getEmailAddress(sr.getUserId(), sr.getDataSourceId(), sr.getUid()) : null;
   }

   public static String getEmailAddress(int userID, String dsID, String uid) {
      String emailAddress = null;
      ServiceRecord emailServiceRecord = ServiceBook.getSB().getCIDAssociatedWithService("CMIME", userID, dsID, uid);
      if (emailServiceRecord != null) {
         emailAddress = CMIMEUtilities.getEmailAddress(emailServiceRecord);
      }

      return emailAddress;
   }

   public static long makeLUID(int uid) {
      long LUID = 0;
      return uid > 0 ? UIDGenerator.makeLUID(267390960, uid) : UIDGenerator.makeLUID(-1, uid);
   }

   public static boolean moveEvent(Event event, CalendarService sourceCalendar, CalendarService destinationService, boolean forceMove, boolean maintainKey) {
      CalDB sourceDB = sourceCalendar == null ? null : sourceCalendar.getCalendarDatabase();
      return moveEvent(event, sourceCalendar, sourceDB, destinationService, forceMove, maintainKey);
   }

   public static boolean moveEvent(
      Event event, CalendarService sourceCalendar, CalDB sourceDB, CalendarService destinationService, boolean forceMove, boolean maintainKey
   ) {
      long baseSystemServiceID = ((ServiceIdentifier)CalendarServiceManager.getInstance().getBaseSystemCalendarService()).getUniqueServiceID();
      long eventServiceID = event.getCalendarKey().getCalendarServiceID();
      long destinationServiceID = destinationService.getUniqueServiceID();
      if (!forceMove) {
         if (sourceCalendar != null && sourceCalendar.isSecureService()) {
            return false;
         }

         if (eventServiceID != baseSystemServiceID && !net.rim.device.api.servicebook.ServiceIdentifier.isSameService(eventServiceID, destinationServiceID)) {
            return false;
         }
      }

      CalendarKey destinationCalendarKey = new ServiceIdentifier(destinationServiceID, destinationService.getPrimaryCalendarFolderID());
      if (sourceDB == null) {
         removeEvent(event, true);
      } else {
         removeEvent(sourceDB, event, true);
      }

      if (!maintainKey && eventServiceID != destinationServiceID) {
         boolean wasGrouped = false;
         if (ObjectGroup.isInGroup(event)) {
            wasGrouped = true;
            event = (Event)ObjectGroup.expandGroup(event);
         }

         event.setCalendarKey(destinationCalendarKey);
         if (wasGrouped) {
            ObjectGroup.createGroup(event);
         }
      }

      destinationService.getCalendarDatabase().addWithAction(event, 6);
      return true;
   }

   public static Object getMoveEventsLockObject() {
      return _moveEventsLockObject;
   }

   public static void moveCalendarEvents(CalendarService sourceCalendar, CalendarService destinationCalendar) {
      moveCalendarEvents(sourceCalendar, destinationCalendar, false, false);
   }

   public static void moveCalendarEvents(CalendarService sourceCalendar, CalendarService destinationCalendar, boolean forceMove, boolean maintainKey) {
      moveCalendarEvents(sourceCalendar, sourceCalendar.getCalendarDatabase(), destinationCalendar, forceMove, maintainKey);
   }

   public static void moveCalendarEvents(
      CalendarService sourceCalendar, CalDB sourceDB, CalendarService destinationCalendar, boolean forceMove, boolean maintainKey
   ) {
      synchronized (_moveEventsLockObject) {
         if (sourceCalendar != null) {
            EventLogger.logEvent(-256469206327664059L, 1129137491, sourceCalendar.getUniqueServiceID(), 10, 0);
         }

         EventLogger.logEvent(-256469206327664059L, 1129137476, destinationCalendar.getUniqueServiceID(), 10, 0);
         Object[] elements = null;
         synchronized (sourceDB.getLockObject()) {
            int size = sourceDB.size();
            if (size > 0) {
               elements = new Object[size];
               sourceDB.getElements(elements);
            }
         }

         if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
               Event event = (Event)elements[i];
               moveEvent(event, sourceCalendar, sourceDB, destinationCalendar, forceMove, maintainKey);
            }
         }

         EventLogger.logEvent(-256469206327664059L, 1129137230, destinationCalendar.getUniqueServiceID(), 10, 0);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Object o = ar.get(-8609276785593847844L);
         if (o == null) {
            _externalActionVerbs = (LongHashtable)(new Object());
            ar.put(-8609276785593847844L, _externalActionVerbs);
         } else {
            _externalActionVerbs = (LongHashtable)o;
         }
      }

      _moveEventsLockObject = ar.getOrWaitFor(-6052896484446833044L);
      if (_moveEventsLockObject == null) {
         _moveEventsLockObject = new Object();
         ar.put(-6052896484446833044L, _moveEventsLockObject);
      }
   }
}
