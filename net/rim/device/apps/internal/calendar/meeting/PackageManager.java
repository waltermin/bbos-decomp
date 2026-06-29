package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.i18n.UnicodeServiceUtilities;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(5431812121400615279L, new AttendeeModelFactory());
      MeetingListener meetingListener = new MeetingListener();
      ar.put(8503322669810003080L, meetingListener);
      ar.put(7228817433593908387L, new MeetingInfoFactory());
      CalendarProxy calProxy = CalendarProxy.getInstance();
      calProxy.addFactoryID(7228817433593908387L);
      calProxy.addFactoryID(813899564474876953L);
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.addDeviceCapabilities((byte)9, UnicodeServiceUtilities.getSupportedEncodings());
      }
   }
}
