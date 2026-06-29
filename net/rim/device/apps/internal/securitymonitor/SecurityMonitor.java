package net.rim.device.apps.internal.securitymonitor;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.services.runtime.ServiceStartup;

final class SecurityMonitor extends Application {
   private static final long ID = 4540117967283091590L;

   public static final void main(String[] args) {
      if (args.length == 0) {
         SecurityMonitor$SecurityMonitorImpl securityMonitor = new SecurityMonitor$SecurityMonitorImpl(null);
         ApplicationRegistry.getApplicationRegistry().put(4540117967283091590L, securityMonitor);
         ServiceStartup.getInstance().registerCriticalService(4);
      } else {
         if (args.length == 2) {
            SecurityMonitor$SecurityMonitorImpl securityMonitor = (SecurityMonitor$SecurityMonitorImpl)ApplicationRegistry.getApplicationRegistry()
               .get(4540117967283091590L);
            securityMonitor.checkTimers(args[0], args[1]);
         }
      }
   }
}
