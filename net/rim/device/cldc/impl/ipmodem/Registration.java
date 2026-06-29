package net.rim.device.cldc.impl.ipmodem;

import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.provisioning.ProvisioningService;
import net.rim.device.internal.proxy.Proxy;

public final class Registration {
   public static final void ModemMain(String[] args) {
      EventLogger.register(3239869412285299861L, "net.rim.ipmodem", 2);

      try {
         ProvisioningService provService = ProvisioningService.getInstance();
         IPModemProvHandler IPModemHandler = new IPModemProvHandler();
         provService.addHandler(IPModemHandler);
         Proxy.getInstance().addGlobalEventListener(IPModemHandler);
      } finally {
         EventLogger.logEvent(3239869412285299861L, 1347636801, 2);
         return;
      }
   }
}
