package net.rim.device.apps.internal.calendar.ota;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.sync.OTABitmask;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.Reconcilable;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.commonmodels.pim.RecurImpl;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.vm.Array;

class CICALBaseConverter extends BaseConverter {
   private Factory _eventFactory;
   protected OTACalendarSyncDataManager _otaSyncDataManager;
   private int[] _dateTimeFields1 = new int[7];
   private int[] _dateTimeFields2 = new int[7];
   protected int[] _recurModifiers = new int[4];
   protected Hashtable _childListTable = (Hashtable)(new Object());
   static final long EXCLUSION_LIST_OVERRIDE = -8188970212168295222L;
   static final long RECUR_INSTANCE = -1184541483416107193L;
   protected static TimeZone _gmtTimeZone = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static Calendar _calendar = Calendar.getInstance();
   private static Calendar _gmtCalendar = Calendar.getInstance(_gmtTimeZone);
   private static boolean _blockQuincyForEmptyCICALErrors;

   CICALBaseConverter() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._eventFactory = (Factory)ar.waitFor(-1986287563994289176L);
      this._otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized Object convert(byte[] inputBytes, Object contextObject) {
      DataBuffer data = (DataBuffer)(new Object(inputBytes, 0, inputBytes.length, true));
      Event[] events = new Object[0];
      int count = 0;
      boolean unregisterFlag = false;
      boolean emptyData = false;
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      if (calendarService == null) {
         calendarService = CalendarServiceManager.getInstance().getDefaultCalendarService();
      }

      boolean var27 = false /* VF: Semaphore variable */;

      label384: {
         Object object;
         label385: {
            try {
               try {
                  var27 = true;
                  data.readByte();
                  if (data.readByte() != 16) {
                     throw new Object("Wrong version number");
                  }

                  this._childListTable.clear();

                  while (!data.eof()) {
                     object = this.convert(data, contextObject);
                     if (object == null) {
                        break;
                     }

                     if (!(object instanceof Object)) {
                        break;
                     }

                     Event e = (Event)object;
                     CalendarKey calendarKey = (CalendarKey)(new Object(calendarService.getUniqueServiceID(), calendarService.getPrimaryCalendarFolderID()));
                     e.setCalendarKey(calendarKey);
                     Array.resize(events, count + 1);
                     events[count++] = e;
                  }

                  if (events.length == 0) {
                     _blockQuincyForEmptyCICALErrors = true;
                     emptyData = true;
                     throw new Object("Expecting at least one calendar event");
                  }

                  if (events.length == 1) {
                     object = events[0];
                     var27 = false;
                     break label385;
                  }

                  for (int i = 0; i < events.length; i++) {
                     if (!this._childListTable.containsKey(events[i]) && events[i].isRecurring()) {
                        events[i] = RecurUtilities.rebuildChildList(calendarService.getCalendarDatabase(), events[i], events);
                     }
                  }

                  this._childListTable.clear();
                  object = events;
                  var27 = false;
               } catch (Throwable var38) {
                  unregisterFlag = true;
                  if (!_blockQuincyForEmptyCICALErrors || !emptyData) {
                     QuincyManager.sendJavaLogworthy("CICALBaseConverter");
                  }

                  if (e instanceof Object) {
                     throw (Object)e;
                  }

                  var27 = false;
                  break label384;
               }
            } finally {
               if (var27) {
                  if (unregisterFlag) {
                     synchronized (calendarService.getCalendarDatabase().getLockObject()) {
                        for (int i = events.length - 1; i >= 0; i--) {
                           Event event = events[i];
                           this._otaSyncDataManager.remove(event);
                        }
                     }
                  }
               }
            }

            if (unregisterFlag) {
               synchronized (calendarService.getCalendarDatabase().getLockObject()) {
                  for (int i = events.length - 1; i >= 0; i--) {
                     Event event = events[i];
                     this._otaSyncDataManager.remove(event);
                  }
               }
            }

            return object;
         }

         if (unregisterFlag) {
            synchronized (calendarService.getCalendarDatabase().getLockObject()) {
               for (int i = events.length - 1; i >= 0; i--) {
                  Event event = events[i];
                  this._otaSyncDataManager.remove(event);
               }
            }
         }

         return object;
      }

      if (unregisterFlag) {
         synchronized (calendarService.getCalendarDatabase().getLockObject()) {
            for (int i = events.length - 1; i >= 0; i--) {
               Event event = events[i];
               this._otaSyncDataManager.remove(event);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   Object convert(DataBuffer data, Object contextObject) {
      Event event = null;
      TimeZone deviceTimeZone = TimeZone.getDefault();
      TimeZone serverTimeZone = null;
      long startDate = -1;
      long endDate = -1;
      long relatedTime = -1;
      long triggerMin = -1;
      int sequenceNum = -1;
      int revisionNum = -1;
      long[] exclusions = null;
      long[] inclusions = null;
      long[] childDates = null;
      int recurType = -1;
      int recurDayOfWeek = -1;
      int recurMonth = -1;
      int recurDayOfMonth = -1;
      int recurCount = -1;
      int recurInterval = -1;
      int recurStartOfWeek = -1;
      int recurBySetPosition = -2;
      long recurUntil = -1;
      byte fieldId = -1;
      boolean isEncoded = false;
      int sessionID = -1;
      boolean allDayFlag = false;
      boolean capabilitiesReceived = false;
      boolean organizerReceived = false;
      String iCalendarID = null;
      long folderID = -1;
      boolean isFolderIDSet = false;
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      if (calendarService == null) {
         calendarService = CalendarServiceManager.getInstance().getDefaultCalendarService();
      }

      boolean var69 = false /* VF: Semaphore variable */;

      try {
         var69 = true;
         byte calendarKey = data.readByte();
         if (calendarKey == 0) {
            return null;
         }

         if (calendarKey != 1) {
            throw new Object("Expecting component ID");
         }

         int intData = data.readCompressedInt();
         if (intData != 1) {
            throw new Object("Expecting length of 1");
         }

         if (data.readByte() != 1) {
            throw new Object("Wrong component type");
         }

         event = (Event)this._eventFactory.createInstance(null);
         iCalendarID = null;

         while (true) {
            if (data.eof()) {
               var69 = false;
               break;
            }

            fieldId = data.readByte();
            if (fieldId == 0) {
               var69 = false;
               break;
            }

            if ((fieldId & -128) == -128) {
               switch (fieldId & 127) {
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 13:
                  case 14:
                  case 24:
                     fieldId = (byte)(fieldId & 127);
                     isEncoded = true;
               }
            }

            switch (fieldId) {
               case 2:
               case 3:
               case 5:
               case 11:
               case 12:
               case 15:
               case 20:
               case 21:
               case 25:
               case 26:
               case 32:
               case 65:
                  intData = data.readCompressedInt();
                  if (intData != 4) {
                     throw new Object("Expecting 4");
                  }

                  intData = data.readInt();
                  switch (fieldId) {
                     case 2:
                        event.setUID(intData);
                        continue;
                     case 3:
                        sequenceNum = intData;
                        continue;
                     case 5:
                        event.setRelatedLUID(UIDGenerator.makeLUID(0, intData));
                        continue;
                     case 11:
                        startDate = (long)intData * 1000;
                        continue;
                     case 12:
                        endDate = (long)intData * 1000;
                        continue;
                     case 15:
                        triggerMin = (long)intData * 60000;
                        continue;
                     case 20:
                        recurCount = intData;
                        continue;
                     case 21:
                        recurInterval = intData;
                        continue;
                     case 25:
                        relatedTime = (long)intData * 1000;
                        continue;
                     case 26:
                        revisionNum = intData;
                        continue;
                     case 32:
                        recurUntil = (long)intData * 1000;
                        continue;
                     case 65:
                        sessionID = intData;
                     default:
                        continue;
                  }
               case 4:
               case 13:
               case 14:
               case 24:
                  int length = data.readCompressedInt();
                  boolean var77 = false /* VF: Semaphore variable */;
                  boolean var85 = false /* VF: Semaphore variable */;

                  Object var111;
                  label1704: {
                     label1703: {
                        try {
                           label1701:
                           try {
                              var85 = true;
                              var77 = true;
                              var111 = CMIMEUtilities.getTextObject(data.getArray(), data.getArrayPosition(), length, isEncoded, null);
                              var77 = false;
                              var85 = false;
                              break label1703;
                           } finally {
                              if (var85) {
                                 var111 = "";
                                 var77 = false;
                                 break label1701;
                              }
                           }
                        } finally {
                           if (var77) {
                              data.skipBytes(length);
                           }
                        }

                        data.skipBytes(length);
                        break label1704;
                     }

                     data.skipBytes(length);
                  }

                  isEncoded = false;
                  switch (fieldId) {
                     case 4:
                        iCalendarID = (String)var111;
                        continue;
                     case 13:
                        event.setSubject((String)var111);
                        continue;
                     case 14:
                        event.setLocation((String)var111);
                        continue;
                     case 24:
                        event.setNotes((String)var111);
                     default:
                        continue;
                  }
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
                  int length = data.readCompressedInt();
                  boolean var93 = false /* VF: Semaphore variable */;
                  boolean var101 = false /* VF: Semaphore variable */;

                  Object var110;
                  label1695: {
                     label1694: {
                        try {
                           label1692:
                           try {
                              var101 = true;
                              var93 = true;
                              var110 = CMIMEUtilities.getTextObject(data.getArray(), data.getArrayPosition(), length, isEncoded, null);
                              var93 = false;
                              var101 = false;
                              break label1694;
                           } finally {
                              if (var101) {
                                 var110 = "";
                                 var93 = false;
                                 break label1692;
                              }
                           }
                        } finally {
                           if (var93) {
                              data.skipBytes(length);
                           }
                        }

                        data.skipBytes(length);
                        break label1695;
                     }

                     data.skipBytes(length);
                  }

                  isEncoded = false;
                  int attendeeType;
                  switch (fieldId) {
                     case 5:
                        attendeeType = 1;
                        break;
                     case 6:
                     default:
                        attendeeType = 0;
                        organizerReceived = true;
                        break;
                     case 7:
                        attendeeType = 2;
                        break;
                     case 8:
                        attendeeType = 3;
                        break;
                     case 9:
                        attendeeType = 4;
                  }

                  ContextObject context = (ContextObject)(new Object());
                  context.setPrivateFlag(4567630869418996525L, 0);
                  Attendee attendeeModel = AttendeeFactory.createAttendeeFromRFC822(attendeeType, (String)var110, context);
                  MeetingInfo meetingInfo = event.getMeetingInfo();
                  meetingInfo.addAttendee(attendeeModel);
                  break;
               case 16:
               case 17:
               case 19:
               case 27:
               case 28:
               case 46:
               case 49:
               case 96:
                  intData = data.readCompressedInt();
                  if (intData != 1) {
                     throw new Object("Expecting 1");
                  }

                  int var120 = data.readByte();
                  switch (fieldId) {
                     case 16:
                        recurType = var120;
                        continue;
                     case 17:
                        recurDayOfWeek = var120;
                        continue;
                     case 19:
                        recurDayOfMonth = var120;
                        continue;
                     case 27:
                        if (var120 == 1) {
                           allDayFlag = true;
                        }
                        continue;
                     case 28:
                        switch (var120) {
                           case -1:
                           case 2:
                              var120 = 2;
                              break;
                           case 0:
                           default:
                              var120 = 0;
                              break;
                           case 1:
                              var120 = 1;
                              break;
                           case 3:
                              var120 = 3;
                        }

                        event.setFreeBusy((byte)var120);
                        continue;
                     case 46:
                        recurStartOfWeek = var120;
                        continue;
                     case 49:
                        event.setSensitivity((byte)var120);
                        continue;
                     case 96:
                        MeetingInfo meetingInfo = event.getMeetingInfo();
                        meetingInfo.setCapabilities((byte)var120);
                        capabilitiesReceived = true;
                     default:
                        continue;
                  }
               case 18:
               case 22:
                  intData = data.readCompressedInt();
                  short var118;
                  if (intData == 1) {
                     var118 = data.readByte();
                  } else {
                     if (intData != 2) {
                        throw new Object("Expecting 1 or 2");
                     }

                     var118 = data.readShort();
                  }

                  switch (fieldId) {
                     case 18:
                        recurMonth = var118;
                        continue;
                     case 22:
                        recurBySetPosition = var118;
                     default:
                        continue;
                  }
               case 23:
                  int exclusionCount = data.readCompressedInt() / 4;
                  int exclusionIndex;
                  if (exclusions == null) {
                     exclusionIndex = 0;
                     exclusions = new long[exclusionCount];
                  } else {
                     exclusionIndex = exclusions.length;
                     Array.resize(exclusions, exclusionIndex + exclusionCount);
                  }

                  while (exclusionCount > 0) {
                     intData = data.readInt();
                     exclusions[exclusionIndex++] = (long)intData * 1000;
                     exclusionCount--;
                  }
                  break;
               case 29:
               case 31:
                  intData = data.readCompressedInt();
                  if (intData != 2) {
                     throw new Object("Expecting 2");
                  }

                  int var115 = data.readShort();
                  switch (fieldId) {
                     case 29:
                        event.put(-2053159172728646859L, new Object((short)var115));
                        continue;
                     case 31:
                        String stringData = TimeService.getTimeService().getTimeZoneIDFromSerialSyncID(var115);
                        if (stringData != null) {
                           event.setTimeZoneID(stringData);
                           serverTimeZone = TimeZone.getTimeZone(stringData);
                        } else {
                           CICALEventLogger.logEvent(1415204434, 2, null, var115);
                        }
                     default:
                        continue;
                  }
               case 33:
                  int childDatesCount = data.readCompressedInt() / 4;
                  int childDatesIndex;
                  if (childDates == null) {
                     childDatesIndex = 0;
                     childDates = new long[childDatesCount];
                  } else {
                     childDatesIndex = childDates.length;
                     Array.resize(childDates, childDatesIndex + childDatesCount);
                  }

                  while (childDatesCount > 0) {
                     intData = data.readInt();
                     childDates[childDatesIndex++] = (long)intData * 1000;
                     childDatesCount--;
                  }
                  break;
               case 48:
                  int inclusionCount = data.readCompressedInt() / 4;
                  int inclusionIndex;
                  if (inclusions == null) {
                     inclusionIndex = 0;
                     inclusions = new long[inclusionCount];
                  } else {
                     inclusionIndex = inclusions.length;
                     Array.resize(inclusions, inclusionIndex + inclusionCount);
                  }

                  while (inclusionCount > 0) {
                     intData = data.readInt();
                     inclusions[inclusionIndex++] = (long)intData * 1000;
                     inclusionCount--;
                  }
                  break;
               case 50:
                  long longData = data.readCompressedLong();
                  if (longData != 8) {
                     throw new Object("Expecting 8");
                  }

                  folderID = data.readLong();
                  isFolderIDSet = true;
                  break;
               default:
                  data.skipBytes(data.readCompressedInt());
            }
         }
      } finally {
         if (var69) {
            CICALEventLogger.logEvent(1129727314, 2, null, fieldId);
            throw new Object("Malformed CICAL packet.");
         }
      }

      if (!isFolderIDSet) {
         folderID = calendarService.getUniqueServiceID();
      }

      CalendarKey calendarKey = (CalendarKey)(new Object(calendarService.getUniqueServiceID(), folderID));
      event.setCalendarKey(calendarKey);
      if (!capabilitiesReceived) {
         MeetingInfo meetingInfo = event.getMeetingInfo();
         meetingInfo.setCapabilities((byte)(organizerReceived ? 0 : 1));
      }

      ReminderModel reminder = event.getReminderData();
      if (reminder != null) {
         reminder.setTime(triggerMin);
      }

      long duration = endDate - startDate;
      if (duration < 0) {
         duration = 0;
         CICALEventLogger.logEvent(1129727314, 2, null, 12);
      }

      event.setInstanceDuration(duration);
      if (duration > 0) {
         if (serverTimeZone == null && event.getRelatedLUID() != 0) {
            Event parentEvent = (Event)calendarService.getCalendarDatabase().get(event.getRelatedLUID());
            if (parentEvent != null) {
               serverTimeZone = TimeZone.getTimeZone(parentEvent.getTimeZoneID());
               event.setTimeZoneID(parentEvent.getTimeZoneID());
            }
         }

         synchronized (_calendar) {
            Calendar cal = _calendar;
            if (serverTimeZone != null) {
               cal.setTimeZone(serverTimeZone);
            } else {
               cal.setTimeZone(deviceTimeZone);
            }

            ((CalendarExtensions)cal).setTimeLong(startDate);
            int[] startFields = this._dateTimeFields1;
            int[] endFields = this._dateTimeFields2;
            startFields = DateTimeUtilities.getCalendarFields(cal, startFields);
            startFields[5] = startFields[5] = 0;
            ((CalendarExtensions)cal).setTimeLong(endDate);
            endFields = DateTimeUtilities.getCalendarFields(cal, endFields);
            endFields[5] = endFields[5] = 0;
            cal.setTimeZone(deviceTimeZone);
            if (allDayFlag && serverTimeZone == null) {
               event.setAllDayFlag(true);
               int hour = startFields[3];
               int desiredOffset = deviceTimeZone.getOffset(
                  1, startFields[0], startFields[1], startFields[2], CalendarOptions.getOptions().getFirstDayOfWeek(), 0
               );
               if (hour < 12) {
                  desiredOffset -= hour * 3600000;
               } else if (hour >= 12) {
                  desiredOffset += hour * 3600000;
               }

               TimeService ts = TimeService.getTimeService();
               TimeZone[] tzs = ts.getTimeZones();

               for (int i = 0; i < tzs.length; i++) {
                  int targetOffset = tzs[i].getOffset(1, startFields[0], startFields[1], startFields[2], CalendarOptions.getOptions().getFirstDayOfWeek(), 0);
                  if (desiredOffset == targetOffset) {
                     serverTimeZone = tzs[i];
                     break;
                  }
               }

               if (serverTimeZone != null) {
                  event.setTimeZoneID(serverTimeZone.getID());
               }
            } else if (startFields[3] == 0 && endFields[3] == 0 && startFields[4] == 0 && endFields[4] == 0) {
               if (recurType != -1 && recurUntil != -1) {
                  if (serverTimeZone != null) {
                     cal.setTimeZone(serverTimeZone);
                  }

                  ((CalendarExtensions)cal).setTimeLong(recurUntil);
                  recurUntil = DateTimeUtilities.copyCalendar(cal, _gmtCalendar);
               }

               event.setAllDayFlag(true);
            }

            if (event.getAllDayFlag()) {
               cal.setTimeZone(_gmtTimeZone);
               DateTimeUtilities.setCalendarFields(cal, endFields);
               long newDuration = ((CalendarExtensions)cal).getTimeLong();
               DateTimeUtilities.setCalendarFields(cal, startFields);
               newDuration -= ((CalendarExtensions)cal).getTimeLong();
               event.setInstanceDuration(newDuration);
            }
         }
      }

      if (startDate != -1) {
         if (serverTimeZone != null) {
            event.setStartDate(startDate, serverTimeZone);
         } else {
            event.setStartDate(startDate, deviceTimeZone);
         }
      }

      if (relatedTime != -1) {
         event.setRelatedTime(relatedTime);
      }

      if (recurType != -1) {
         RecurImpl recurInfo = (RecurImpl)event.getRecurrenceCopy();
         if (recurUntil != -1) {
            recurInfo.setEndDate(recurUntil);
         } else if (recurCount != -1) {
         }

         if (recurInterval > 1) {
            recurInfo.setRecurPeriod(recurInterval);
         }

         if (recurStartOfWeek == -1) {
            recurStartOfWeek = CalendarOptions.getOptions().getFirstDayOfWeek();
         } else {
            recurStartOfWeek++;
         }

         recurInfo.setFirstDayOfWeek(recurStartOfWeek);
         switch (recurType) {
            case 0:
               break;
            case 1:
            default:
               recurInfo.setRecurType((byte)1);
               break;
            case 2:
               recurInfo.setRecurType((byte)2);
               if (recurDayOfWeek != -1) {
                  RecurUtil.addDaysModifier(recurInfo, recurDayOfWeek);
               }
               break;
            case 3:
               recurInfo.setRecurType((byte)3);
               if (recurBySetPosition == -1) {
                  recurBySetPosition = 5;
               }

               if (recurBySetPosition != -2 && recurDayOfWeek != -1) {
                  RecurUtil.addDaysModifier(recurInfo, recurDayOfWeek, recurBySetPosition);
               } else if (recurDayOfMonth != -1) {
                  RecurUtil.setDayOfMonth(recurInfo, recurDayOfMonth);
               }
               break;
            case 4:
               recurInfo.setRecurType((byte)4);
               if (recurBySetPosition == -1) {
                  recurBySetPosition = 5;
               }

               if (recurBySetPosition != -2 && recurDayOfWeek != -1) {
                  RecurUtil.addDaysModifier(recurInfo, recurDayOfWeek, recurBySetPosition);
                  if (recurMonth != -1) {
                     RecurUtil.addMonthModifier(recurInfo, recurMonth);
                  }
               }
         }

         if (childDates != null) {
            TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
            _calendar.setTimeZone(tz);

            for (int i = childDates.length - 1; i >= 0; i--) {
               long childDateTime = childDates[i];
               if (childDateTime != 0) {
                  if (event.getAllDayFlag()) {
                     ((CalendarExtensions)_calendar).setTimeLong(childDateTime);
                     childDateTime = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
                     childDates[i] = childDateTime;
                  }

                  recurInfo.addChildDate(childDateTime);
               }
            }
         }

         if (exclusions != null) {
            TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
            _calendar.setTimeZone(tz);

            for (int i = exclusions.length - 1; i >= 0; i--) {
               long excludedTime = exclusions[i];
               if (event.getAllDayFlag()) {
                  ((CalendarExtensions)_calendar).setTimeLong(excludedTime);
                  excludedTime = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
               }

               recurInfo.addExclusion(excludedTime);
            }
         }

         if (inclusions != null) {
            TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
            _calendar.setTimeZone(tz);

            for (int i = inclusions.length - 1; i >= 0; i--) {
               long includedTime = inclusions[i];
               if (event.getAllDayFlag()) {
                  ((CalendarExtensions)_calendar).setTimeLong(includedTime);
                  includedTime = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
               }

               recurInfo.addInclusion(includedTime);
            }
         }

         if (childDates != null) {
            this._childListTable.put(event, childDates);
         }

         if (recurType == 3 && recurInfo != null && recurInfo.numModifierValues(1) == 0 && RecurUtil.getDayOfMonth(recurInfo) == -1) {
            ((CalendarExtensions)_calendar).setTimeLong(event.getStartDate(deviceTimeZone));
            RecurUtil.setDayOfMonth(recurInfo, _calendar.get(5));
         }

         event.setRecurrence(recurInfo);
      }

      Object tmpObj = calendarService.getCalendarDatabase().get(event.getLUID());
      Event existingEvent = null;
      OTASyncData oldSyncData = null;
      if (tmpObj instanceof Object) {
         existingEvent = (Event)tmpObj;
         oldSyncData = this._otaSyncDataManager.get(existingEvent);
      }

      if (sequenceNum != -1 && revisionNum != -1) {
         OTASyncData syncData = (OTASyncData)(new Object(sequenceNum, revisionNum));
         synchronized (calendarService.getCalendarDatabase().getLockObject()) {
            syncData.updateChecksum(EventUtilities.getHashData(event), false);
            if (oldSyncData != null) {
               syncData.setOwnerId(oldSyncData.getOwnerId());
            }

            this._otaSyncDataManager.add(event, syncData);
         }
      }

      if (sessionID != -1) {
         CICALSlowSyncConverter slowSyncAgent = CICALSlowSyncConverter.getInstance();
         if (slowSyncAgent.getCurrentSlowSyncSessionID(calendarService) == sessionID) {
            if (slowSyncAgent.receivedStatistics()) {
               slowSyncAgent.decrementIncomingQueue(calendarService, sessionID);
               if (oldSyncData != null) {
                  this._otaSyncDataManager.remove(existingEvent);
               }
            } else {
               slowSyncAgent.abortSlowSync(calendarService, sessionID, (byte)8, false);
            }
         }

         calendarService.getCalendarDatabase().updateIncomingStatistics(-1);
      }

      event.setICalID(null);
      if (existingEvent != null) {
         ((Reconcilable)event).reconcile(existingEvent, null);
      } else if (iCalendarID != null) {
         event.setICalID(iCalendarID);
      }

      if (event.getICalID() == null) {
         event.setICalID(EventUtilities.generateStringUID());
      }

      return event;
   }

   protected void writeByteArrayField(DataBuffer dataBuffer, int fieldId, byte[] value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(value != null ? value.length : 0);
      if (value != null) {
         dataBuffer.write(value);
      }
   }

   protected void writeLongField(DataBuffer dataBuffer, int fieldId, long value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(8);
      dataBuffer.writeLong(value);
   }

   protected void writeIntegerField(DataBuffer dataBuffer, int fieldId, int value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(4);
      dataBuffer.writeInt(value);
   }

   protected void writeShortField(DataBuffer dataBuffer, int fieldId, short value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(2);
      dataBuffer.writeShort(value);
   }

   protected void writeByteField(DataBuffer dataBuffer, int fieldId, byte value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(1);
      dataBuffer.writeByte(value);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void writeStringField(DataBuffer dataBuffer, int fieldId, String value, byte[] encoding) {
      if (value != null) {
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            String se = CMIMEUtilities.getTextContentType();
            byte encodingByte = -1;
            boolean toBeEncoded = false;
            byte[] tempByteArray = encoding;
            if (tempByteArray != null && !ConverterUtilities.isIntellisyncCompatible(value)) {
               se = CMIMEUtilities.getTextContentType(tempByteArray, false);
               encodingByte = CMIMEUtilities.parseEncoding(se);
               toBeEncoded = encodingByte != -1;
            } else {
               toBeEncoded = false;
            }

            tempByteArray = CMIMEUtilities.getTextByteArray(value, se);
            if (tempByteArray.length > 0) {
               if (toBeEncoded) {
                  switch (fieldId) {
                     case 4:
                     case 6:
                     case 7:
                     case 8:
                     case 9:
                     case 10:
                     case 13:
                     case 14:
                     case 24:
                        fieldId |= -128;
                        break;
                     default:
                        toBeEncoded = false;
                  }
               }

               dataBuffer.writeByte(fieldId);
               if (!toBeEncoded) {
                  dataBuffer.writeCompressedInt(tempByteArray.length);
               } else {
                  dataBuffer.writeCompressedInt(tempByteArray.length + 1);
                  dataBuffer.writeByte(encodingByte);
               }

               dataBuffer.write(tempByteArray, 0, tempByteArray.length);
               return;
            }

            var10 = false;
         } finally {
            if (var10) {
               CICALEventLogger.logEvent(1129727314, 2, value.getBytes(), fieldId);
               return;
            }
         }
      }
   }

   protected void convertEventHeader(CalendarService calendarService, Event event, DataBuffer dataBuffer, Object context) {
      OTASyncData syncData = this._otaSyncDataManager.get(event);
      Integer sessionId = null;
      Long recurInstance = null;
      if (context instanceof Object) {
         ContextObject co = (ContextObject)context;
         Object o = ContextObject.get(co, -2725183197236608288L);
         if (o instanceof Object) {
            sessionId = (Integer)o;
            this.writeIntegerField(dataBuffer, 65, sessionId);
         }

         o = ContextObject.get(co, -1184541483416107193L);
         if (o instanceof Object) {
            recurInstance = (Long)o;
         }
      }

      int uid = event.getUID();
      long relatedLUID = event.getRelatedLUID();
      long relatedTime = event.getRelatedTime();
      if (event.isRecurring() && recurInstance != null) {
         relatedLUID = uid;
         uid = 0;
         relatedTime = recurInstance;
      }

      this.writeIntegerField(dataBuffer, 2, uid);
      if (syncData == null && sessionId != null) {
         syncData = (OTASyncData)(new Object(0, 0));
         EventUtilities.incrementDeviceSequence(syncData, event);
         this._otaSyncDataManager.add(event, syncData);
      }

      int deviceSequence = 0;
      int hostSequence = 0;
      if (syncData != null) {
         deviceSequence = syncData.getDeviceSequence();
         hostSequence = syncData.getHostSequence();
      } else {
         QuincyManager.sendJavaLogworthy("CICALUpdate:NoSyncData");
         CICALEventLogger.logEvent(1314085187, 2, null, uid);
      }

      this.writeIntegerField(dataBuffer, 3, deviceSequence);
      this.writeIntegerField(dataBuffer, 26, hostSequence);
      if (relatedLUID != 0) {
         this.writeIntegerField(dataBuffer, 5, (int)(relatedLUID & 4294967295L));
      }

      if (relatedTime != 0) {
         this.writeIntegerField(dataBuffer, 25, (int)(relatedTime / 1000));
      }

      byte fbtype;
      switch (event.getFreeBusy()) {
         case -1:
         case 2:
            fbtype = 2;
            break;
         case 0:
         default:
            fbtype = 0;
            break;
         case 1:
            fbtype = 1;
            break;
         case 3:
            fbtype = 3;
      }

      this.writeByteField(dataBuffer, 28, fbtype);
      byte[] stringEncoding = calendarService.getCICALConfiguration().getEncodingData();
      String iCalID = event.getICalID();
      if (iCalID != null) {
         this.writeStringField(dataBuffer, 4, iCalID, stringEncoding);
      }
   }

   protected void convertEventBody(CalendarService calendarService, Object inputObject, DataBuffer dataBuffer, Object context) {
      if (!(inputObject instanceof Object)) {
         throw new Object("Unknown object type");
      }

      Event event = (Event)inputObject;
      String timeZoneID = event.getTimeZoneID();
      TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
      Calendar cal = _calendar;
      byte[] stringEncoding = calendarService.getCICALConfiguration().getEncodingData();
      long startDate = EventUtilities.convertEventStartTime(event);
      long endDate = EventUtilities.convertEventEndTime(event);
      this.writeIntegerField(dataBuffer, 11, (int)(startDate / 1000));
      this.writeIntegerField(dataBuffer, 12, (int)(endDate / 1000));
      this.writeStringField(dataBuffer, 13, event.getSubject(), stringEncoding);
      this.writeStringField(dataBuffer, 14, event.getLocation(), stringEncoding);
      String notes = event.getNotes();
      this.writeStringField(dataBuffer, 24, notes, stringEncoding);
      OTABitmask bitmask = (OTABitmask)event.get(-2053159172728646859L);
      if (bitmask != null) {
         this.writeShortField(dataBuffer, 29, bitmask.getValue());
      }

      if (event.getAllDayFlag()) {
         this.writeByteField(dataBuffer, 27, (byte)1);
      }

      long reminder = -1;
      ReminderModel reminderObject = event.getReminderData();
      if (reminderObject != null) {
         reminder = reminderObject.getTime();
         if (reminderObject.hasReminder()) {
            this.writeIntegerField(dataBuffer, 15, (int)(reminder / 60000));
         }
      }

      byte sensitivity = 0;
      switch (event.getSensitivity()) {
         case 0:
            break;
         case 1:
            sensitivity = 1;
            break;
         case 2:
            sensitivity = 2;
            break;
         case 3:
         default:
            sensitivity = 3;
      }

      this.writeByteField(dataBuffer, 49, sensitivity);
      RecurImpl recurInfo = (RecurImpl)event.getReadOnlyRecurrence();
      byte recurType = 0;
      if (recurInfo != null) {
         recurType = recurInfo.getRecurType();
      }

      if (recurType != 0) {
         synchronized (_calendar) {
            ((CalendarExtensions)cal).setTimeLong(startDate);
            cal.setTimeZone(TimeZone.getTimeZone(timeZoneID));
            switch (recurType) {
               case 0:
                  throw new Object("Unrecognized recurrence type");
               case 1:
               default:
                  recurType = 1;
                  break;
               case 2:
                  recurType = 2;
                  break;
               case 3:
                  recurType = 3;
                  break;
               case 4:
                  recurType = 4;
            }
         }

         RecurUtilities.populateRecurrenceModifiers(startDate, recurInfo, timeZoneID, this._recurModifiers);
         int dayofmonth = this._recurModifiers[0];
         int days = this._recurModifiers[1];
         int weeks = this._recurModifiers[2];
         int months = this._recurModifiers[3];
         this.writeByteField(dataBuffer, 16, recurType);
         this.writeIntegerField(dataBuffer, 21, recurInfo.getRecurPeriod());
         this.writeByteField(dataBuffer, 46, (byte)(recurInfo.getFirstDayOfWeek() - 1));
         if (recurInfo.isFinite()) {
            long recurUntil = recurInfo.getEndDate();
            if (event.getAllDayFlag()) {
               _calendar.setTimeZone(timeZone);
               ((CalendarExtensions)_gmtCalendar).setTimeLong(recurUntil);
               recurUntil = DateTimeUtilities.copyCalendar(_gmtCalendar, _calendar);
            }

            this.writeIntegerField(dataBuffer, 32, (int)(recurUntil / 1000));
         }

         if (dayofmonth != 0 && dayofmonth != -1) {
            this.writeByteField(dataBuffer, 19, (byte)days);
         }

         if (days != 0) {
            if (recurType == 4 && weeks == 0) {
               this.writeByteField(dataBuffer, 19, (byte)days);
            } else {
               this.writeByteField(dataBuffer, 17, (byte)days);
            }
         }

         if (months != 0) {
            this.writeByteField(dataBuffer, 18, (byte)months);
         }

         if (weeks != 0) {
            this.writeShortField(dataBuffer, 22, (short)weeks);
         }

         long[] exclusionDays = (long[])ContextObject.get(context, -8188970212168295222L);
         if (exclusionDays == null) {
            exclusionDays = recurInfo.getExclusions(null);
         }

         if (exclusionDays.length != 0) {
            for (int i = 0; i < exclusionDays.length; i++) {
               dataBuffer.writeByte(23);
               dataBuffer.writeCompressedInt(4);
               long exclustionDate = exclusionDays[i];
               if (event.getAllDayFlag()) {
                  exclustionDate = EventUtilities.adjustAllDayDate(exclusionDays[i], timeZone);
               }

               dataBuffer.writeInt((int)(exclustionDate / 1000));
            }
         }

         long[] inclusionDays = recurInfo.getInclusions(null);
         if (inclusionDays.length != 0) {
            for (int i = 0; i < inclusionDays.length; i++) {
               dataBuffer.writeByte(48);
               dataBuffer.writeCompressedInt(4);
               long inclustionDate = inclusionDays[i];
               if (event.getAllDayFlag()) {
                  inclustionDate = EventUtilities.adjustAllDayDate(inclusionDays[i], timeZone);
               }

               dataBuffer.writeInt((int)(inclustionDate / 1000));
            }
         }
      }

      this.writeShortField(dataBuffer, 31, (short)TimeService.getTimeService().getSerialSyncID(timeZoneID));
      if (event.isMeeting()) {
         MeetingInfo meetingInfo = event.getMeetingInfo();
         this.writeByteField(dataBuffer, 96, meetingInfo.getCapabilities());
         Enumeration attendees = meetingInfo.getAttendees();
         StringBuffer sb = null;

         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            int attendeeType = attendee.getType();
            byte var41;
            switch (attendee.getType()) {
               case -1:
               case 1:
                  var41 = 10;
                  break;
               case 0:
               default:
                  var41 = 6;
                  break;
               case 2:
                  var41 = 7;
                  break;
               case 3:
                  var41 = 8;
                  break;
               case 4:
                  var41 = 9;
            }

            String[] names = AttendeeUtilities.convert(attendee);
            if (names != null) {
               int addressLength = names[0] != null ? names[0].length() : 0;
               int friendlyAddressLength = names[1] != null ? names[1].length() : 0;
               if (sb == null) {
                  sb = (StringBuffer)(new Object());
               }

               if (addressLength > 0 || friendlyAddressLength > 0) {
                  if (addressLength > 0) {
                     sb.append(names[0]);
                  }

                  sb.append('\u0000');
                  if (friendlyAddressLength > 0) {
                     sb.append(names[1]);
                  }

                  this.writeStringField(dataBuffer, var41, sb.toString(), stringEncoding);
                  sb.delete(0, sb.length());
               }
            }
         }
      }
   }
}
