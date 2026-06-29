package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;

class CalendarHandlerFactory$1 implements Runnable {
   private final CalendarHandlerFactory this$0;

   CalendarHandlerFactory$1(CalendarHandlerFactory _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Application.getApplication().addRealtimeClockListener(this.this$0._rtClockHelper);
      CalDB calendardb = CalendarProxy.getInstance().getCalendarDatabase();
      calendardb.addCollectionListener(this.this$0._helper);
      synchronized (this.this$0._helper) {
         this.this$0._initialized = true;
         if (this.this$0._handlerForUpdate != null) {
            this.this$0._handlerForUpdate.update(false);
            this.this$0._handlerForUpdate = null;
         }
      }
   }
}
