package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;

class CICALConverterDescriptor implements ConverterDescriptor {
   private long _transmissionServiceID;
   private static Converter _apptUpdateConverter = new CICALApptUpdateConverter();
   private static Converter _apptDeleteConverter = new CICALApptDeleteConverter();
   private static Converter _slowSyncConverter = CICALSlowSyncConverter.getInstance();
   private static Converter _calendarConfigurationConverter = CICALConfigConverter.getInstance();

   CICALConverterDescriptor(long transmissionServiceID) {
      this._transmissionServiceID = transmissionServiceID;
   }

   @Override
   public boolean canConvert(byte[] inputBytes, Object contextObject) {
      return false;
   }

   @Override
   public boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public Object getContext() {
      return TransmissionServiceManager.get(this._transmissionServiceID).getContext();
   }

   @Override
   public Converter createConverterInstance(String type) {
      if (type.equals("net.rim.RIMCalendarApptUpdate")) {
         return _apptUpdateConverter;
      } else if (type.equals("net.rim.RIMCalendarApptDelete")) {
         return _apptDeleteConverter;
      } else if (type.equals("net.rim.RIMCalendarSlowSync")) {
         return _slowSyncConverter;
      } else {
         return type.equals("net.rim.RIMCalendarConfig") ? _calendarConfigurationConverter : null;
      }
   }
}
