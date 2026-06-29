package net.rim.device.apps.api;

import net.rim.device.apps.api.calendar.caldb.CalendarServiceFactory;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.service.MultiServiceManager;
import net.rim.device.apps.api.transmission.rim.Registration;
import net.rim.device.apps.api.vcal.VCal;

final class PackageManager {
   public static final void libMain(String[] args) {
      MultiServiceManager msm = MultiServiceManager.getInstance();
      msm.registerCID("CICAL", new CalendarServiceFactory());
      TimeBasedCollection.register();
      Registration.register();
      VCal.register();
      net.rim.device.apps.internal.itadmin.Registration.ITAdminMain();
   }
}
