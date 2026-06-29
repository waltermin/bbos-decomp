package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.utility.serialization.SerializationException;

class CICALApptUpdateConverter extends CICALBaseUpdateConverter {
   private static final byte[] UPDATE_FROM_PAGER_HEADER = new byte[]{22, 16};

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object context) throws SerializationException {
      if (!(inputObject instanceof Event)) {
         throw new SerializationException("Unknown object type");
      }

      Event event = (Event)inputObject;
      return this.convert(UPDATE_FROM_PAGER_HEADER, event, context);
   }
}
