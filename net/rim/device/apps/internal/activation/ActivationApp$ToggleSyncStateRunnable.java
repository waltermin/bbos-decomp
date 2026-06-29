package net.rim.device.apps.internal.activation;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.internal.calendar.ota.CICALConfigConverter;
import net.rim.device.apps.internal.calendar.ota.CICALSlowSyncConverter;

final class ActivationApp$ToggleSyncStateRunnable implements Runnable {
   private boolean _enable = false;
   private long _sid;

   ActivationApp$ToggleSyncStateRunnable(long serviceId, boolean enable) {
      this._enable = enable;
      this._sid = serviceId;
   }

   @Override
   public final void run() {
      SyncManager.getInstance().enableOTASync(this._enable);
      CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
      CICALConfigConverter configConverter = CICALConfigConverter.getInstance();
      ServiceRecord calendarServiceRecord = ServiceBook.getSB().getRecordByCidAndSid("CICAL", this._sid);
      CalendarService calendarService = null;
      if (calendarServiceRecord != null) {
         calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(calendarServiceRecord);
      }

      if (this._enable) {
         if (calendarService != null && CalendarOptions.getOptions().isAllowWirelessSync(calendarService.getUniqueServiceID())) {
            configConverter.sendDeviceConfiguration(calendarService);
            CICALConfigConverter.getInstance().requestConfiguration(calendarService);
            return;
         }
      } else if (calendarService != null) {
         slowSyncConverter.purgeSlowSyncStatistics(calendarService);
         if (CalendarOptions.getOptions().isAllowWirelessSync(calendarService.getUniqueServiceID())) {
            slowSyncConverter.stopCurrentCalendarSlowSync(calendarService);
            calendarService.getCICALConfiguration().setOTACalendarStatus(false);
            configConverter.sendDeviceConfiguration(calendarService);
         }
      }
   }
}
