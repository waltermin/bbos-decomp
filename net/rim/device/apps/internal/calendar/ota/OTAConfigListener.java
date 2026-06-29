package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;

public class OTAConfigListener extends OTABaseListener {
   byte[] _emptySettings = new byte[]{0, 0};
   private static OTAConfigListener _instance;
   private static final long ID;

   private OTAConfigListener() {
   }

   public static OTAConfigListener getInstance() {
      if (_instance == null) {
         _instance = new OTAConfigListener();
      }

      return _instance;
   }

   @Override
   public boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      if (!(anObject instanceof CICALConfigConverter$OTAConfigEvent)) {
         return false;
      }

      CICALConfigConverter$OTAConfigEvent configEvent = (CICALConfigConverter$OTAConfigEvent)anObject;
      CICALConfigConverter configConverter = CICALConfigConverter.getInstance();
      CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      CICALConfiguration cicalConfiguraton = calendarService.getCICALConfiguration();
      byte result = configEvent.getResult();
      Object ticket = PersistentContent.getTicket();
      switch (configEvent.getCommand()) {
         case 25:
            break;
         case 26:
            CICALEventLogger.logEvent(1380139858, 0);
            break;
         case 27:
         default:
            CICALEventLogger.logEvent(1380139861, 0);
            if (ticket == null) {
               result = 5;
            }

            boolean changed = cicalConfiguraton.setCapabilities(configEvent.getCapabilitySettings(), 2, false);
            changed = cicalConfiguraton.setConflictResolution(configEvent.getConflictSettings(), 2, false) || changed;
            byte[] serverUserSettings = configEvent.getUserSettings();
            byte[] currentUserSettings = cicalConfiguraton.getUserSettings(true);
            if (serverUserSettings == null) {
               serverUserSettings = this._emptySettings;
            }

            int code = 1396899840;
            if (serverUserSettings != null) {
               code = code | serverUserSettings[1] << 8 | serverUserSettings[0];
            }

            CICALEventLogger.logEvent(code, 5);
            code = 1430454272;
            if (currentUserSettings != null) {
               code = code | currentUserSettings[1] << 8 | currentUserSettings[0];
            }

            CICALEventLogger.logEvent(code, 5);
            if (!Arrays.equals(currentUserSettings, serverUserSettings)) {
               changed = true;
               CICALEventLogger.logEvent(1129665357, 5);
               configConverter.sendDeviceConfiguration(calendarService);
            }

            configConverter.ackConfigurationUpdate(result, calendarService);
            if (result == 0 && changed) {
               slowSyncConverter.startCalendarSlowSync(-4778897293134846142L, calendarService);
            } else {
               CICALEventLogger.logEvent(1129205571, 5);
            }
            break;
         case 28:
            CICALEventLogger.logEvent(1380139841, 0);
      }

      ticket = null;
      return true;
   }
}
