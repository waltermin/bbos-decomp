package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.Factory;

public class CalendarServiceFactory implements Factory {
   CalendarServiceManager _calendarServiceManager;

   @Override
   public Object createInstance(Object initialData) {
      CalendarService calendarService = new CalendarService(initialData);
      if (this._calendarServiceManager == null) {
         this._calendarServiceManager = CalendarServiceManager.getInstance();
      }

      this._calendarServiceManager.notifyCalendarServiceCreated(calendarService);
      return calendarService;
   }
}
