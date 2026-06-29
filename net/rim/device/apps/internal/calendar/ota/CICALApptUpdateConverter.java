package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;

class CICALApptUpdateConverter extends CICALBaseUpdateConverter {
   private static final byte[] UPDATE_FROM_PAGER_HEADER = new byte[]{22, 16};

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object context) {
      if (!(inputObject instanceof Object)) {
         throw new Object("Unknown object type");
      }

      Event event = (Event)inputObject;
      return this.convert(UPDATE_FROM_PAGER_HEADER, event, context);
   }
}
