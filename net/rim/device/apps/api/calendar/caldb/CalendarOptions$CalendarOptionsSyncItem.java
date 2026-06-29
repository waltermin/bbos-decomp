package net.rim.device.apps.api.calendar.caldb;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.service.ServiceIdentifier;

final class CalendarOptions$CalendarOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH = 16;
   private static final int DATA_LENGTH = 44;
   private static final int DB_VERSION = 0;
   private static final int SCALING_FACTOR = 60000;
   private static final int END_OF_TAGGED_DATA = 0;
   private static final int SERVICE_ID = 1;
   private static final int END_SERVICE_PROPERTIES = 2;
   private static final int WIRELESS_SYNC = 3;
   private static final int COLOUR = 4;
   private static final int DISPLAY_REMINDERS = 5;

   @Override
   public final String getSyncName() {
      return "Calendar Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      CalendarOptions options = CalendarOptions.getOptions();
      short keepAppointmentsDuration = 0;
      Boolean legacyWirelessSyncOption = null;
      boolean foundCalendarProperties = false;

      label146:
      try {
         int length = buffer.readShort();
         buffer.readByte();
         if (length < 16) {
            return false;
         }

         options.setFirstDayOfWeek((buffer.readInt() + 1) % 8);
         options.setDayStartAndEnd(buffer.readShort() * '\uea60', buffer.readShort() * '\uea60');
         buffer.skipBytes(4);
         options.setConfirmDelete(buffer.readBoolean());
         buffer.skipBytes(1);
         options.setReminderMillis(buffer.readShort() > 0 ? 900000 : -1);
         int seconds = buffer.readInt();
         if (seconds >= 0) {
            options.setReminderMillis((long)seconds * 60000);
         } else {
            options.setReminderMillis(-1);
         }

         options.setInitialView(buffer.readByte());
         options.setQuickInputTriggeredByKeystrokes(buffer.readBoolean());
         buffer.readBoolean();
         seconds = buffer.readInt();
         if (seconds > 0) {
            options.setSnoozeMillis((long)seconds * 60000);
         } else {
            options.setSnoozeMillis(-1);
         }

         legacyWirelessSyncOption = buffer.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
         options.setSpellCheckCommit(buffer.readBoolean());
         keepAppointmentsDuration = buffer.readShort();
         options.integrateTasksIntoViews(buffer.readBoolean());
         buffer.readBoolean();
         options.setUseLegacyAgendaView(buffer.readBoolean());
         options.setFreeTimeBlockThreshhold(buffer.readInt());
         options.setShowEndTime(buffer.readBoolean());

         label142:
         while (!buffer.eof()) {
            CalendarKey key;
            Boolean wirelessSync;
            Integer colour;
            Boolean displayReminders;
            switch (ConverterUtilities.getType(buffer, true)) {
               case -1:
                  ConverterUtilities.skipField(buffer);
                  continue;
               case 0:
               default:
                  ConverterUtilities.skipField(buffer);
                  break label142;
               case 1:
                  long[] serviceIdPair = ConverterUtilities.readLongArray(buffer);
                  key = new ServiceIdentifier(serviceIdPair[0], serviceIdPair[1]);
                  wirelessSync = null;
                  colour = null;
                  displayReminders = null;
            }

            while (true) {
               switch (ConverterUtilities.getType(buffer, true)) {
                  case 1:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 2:
                  default:
                     ConverterUtilities.skipField(buffer);
                     foundCalendarProperties = true;
                     if (key.getCalendarFolderID() == 0) {
                        if (wirelessSync != null) {
                           EventLogger.logEvent(-256469206327664059L, 1465078610, 0);
                           options.setAllowWirelessSync(key.getCalendarServiceID(), wirelessSync, true);
                        }
                     } else {
                        if (colour != null) {
                           options.setCalendarColour(key, colour);
                        }

                        if (displayReminders != null) {
                           options.setDisplayReminders(key, displayReminders);
                        }
                     }
                     continue label142;
                  case 3:
                     wirelessSync = ConverterUtilities.readInt(buffer) != 0 ? Boolean.TRUE : Boolean.FALSE;
                     break;
                  case 4:
                     colour = (Integer)(new Object(ConverterUtilities.readInt(buffer)));
                     break;
                  case 5:
                     displayReminders = ConverterUtilities.readInt(buffer) != 0 ? Boolean.TRUE : Boolean.FALSE;
               }
            }
         }
      } finally {
         break label146;
      }

      if (keepAppointmentsDuration == 0) {
         keepAppointmentsDuration = CalendarOptions.KEEP_APPOINTMENTS_DURATION_CHOICES[4];
      }

      options.setKeepAppointmentsDuration(keepAppointmentsDuration);
      if (!foundCalendarProperties && legacyWirelessSyncOption != null) {
         CalendarServiceManager csm = CalendarServiceManager.getInstance();
         CalendarService defaultService = (CalendarService)csm.getDefaultCalendarService();
         if (defaultService != null) {
            EventLogger.logEvent(-256469206327664059L, 1465076818, 0);
            options.setAllowWirelessSync(defaultService.getUniqueServiceID(), legacyWirelessSyncOption, true);
         }
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      CalendarOptions options = CalendarOptions.getOptions();
      Hashtable properties = options.getCalendarPropertyTable();
      Enumeration propertyKeys = properties.keys();
      DataBuffer tempBuffer = (DataBuffer)(new Object(buffer.isBigEndian()));
      tempBuffer.writeInt(options.getFirstDayOfWeekOption() - 1);
      tempBuffer.writeShort(options.getDayStart() / 60000);
      tempBuffer.writeShort(options.getDayEnd() / 60000);
      tempBuffer.writeShort(0);
      tempBuffer.writeShort(0);
      tempBuffer.writeBoolean(options.getConfirmDelete());
      tempBuffer.writeByte(0);
      tempBuffer.writeShort(0);
      long millis = options.getReminderMillis();
      if (millis >= 0) {
         tempBuffer.writeInt((int)(millis / 60000));
      } else {
         tempBuffer.writeInt(-1);
      }

      tempBuffer.writeByte((byte)options.getInitialView());
      tempBuffer.writeBoolean(options.isQuickInputTriggeredByKeystrokes());
      tempBuffer.writeBoolean(false);
      millis = options.getSnoozeMillis();
      if (millis > 0) {
         tempBuffer.writeInt((int)(millis / 60000));
      } else {
         tempBuffer.writeInt(-1);
      }

      CalendarServiceManager csm = CalendarServiceManager.getInstance();
      CalendarService defaultService = (CalendarService)csm.getDefaultCalendarService();
      boolean wirelessSyncAllowed = true;
      if (defaultService != null) {
         wirelessSyncAllowed = options.isAllowWirelessSync(defaultService.getUniqueServiceID());
      }

      tempBuffer.writeBoolean(wirelessSyncAllowed);
      tempBuffer.writeBoolean(options.getSpellCheckCommit());
      tempBuffer.writeShort(options.getKeepAppointmentsDuration());
      tempBuffer.writeBoolean(options.isTasksIntegratedIntoViews());
      tempBuffer.writeBoolean(false);
      tempBuffer.writeBoolean(options.useLegacyAgendaView());
      tempBuffer.writeInt(options.getFreeTimeBlockThreshhold());
      tempBuffer.writeBoolean(options.showEndTime());

      for (; propertyKeys.hasMoreElements(); ConverterUtilities.writeEmptyField(tempBuffer, 2)) {
         CalendarKey key = (ServiceIdentifier)propertyKeys.nextElement();
         CalendarOptions$CalendarPropertyValue value = (CalendarService)properties.get(key);
         long[] serviceIdPair = new long[]{key.getCalendarServiceID(), key.getCalendarFolderID()};
         ConverterUtilities.writeLongArray(tempBuffer, 1, serviceIdPair);
         if (key.getCalendarFolderID() == 0) {
            ConverterUtilities.convertInt(tempBuffer, 3, value._allowWirelessSync ? 1 : 0, 1);
         } else {
            ConverterUtilities.writeInt(tempBuffer, 4, value._calendarColour);
            ConverterUtilities.convertInt(tempBuffer, 5, value._displayReminders ? 1 : 0, 1);
         }
      }

      ConverterUtilities.writeEmptyField(tempBuffer, 0);
      tempBuffer.trim();
      buffer.writeShort(tempBuffer.getLength());
      buffer.writeByte(0);
      buffer.write(tempBuffer.getArray());
      return true;
   }
}
