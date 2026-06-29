package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;

class CalendarService$1 extends Thread {
   private final CalendarService this$0;

   CalendarService$1(CalendarService _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (EventUtilities.getMoveEventsLockObject()) {
         EventLogger.logEvent(-256469206327664059L, 1129141069, this.this$0.getUniqueServiceID(), 10, 0);
         EventUtilities.moveCalendarEvents((CalendarService)CalendarService._calendarServiceManager.getBaseSystemCalendarService(), this.this$0, true, false);
      }
   }
}
