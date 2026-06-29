package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;

public class OTASlowSyncListener extends OTABaseListener {
   private static final long ID = -6969422378309320280L;
   private static OTASlowSyncListener _instance;

   private OTASlowSyncListener() {
   }

   public static OTASlowSyncListener getInstance() {
      if (_instance == null) {
         _instance = new OTASlowSyncListener();
      }

      return _instance;
   }

   @Override
   public boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      if (!(anObject instanceof CICALSlowSyncEvent)) {
         return false;
      }

      CICALSlowSyncEvent slowSyncEvent = (CICALSlowSyncEvent)anObject;
      CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      if (slowSyncConverter == null) {
         CICALEventLogger.logEvent(1398359617, 2);
         return false;
      }

      switch (slowSyncEvent._type) {
         case 29:
            CICALEventLogger.logEvent(1381192530, 0);
            slowSyncConverter.startCalendarSlowSync(
               -6969422378309320280L, calendarService, slowSyncEvent.getSyncStartDate(), slowSyncEvent.getSyncEndDate(), (byte)82, false, true
            );
            return true;
         case 31:
            CICALEventLogger.logEvent(1381188946, 0);
            slowSyncConverter.sendRequestedEvents(slowSyncEvent);
            return true;
         case 33:
            slowSyncConverter.forceComplete(slowSyncEvent);
         default:
            return true;
         case 73:
            slowSyncConverter.abortSlowSync(calendarService, slowSyncEvent.getSessionID(), slowSyncEvent.getResult(), true);
            return true;
      }
   }
}
