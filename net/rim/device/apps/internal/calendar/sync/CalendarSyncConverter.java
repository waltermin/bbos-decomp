package net.rim.device.apps.internal.calendar.sync;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.Reconcilable;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;

public class CalendarSyncConverter implements SyncConverter {
   private Factory _eventFactory;
   private OTACalendarSyncDataManager _otaSyncDataManager;
   private TimeZone _gmtTimeZone = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private int[] _dateTimeFields1 = new int[7];
   private int[] _dateTimeFields2 = new int[7];
   private static final int TYPE_FIELD_ID;
   private static final int TITLE_FIELD_ID;
   private static final int TEXT_FIELD_ID;
   private static final int LOCATION_FIELD_ID;
   private static final int TRIGGER_FIELD_ID;
   private static final int STARTS_FIELD_ID;
   private static final int ENDS_FIELD_ID;
   private static final int ATTENDEE_FIELD_ID;
   private static final int CALENDAR_UID_ID;
   private static final int RELATED_TO_FIELD_ID;
   private static final int ATTENDEE_DECLINED_FIELD_ID;
   private static final int ATTENDEE_TENTATIVE_FIELD_ID;
   private static final int ATTENDEE_NEEDSACTION_FIELD_ID;
   private static final int ORGANIZER_FIELD_ID;
   private static final int RECURRENCE_UID_ID;
   private static final int NEXT_ALARM_FIELD_ID;
   private static final int SYNC_DATA_FIELD_ID;
   private static final int FREE_BUSY_FIELD_ID;
   private static final int OTA_BITFIELD_ID;
   private static final int ORIGINAL_TZ_FIELD_ID;
   private static final int SENSITIVITY_FIELD_ID;
   private static final int MEETING_CAPABILITIES_FIELD_ID;
   private static final int ICALENDAR_UID;
   private static final int CALENDAR_SERVICE_ID;
   private static final int CALENDAR_FOLDER_ID;
   private static final int ALLDAY_FIELD_ID;
   private static final byte EVENT_RECORD;
   private static final byte RECURRING_RECORD;
   private static final long ID;
   private static CalendarSyncConverter _instance;
   private static Calendar _calendar = Calendar.getInstance();
   private static final int RECURRENCE_DATA_SIZE;
   private static Object _conversionContext = new Object(10);

   private CalendarSyncConverter() {
      this.init();
   }

