package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;

class CICALMeetingCancelConverter extends CICALBaseConverter {
   private static final byte[] MEETING_CANCEL_FROM_PAGER_HEADER = new byte[]{20, 16, 1, 1, 1};

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object contextObject) {
      if (!(inputObject instanceof Object)) {
         throw new Object("Unknown object type");
      }

      Event event = (Event)inputObject;
      DataBuffer dataBuffer = (DataBuffer)(new Object(true));
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      dataBuffer.write(MEETING_CANCEL_FROM_PAGER_HEADER);
      this.convertEventHeader(calendarService, event, dataBuffer, contextObject);
      this.convertEventDates(calendarService, event, dataBuffer, contextObject);
      dataBuffer.writeShort(0);
      return dataBuffer.toArray();
   }

   private void convertEventDates(CalendarService calendarService, Object inputObject, DataBuffer dataBuffer, Object context) {
      if (!(inputObject instanceof Object)) {
         throw new Object("Unknown object type");
      }

      Event event = (Event)inputObject;
      long startDate = EventUtilities.convertEventStartTime(event);
      long endDate = EventUtilities.convertEventEndTime(event);
      this.writeIntegerField(dataBuffer, 11, (int)(startDate / 1000));
      this.writeIntegerField(dataBuffer, 12, (int)(endDate / 1000));
   }
}
