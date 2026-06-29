package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;

class CalendarService$BulkMoveRunnable implements Runnable {
   private CalDB _sourceDB;
   private final CalendarService this$0;

   public CalendarService$BulkMoveRunnable(CalendarService _1, CalDB sourceDB) {
      this.this$0 = _1;
      this._sourceDB = sourceDB;
   }

   @Override
   public void run() {
      synchronized (EventUtilities.getMoveEventsLockObject()) {
         EventLogger.logEvent(-256469206327664059L, 1129141072, this.this$0.getUniqueServiceID(), 10, 0);
         EventUtilities.moveCalendarEvents(
            this.this$0, this._sourceDB, (CalendarService)CalendarService._calendarServiceManager.getBaseSystemCalendarService(), true, true
         );
      }
   }
}
