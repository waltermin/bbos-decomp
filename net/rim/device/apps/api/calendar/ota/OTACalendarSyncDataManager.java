package net.rim.device.apps.api.calendar.ota;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.sync.OTASyncDataManager;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;

public class OTACalendarSyncDataManager extends OTASyncDataManager {
   private static final long ID = 7645607223821437186L;
   private static OTACalendarSyncDataManager _manager;

   private OTACalendarSyncDataManager() {
      super(7645607223821437186L);
   }

   public static OTACalendarSyncDataManager getInstance() {
      if (_manager == null) {
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         _manager = (OTACalendarSyncDataManager)appRegistry.getOrWaitFor(7645607223821437186L);
         if (_manager == null) {
            _manager = new OTACalendarSyncDataManager();
            appRegistry.put(7645607223821437186L, _manager);
         }
      }

      return _manager;
   }

   public int getTransmissionStatusForEvent(Event event) {
      int result = -1;
      CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(event);
      long transmissionServiceID = calendarService.getTransmissionServiceID();
      TransmissionService transmissionService = TransmissionServiceManager.get(transmissionServiceID);
      if (transmissionService instanceof AbstractTransmissionService) {
         AbstractTransmissionService ats = (AbstractTransmissionService)transmissionService;
         result = ats.getTransmissionStatusForObject(event);
      }

      return result;
   }
}
