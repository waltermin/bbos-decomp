package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.utility.serialization.SerializationException;

class CICALMeetingResponseConverter extends CICALBaseConverter {
   private static final byte[] MEETING_RESPONSE_FROM_PAGER_HEADER = new byte[]{18, 16, 1, 1, 1};

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object contextObject) throws SerializationException {
      if (!(inputObject instanceof Event)) {
         throw new SerializationException("Unknown object type");
      }

      Event event = (Event)inputObject;
      DataBuffer dataBuffer = new DataBuffer(true);
      int answer = (Integer)ContextObject.get(contextObject, 7849556394715590464L);
      String comment = (String)ContextObject.get(contextObject, 8925131257384216348L);
      dataBuffer.write(MEETING_RESPONSE_FROM_PAGER_HEADER);
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      this.convertEventHeader(calendarService, event, dataBuffer, contextObject);
      byte var10;
      switch (answer) {
         case 2:
            var10 = 7;
            break;
         case 3:
         default:
            var10 = 8;
            break;
         case 4:
            var10 = 9;
      }

      String emailAddress = null;
      emailAddress = EventUtilities.getEmailAddress(calendarService.getServiceRecord());
      CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
      if (emailAddress != null) {
         this.writeStringField(dataBuffer, var10, emailAddress, cicalConfiguration.getEncodingData());
      }

      if (comment != null) {
         this.writeStringField(dataBuffer, 24, comment, cicalConfiguration.getEncodingData());
      }

      dataBuffer.writeShort(0);
      return dataBuffer.toArray();
   }
}
