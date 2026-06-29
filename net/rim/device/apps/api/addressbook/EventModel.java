package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface EventModel extends PersistableRIMModel {
   int BIRTHDAY;
   int ANNIVERSARY;
   long INVALID_TIME;

   long getEventDate();

   void setEventDate(long var1);

   int getEventType();

   void setEventType(int var1);

   void updateEventDate(long var1, Object var3);
}
