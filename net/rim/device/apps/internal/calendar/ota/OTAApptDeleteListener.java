package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;

public class OTAApptDeleteListener extends OTABaseListener {
   private static OTAApptDeleteListener _instance;

   private OTAApptDeleteListener() {
   }

   public static OTAApptDeleteListener getInstance() {
      if (_instance == null) {
         _instance = new OTAApptDeleteListener();
      }

      return _instance;
   }

   @Override
   public boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      if (!(anObject instanceof Event)) {
         return false;
      }

      Event event = (Event)anObject;
      long luid = event.getLUID();
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      CalDB calendar = calendarService.getCalendarDatabase();
      if (this.checkInSequence(event, calendarService)) {
         Object tmpObj = calendar.get(luid);
         if (tmpObj != null) {
            CICALEventLogger.logEvent(1380009003, 4, null, luid);
            EventUtilities.removeEvent((Event)tmpObj, true);
         } else if (event.getRelatedLUID() != 0) {
            RecurUtilities.ensureMarkedAsDeleted(calendar, event);
         } else {
            CICALEventLogger.logEvent(1380009005, 4, null, luid);
         }

         calendar.updateIncomingStatistics(1);
      } else {
         CICALEventLogger.logEvent(1380009002, 4, null, luid);
      }

      super._otaSyncDataManager.remove(event);
      return true;
   }
}
