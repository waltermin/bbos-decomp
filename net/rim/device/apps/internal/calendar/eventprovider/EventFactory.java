package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.reminders.ReminderModel;

class EventFactory implements Factory {
   CalendarServiceManager _calendarServiceManager = null;
   CalendarProxy _calProxy = null;

   EventFactory() {
      this._calendarServiceManager = CalendarServiceManager.getInstance();
      this._calProxy = CalendarProxy.getInstance();
   }

   @Override
   public Object createInstance(Object initialData) {
      CalendarService calendarService = CalendarOptions.getOptions().getDefaultServiceForNewEvent();
      EventImpl e = new EventImpl(calendarService.getCalendarKeys()[0]);
      CalDB calDB = calendarService.getCalendarDatabase();
      int storeID = calDB.getStoreIdentifier();
      e.setStoreID(storeID);

      do {
         e.setUID(UIDGenerator.getUID(storeID));
      } while (calDB.get(e.getLUID()) != null);

      e.setTimeZoneID(TimeZone.getDefault().getID());

      for (long l : this._calProxy.getFactoryIDs()) {
         Object object = FactoryUtil.createInstance(l, null);
         e.put(l, object);
      }

      ReminderModel rm = e.getReminderData();
      if (rm != null) {
         rm.setTime(CalendarOptions.getOptions().getReminderMillis());
      }

      return e;
   }
}
