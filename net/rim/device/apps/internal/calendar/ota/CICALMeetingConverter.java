package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.cldc.io.gme.GMEDatagram;

class CICALMeetingConverter extends BaseConverter {
   private ContextObject _converterContext = new ContextObject();
   private static Converter _meetingRequestConverter = new CICALMeetingRequestConverter();
   private static Converter _meetingResponseConverter = new CICALMeetingResponseConverter();
   private static Converter _meetingCancelConverter = new CICALMeetingCancelConverter();

   @Override
   public boolean canConvert(Object parameters) {
      if (parameters instanceof String) {
         String string = (String)parameters;
         if (StringUtilities.compareToIgnoreCase(string, "application/x-rimdevicecalendar") == 0
            || StringUtilities.compareToIgnoreCase(string, "calendar") == 0) {
            return true;
         }
      }

      if (!(parameters instanceof Parameters)) {
         return false;
      }

      Parameters cmimeParameters = (Parameters)parameters;
      String type = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
      return this.canConvert(type);
   }

   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      Object result = null;
      int type = inputBytes != null && inputBytes.length > 0 ? inputBytes[0] & 0xFF : -1;
      int attachmentType = 0;
      Converter converter = null;
      ServiceRecord cmimeServiceRecord = null;
      ServiceRecord cicalServiceRecord = null;
      CalendarService calendarService = null;
      if (contextObject instanceof ContextObject) {
         cmimeServiceRecord = (ServiceRecord)ContextObject.get(contextObject, -6095803566992128485L);
         calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      } else if (contextObject instanceof Parameters) {
         Parameters transmissionParameters = (Parameters)contextObject;
         DataBuffer buffer = transmissionParameters.getDataBuffer();
         if (buffer instanceof GMEDatagram) {
            GMEDatagram gmeDatagram = (GMEDatagram)buffer;
            cmimeServiceRecord = gmeDatagram.getBoundServiceRecord();
         }
      }

      if (calendarService == null && cmimeServiceRecord != null) {
         cicalServiceRecord = EventUtilities.getCICALServiceFromOtherService(cmimeServiceRecord);
         if (cicalServiceRecord != null) {
            calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(cicalServiceRecord);
         }
      }

      if (calendarService == null) {
         calendarService = CalendarServiceManager.getInstance().getBaseSystemCalendarService();
      }

      ContextObject.put(this._converterContext, 6741741218837016896L, calendarService);
      switch (type) {
         case 17:
            attachmentType = 1;
            converter = _meetingRequestConverter;
            break;
         case 19:
            attachmentType = 2;
            converter = _meetingResponseConverter;
            break;
         case 21:
            converter = _meetingCancelConverter;
            attachmentType = 3;
      }

      Object convertedObject = null;
      Object[] events = null;
      if (converter != null) {
         if (inputBytes != null) {
            convertedObject = converter.convert(inputBytes, this._converterContext);
         }

         if (convertedObject == null) {
            return null;
         }

         if (convertedObject instanceof Event) {
            events = new Object[]{(Event)convertedObject};
         } else {
            events = (Object[])convertedObject;
         }
      }

      if (events != null && events.length > 0) {
         result = new CICALMeetingAttachmentModel(attachmentType, events, inputBytes);
         OTACalendarTransmissionService calendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
            calendarService.getTransmissionServiceID()
         );
         if (calendarTransmissionService != null) {
            calendarTransmissionService.logIncommingMeetingPacket(this, Integer.toString(type), inputBytes);
         }
      }

      return result;
   }
}
