package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;

public class OTAApptUpdateListener extends OTABaseListener {
   private static OTAApptUpdateListener _instance;

   private OTAApptUpdateListener() {
   }

   public static OTAApptUpdateListener getInstance() {
      if (_instance == null) {
         _instance = new OTAApptUpdateListener();
      }

      return _instance;
   }

   @Override
   public boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      if (!(anObject instanceof Object)) {
         return false;
      }

      Event event = (Event)anObject;
      long luid = event.getLUID();
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      CalDB calendar = calendarService.getCalendarDatabase();
      if (this.checkInSequence(event, calendarService)) {
         event = RecurUtilities.processRecurrenceChanges(calendarService, event);
         CICALEventLogger.logEvent(1380013355, 4, null, luid);
         long relatedUID = event.getRelatedLUID();
         if (relatedUID != 0) {
            Event parentEvent = (Event)calendar.get(relatedUID);
            if (parentEvent == null) {
               CICALEventLogger.logEvent(1380013357, 2, null, luid);
               return false;
            }
         }

         calendar.addWithAction(event, 3);
         RecurUtilities.ensureMarkedAsExclusion(calendar, event);
         calendar.updateIncomingStatistics(1);
         return true;
      } else {
         CICALEventLogger.logEvent(1380013354, 4, null, luid);
         super._otaSyncDataManager.remove(event);
         return true;
      }
   }
}
