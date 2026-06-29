package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.api.util.Persistable;

public interface Attendee extends Persistable {
   int ORGANIZER = 0;
   int INVITED = 1;
   int ACCEPTED = 2;
   int DECLINED = 3;
   int TENTATIVE = 4;

   int getType();

   void setType(int var1);

   Object getAddress();

   void setAddress(Object var1);
}
