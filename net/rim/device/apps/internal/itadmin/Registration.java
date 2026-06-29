package net.rim.device.apps.internal.itadmin;

import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.services.runtime.ServiceStartup;

public final class Registration {
   public static final void ITAdminMain() {
      ITAdminEventLogger.register();
      ITAdminTransmissionService transmissionService = new ITAdminTransmissionService();
      TransmissionServiceManager.register(transmissionService);
      ServiceStartup.getInstance().registerCriticalService(2);
   }
}
