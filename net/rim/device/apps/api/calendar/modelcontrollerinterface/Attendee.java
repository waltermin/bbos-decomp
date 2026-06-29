package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.api.util.Persistable;

public interface Attendee extends Persistable {
   int ORGANIZER;
   int INVITED;
   int ACCEPTED;
   int DECLINED;
   int TENTATIVE;

   int getType();

   void setType(int var1);

   Object getAddress();

   void setAddress(Object var1);
}
