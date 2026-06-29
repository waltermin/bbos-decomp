package net.rim.device.apps.internal.calendar.ota;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Process;

final class OTACalendarApp$PurgeOldAppointmentsRunnable implements Runnable {
   private long _checkTime;
   private final OTACalendarApp this$0;

   OTACalendarApp$PurgeOldAppointmentsRunnable(OTACalendarApp _1, long checkTime) {
      this.this$0 = _1;
      this._checkTime = checkTime;
   }

   private final void purgeOldAppointments(CalendarService calendarService) {
      long keepAppointmentsDuration = CalendarOptions.getOptions().getKeepAppointmentsDuration();
      long threshold = System.currentTimeMillis() - keepAppointmentsDuration * 86400000;
      Calendar cal = Calendar.getInstance();
      ((CalendarExtensions)cal).setTimeLong(threshold);
      cal.set(14, 0);
      cal.set(13, 0);
      cal.set(12, 0);
      cal.set(11, 0);
      threshold = ((CalendarExtensions)cal).getTimeLong();
      long idleTime = Process.ensureMinimumIdleTime(30);
      if (idleTime > 0) {
         EventLogger.logEvent(-256469206327664059L, 1347768903, calendarService.getUniqueServiceID(), 10, 3);
         TimeZone timeZone = TimeZone.getDefault();

         do {
            Event event = this.this$0.getStaleEvent(calendarService.getCalendarDatabase(), timeZone, threshold);
            if (event == null) {
               this.this$0._lastCheckTime = this._checkTime;
               return;
            }

            CICALEventLogger.logEvent(1347372358, 3);
            this.this$0.removeStaleEvent(event);
            idleTime = Process.ensureMinimumIdleTime(idleTime);
         } while (idleTime > 0);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Thread.currentThread().setPriority(4);
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices(true);

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            this.purgeOldAppointments(calendarService);
         }

         var6 = false;
      } finally {
         if (var6) {
            this.this$0._purgingOldAppointments = false;
         }
      }

      this.this$0._purgingOldAppointments = false;
   }
}
