package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;

public class AttendeeFactory {
   private static ApplicationRegistry _ar = ApplicationRegistry.getApplicationRegistry();
   private static Factory _attendeeFactory = (Factory)_ar.waitFor(5431812121400615279L);

   private AttendeeFactory() {
   }

   public static Attendee createAttendee(int type, Object address) {
      Attendee attendee = (Attendee)_attendeeFactory.createInstance(address);
      attendee.setType(type);
      return attendee;
   }

   public static Attendee createAttendeeFromRFC822(int type, String address, Object context) {
      String[] names = new String[]{null, null};
      int i = address.indexOf(0);
      if (i != -1) {
         names[0] = address.substring(0, i);
         if (i < address.length()) {
            names[1] = address.substring(i + 1);
         }
      } else {
         names[0] = address;
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      contextObject.put(251, names);
      Object emailAddressModel = FactoryUtil.createInstance(-2985347935260258684L, contextObject);
      return createAttendee(type, emailAddressModel);
   }

   public static Attendee createAttendeeFromRFC822(int type, String address) {
      return createAttendeeFromRFC822(type, address, null);
   }
}
