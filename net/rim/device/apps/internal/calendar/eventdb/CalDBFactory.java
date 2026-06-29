package net.rim.device.apps.internal.calendar.eventdb;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.internal.calendar.sync.CalendarSyncCollection;

public class CalDBFactory implements Factory {
   @Override
   public Object createInstance(Object initialData) {
      if (!(initialData instanceof CalendarService)) {
         return null;
      }

      CalendarService calendarService = (CalendarService)initialData;
      long calendarServiceID = calendarService.getUniqueServiceID();
      CalDBImpl calDB = new CalDBImpl();
      ApplicationRegistry.getApplicationRegistry().put(calendarServiceID, calDB);
      if (!calDB.init(calendarServiceID)) {
         throw new RuntimeException("Failed to initialize the calendar database");
      }

      ReminderManager rm = ReminderManager.getInstance();
      if (rm == null) {
         throw new RuntimeException("Calendar init error. Couldn't obtain the ReminderManager.");
      }

      calDB.addLowPriorityListener(new CalendarReminderProvider(rm, calDB));
      TimeBasedCollection.getInstance().registerProvider(calDB, true);
      CalendarSyncCollection.register(calendarService);
      return calDB;
   }
}
