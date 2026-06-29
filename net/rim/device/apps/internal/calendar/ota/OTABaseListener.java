package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;

class OTABaseListener implements TransmissionServiceListener {
   protected OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();

   protected boolean checkInSequence(Event event, CalendarService calendarService) {
      OTASyncData syncData = this._otaSyncDataManager.get(event);
      CalDB calendar = null;
      if (calendarService != null) {
         calendar = calendarService.getCalendarDatabase();
      } else {
         calendar = CalendarServiceManager.getInstance().findCalendarDatabase(event);
      }

      Object tmpObj = calendar.get(event.getLUID());
      if (tmpObj instanceof Event) {
         Event existingEvent = (Event)tmpObj;
         OTASyncData existingSyncData = this._otaSyncDataManager.get(existingEvent);
         if (syncData == null || existingSyncData == null) {
            return true;
         }

         if (existingSyncData.getHostSequence() > syncData.getHostSequence()) {
            return false;
         }

         if (existingSyncData.getDeviceSequence() > syncData.getDeviceSequence() && calendarService != null) {
            CICALConfiguration configuration = calendarService.getCICALConfiguration();
            if (configuration.getConflictResolutionFlag() != 2) {
               return true;
            }

            return false;
         }
      }

      return true;
   }

   @Override
   public void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
   }

   @Override
   public boolean receiveObject(TransmissionService _1, Object _2, Object _3) {
      throw null;
   }
}