   public static CalendarSyncConverter getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CalendarSyncConverter)reg.getOrWaitFor(-1330046377160641069L);
            if (_instance == null) {
               _instance = new CalendarSyncConverter();
               reg.put(-1330046377160641069L, _instance);
            }
         }
      }

      return _instance;
   }

   private void init() {
      this._eventFactory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(-1986287563994289176L);
      this._otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
   }

   private void writeStringOptional(DataBuffer buffer, int fieldId, String string) {
      if (string != null && string.length() > 0) {
         ConverterUtilities.writeStringSmart(buffer, (byte)fieldId, string);
      }
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof Object)) {
         return false;
      }

      TimeZone deviceTimeZone = CalendarSyncCollection._deviceTimeZone;
      if (deviceTimeZone == null) {
         deviceTimeZone = TimeZone.getDefault();
         CalendarSyncCollection._deviceTimeZone = deviceTimeZone;
      }

      Event event = (Event)object;
      Recur recurInfo = event.getReadOnlyRecurrence();
      byte recurType = 0;
      if (recurInfo != null) {
         recurType = recurInfo.getRecurType();
      }

      if (recurType == 0) {
         ConverterUtilities.convertInt(buffer, 1, 97, 1);
      } else {
         ConverterUtilities.convertInt(buffer, 1, 42, 1);
      }

      this.writeStringOptional(buffer, 2, event.getSubject());
      this.writeStringOptional(buffer, 4, event.getLocation());
      this.writeStringOptional(buffer, 3, event.getNotes());
      this.writeStringOptional(buffer, 42, event.getICalID());
      ConverterUtilities.writeLong(buffer, 43, event.getCalendarKey().getCalendarServiceID());
      ConverterUtilities.writeLong(buffer, 44, event.getCalendarKey().getCalendarFolderID());
      long startDate = -1;
      long endDate = -1;
      Calendar cal = _calendar;
      if (!event.getAllDayFlag()) {
         startDate = event.getStartDate(deviceTimeZone);
         endDate = startDate + event.getInstanceDuration();
      } else {
         int[] startFields = this._dateTimeFields1;
         int[] endFields = this._dateTimeFields2;
         ConverterUtilities.convertInt(buffer, 255, 1, 1);
         startDate = event.getStartDate(this._gmtTimeZone);
         endDate = startDate + event.getInstanceDuration();
         cal.setTimeZone(this._gmtTimeZone);
         ((CalendarExtensions)cal).setTimeLong(startDate);
         startFields = DateTimeUtilities.getCalendarFields(cal, startFields);
         startFields[5] = startFields[5] = 0;
         ((CalendarExtensions)cal).setTimeLong(endDate);
         endFields = DateTimeUtilities.getCalendarFields(cal, endFields);
         endFields[5] = endFields[5] = 0;
         cal.setTimeZone(deviceTimeZone);
         DateTimeUtilities.setCalendarFields(cal, startFields);
         startDate = ((CalendarExtensions)cal).getTimeLong();
         DateTimeUtilities.setCalendarFields(cal, endFields);
         endDate = ((CalendarExtensions)cal).getTimeLong();
      }

      ConverterUtilities.convertInt(buffer, 6, DateTimeUtilities.convertMillisecondsToEpoch(startDate), 4);
      ConverterUtilities.convertInt(buffer, 7, DateTimeUtilities.convertMillisecondsToEpoch(endDate), 4);
      long reminderDelta = -1;
      ReminderModel reminder = event.getReminderData();
      if (reminder != null) {
         reminderDelta = reminder.getTime();
      }

      if (reminder.hasReminder()) {
         ConverterUtilities.convertInt(buffer, 5, DateTimeUtilities.convertMillisecondsToEpoch(startDate - reminderDelta), 4);
      }

      ConverterUtilities.convertInt(buffer, 18, (int)(event.getRelatedLUID() & 4294967295L), 4);
      long relatedTime = event.getRelatedTime();
      if (relatedTime != 0) {
         ConverterUtilities.convertInt(buffer, 23, DateTimeUtilities.convertMillisecondsToEpoch(relatedTime), 4);
      }

      if (event.isMeeting()) {
         MeetingInfo meetingInfo = event.getMeetingInfo();
         buffer.writeShort(1);
         buffer.writeByte(41);
         buffer.writeByte(meetingInfo.getCapabilities());
         Enumeration attendees = meetingInfo.getAttendees();
         StringBuffer sb = null;

         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            int attendeeType;
            switch (attendee.getType()) {
               case -1:
               case 1:
                  attendeeType = 21;
                  break;
               case 0:
               default:
                  attendeeType = 22;
                  break;
               case 2:
                  attendeeType = 11;
                  break;
               case 3:
                  attendeeType = 19;
                  break;
               case 4:
                  attendeeType = 20;
            }

            Object address = attendee.getAddress();
            if (address instanceof Object) {
               ConversionProvider converter = (ConversionProvider)address;
               String[] names = new Object[]{null, null};
               if (converter.convert(_conversionContext, names) && names[0] != null) {
                  if (names[1] == null) {
                     names[1] = "";
                  }

                  int addressLength = names[0].length();
                  int friendlyLength = names[1].length();
                  if (addressLength > 0 || friendlyLength > 0) {
                     if (sb == null) {
                        sb = (StringBuffer)(new Object(addressLength + friendlyLength + 1));
                     }

                     if (addressLength > 0) {
                        sb.append(names[0]);
                     }

                     sb.append('\u0000');
                     if (friendlyLength > 0) {
                        sb.append(names[1]);
                     }

                     this.writeStringOptional(buffer, attendeeType, sb.toString());
                     sb.delete(0, sb.length());
                  }
               }
            }
         }
      }

      buffer.writeShort(1);
      buffer.writeByte(28);
      buffer.writeByte(event.getFreeBusy());
      buffer.writeShort(1);
      buffer.writeByte(40);
      buffer.writeByte(event.getSensitivity());
      OTASyncData syncData = this._otaSyncDataManager.get(event);
      if (syncData != null) {
         buffer.writeShort(16);
         buffer.writeByte(27);
         buffer.writeInt(12);
         buffer.writeInt(syncData.getDeviceSequence());
         buffer.writeInt(syncData.getHostSequence());
         buffer.writeInt(0);
      }

      if (recurType != 0) {
         TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
         RecurUtil.serializeRecurInfo(startDate, tz, recurInfo, event.getAllDayFlag(), buffer);
         ConverterUtilities.convertInt(buffer, 30, TimeService.getTimeService().getSerialSyncID(event.getTimeZoneID()), 2);
      }

      return true;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      Event event = (Event)this._eventFactory.createInstance(null);
      boolean isFolderIDSet = false;
      boolean isServiceIDSet = false;
      if (UID != 0) {
         event.setUID(UID);
      }

      event.setTimeZoneID(TimeZone.getDefault().getID());

      try {
         byte eventType = 0;
         long startDate = -1;
         long endDate = -1;
         long relatedTime = 0;
         long triggerDate = -1;
         int recurrenceDataOffset = -1;
         int exclusionRecordOffset = -1;
         int inclusionRecordOffset = -1;
         OTASyncData syncData = null;
         int recurFirstDayOfWeek = -1;
         boolean allDayFlag = false;
         long serviceID = -1;
         long folderID = -1;
         TimeZone deviceTimeZone = CalendarSyncCollection._deviceTimeZone;
         if (deviceTimeZone == null) {
            deviceTimeZone = TimeZone.getDefault();
            CalendarSyncCollection._deviceTimeZone = deviceTimeZone;
         }

         while (!data.eof()) {
            int fieldType = ConverterUtilities.getType(data, true);
            switch (fieldType) {
               case 1:
                  data.skipBytes(3);
                  eventType = data.readByte();
                  if (eventType != 97 && eventType != 42) {
                     return null;
                  }
                  break;
               case 2:
                  String stringDatax = ConverterUtilities.readString(data);
                  if (stringDatax != null) {
                     event.setSubject(stringDatax);
                  }
                  break;
               case 3:
                  String var51 = ConverterUtilities.readString(data);
                  if (var51 != null) {
                     event.setNotes(var51);
                  }
                  break;
               case 4:
                  String var50 = ConverterUtilities.readString(data);
                  if (var50 != null) {
                     event.setLocation(var50);
                  }
                  break;
               case 5: {
                  int intData = ConverterUtilities.readInt(data);
                  if (intData != -1) {
                     triggerDate = DateTimeUtilities.convertEpochToMilliseconds(intData);
                  }
                  break;
               }
               case 6:
                  startDate = DateTimeUtilities.convertEpochToMilliseconds(ConverterUtilities.readInt(data));
                  break;
               case 7:
                  endDate = DateTimeUtilities.convertEpochToMilliseconds(ConverterUtilities.readInt(data));
                  break;
               case 11:
               case 19:
               case 20:
               case 21:
               case 22: {
                  String emailAddressString = ConverterUtilities.readString(data);
                  int attendeeType;
                  switch (fieldType) {
                     case 11:
                        attendeeType = 2;
                        break;
                     case 19:
                        attendeeType = 3;
                        break;
                     case 20:
                        attendeeType = 4;
                        break;
                     case 22:
                        attendeeType = 0;
                        break;
                     default:
                        attendeeType = 1;
                  }

                  Attendee attendeeModel = AttendeeFactory.createAttendeeFromRFC822(attendeeType, emailAddressString);
                  MeetingInfo meetingInfo = event.getMeetingInfo();
                  meetingInfo.addAttendee(attendeeModel);
                  break;
               }
               case 12: {
                  short shortData = data.readShort();
                  if (shortData == 18) {
                     recurrenceDataOffset = data.getPosition() + 1;
                  }

                  data.skipBytes(shortData + 1);
                  break;
               }
               case 13:
                  exclusionRecordOffset = data.getPosition();
                  short var66 = data.readShort();
                  data.skipBytes(var66 + 1);
                  break;
               case 15:
                  String var49 = ConverterUtilities.readString(data);
                  break;
               case 18: {
                  int intData = ConverterUtilities.readInt(data);
                  event.setRelatedLUID(UIDGenerator.makeLUID(0, intData));
                  break;
               }
               case 23:
                  relatedTime = DateTimeUtilities.convertEpochToMilliseconds(ConverterUtilities.readInt(data));
                  break;
               case 27:
                  short var65 = data.readShort();
                  if (var65 == 16) {
                     data.skipBytes(5);
                     int deviceSequence = data.readInt();
                     int hostSequence = data.readInt();
                     data.skipBytes(4);
                     syncData = (OTASyncData)(new Object(hostSequence, deviceSequence));
                  } else {
                     data.skipBytes(var65 + 1);
                  }
                  break;
               case 28:
                  short var64 = data.readShort();
                  data.skipBytes(1);
                  event.setFreeBusy(data.readByte());
                  break;
               case 29:
                  short var62 = data.readShort();
                  if (var62 == 2) {
                     data.readByte();
                     var62 = data.readShort();
                     event.put(-2053159172728646859L, new Object(var62));
                  } else {
                     data.skipBytes(var62 + 1);
                  }
                  break;
               case 30:
                  short var60 = data.readShort();
                  data.skipBytes(1);
                  var60 = data.readShort();
                  String var48 = TimeService.getTimeService().getTimeZoneIDFromSerialSyncID(var60);
                  if (var48 != null) {
                     event.setTimeZoneID(var48);
                  } else {
                     event.setTimeZoneID(TimeZone.getDefault().getID());
                     String errorString = ((StringBuffer)(new Object("TZNR - "))).append(var60).toString();
                     EventLogger.logEvent(-256469206327664059L, errorString.getBytes(), 2);
                  }
                  break;
               case 31:
                  short var58 = data.readShort();
                  data.skipBytes(1);
                  var58 = data.readByte();
                  recurFirstDayOfWeek = var58 + 1;
                  break;
               case 32:
                  inclusionRecordOffset = data.getPosition();
                  short var57 = data.readShort();
                  data.skipBytes(var57 + 1);
                  break;
               case 40:
                  short var56 = data.readShort();
                  data.skipBytes(1);
                  event.setSensitivity(data.readByte());
                  break;
               case 41: {
                  short var55 = data.readShort();
                  data.skipBytes(1);
                  MeetingInfo meetingInfo = event.getMeetingInfo();
                  meetingInfo.setCapabilities(data.readByte());
                  break;
               }
               case 42:
                  String stringData = ConverterUtilities.readString(data);
                  if (stringData != null) {
                     event.setICalID(stringData);
                  }
                  break;
               case 43:
                  serviceID = ConverterUtilities.readLong(data);
                  isServiceIDSet = true;
                  break;
               case 44:
                  folderID = ConverterUtilities.readLong(data);
                  isFolderIDSet = true;
                  break;
               case 255:
                  short var54 = data.readShort();
                  if (var54 == 1) {
                     data.readByte();
                     if (data.readByte() == 1) {
                        allDayFlag = true;
                     }
                  } else {
                     data.skipBytes(var54 + 1);
                  }
                  break;
               default: {
                  short shortData = data.readShort();
                  data.skipBytes(shortData + 1);
               }
            }
         }

         if (eventType != 0 && startDate != -1 && endDate != -1) {
            CalendarServiceManager serviceManager = CalendarServiceManager.getInstance();
            if (!isServiceIDSet) {
               serviceID = serviceManager.getDefaultCalendarService().getUniqueServiceID();
            }

            if (!isFolderIDSet) {
               folderID = serviceID;
            }

            CalendarKey calendarKey = (CalendarKey)(new Object(serviceID, folderID));
            event.setCalendarKey(calendarKey);
            long duration = endDate - startDate;
            if (duration < 0) {
               EventLogger.logEvent(-256469206327664059L, "BEND".getBytes(), 2);
               duration = 0;
            }

            event.setInstanceDuration(duration);
            if (triggerDate != -1) {
               triggerDate = startDate - triggerDate;
            }

            ReminderModel reminder = event.getReminderData();
            if (reminder != null) {
               reminder.setTime(triggerDate);
            }

            if (duration > 0) {
               int[] startFields = this._dateTimeFields1;
               int[] endFields = this._dateTimeFields2;
               Calendar cal = _calendar;
               if (allDayFlag) {
                  TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
                  cal.setTimeZone(tz);
               } else {
                  cal.setTimeZone(deviceTimeZone);
               }

               ((CalendarExtensions)cal).setTimeLong(startDate);
               startFields = DateTimeUtilities.getCalendarFields(cal, startFields);
               startFields[5] = startFields[5] = 0;
               ((CalendarExtensions)cal).setTimeLong(endDate);
               endFields = DateTimeUtilities.getCalendarFields(cal, endFields);
               endFields[5] = endFields[5] = 0;
               if (startFields[3] == 0 && endFields[3] == 0 && startFields[4] == 0 && endFields[4] == 0) {
                  event.setAllDayFlag(true);
                  cal.setTimeZone(this._gmtTimeZone);
                  DateTimeUtilities.setCalendarFields(cal, endFields);
                  long newDuration = ((CalendarExtensions)cal).getTimeLong();
                  DateTimeUtilities.setCalendarFields(cal, startFields);
                  newDuration -= ((CalendarExtensions)cal).getTimeLong();
                  event.setInstanceDuration(newDuration);
               }
            }

            if (eventType == 42 && recurrenceDataOffset != -1) {
               Recur recurInfo = event.getRecurrenceCopy();
               RecurUtil.parseRecurInfo(
                  recurInfo,
                  event.getAllDayFlag(),
                  TimeZone.getTimeZone(event.getTimeZoneID()),
                  data,
                  recurrenceDataOffset,
                  exclusionRecordOffset,
                  inclusionRecordOffset
               );
               recurInfo.setFirstDayOfWeek(recurFirstDayOfWeek);
               event.setRecurrence(recurInfo);
            } else {
               eventType = 97;
            }

            event.setStartDate(startDate, deviceTimeZone);
            if (relatedTime != 0) {
               event.setRelatedTime(relatedTime);
            }

            CalendarService service = CalendarServiceManager.getInstance().findCalendarService(event);
            CalDB calDB = service.getCalendarDatabase();
            Event oldEvent = (Event)calDB.get(calDB.getKey(event));
            if (oldEvent != null) {
               ((Reconcilable)event).reconcile(oldEvent, null);
            }

            if (syncData == null) {
               syncData = (OTASyncData)(new Object(0, 1));
            }

            synchronized (calDB.getLockObject()) {
               syncData.updateChecksum(EventUtilities.getHashData(event), false);
            }

            this._otaSyncDataManager.add(event, syncData);
            return (SyncObject)event;
         } else {
            return null;
         }
      } finally {
         ;
      }
   }
}
