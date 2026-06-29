package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.utility.serialization.SerializationException;

class CICALApptDeleteConverter extends CICALBaseConverter {
   private static final byte[] DELETE_FROM_PAGER_HEADER = new byte[]{24, 16, 1, 1, 1};

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
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      dataBuffer.write(DELETE_FROM_PAGER_HEADER);
      this.convertEventHeader(calendarService, event, dataBuffer, contextObject);
      dataBuffer.writeShort(0);
      return dataBuffer.toArray();
   }
}
