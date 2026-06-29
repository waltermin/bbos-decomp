package net.rim.device.apps.internal.securitymonitor;

import net.rim.device.api.util.Persistable;

final class SecurityMonitor$SecurityMonitorImpl$Data implements Persistable {
   long _lockWipeStartTime;
   long _itPolicyWipeStartTime;
   long _delayedWipeStartTime;
   long _delayedWipeDelay;

   private SecurityMonitor$SecurityMonitorImpl$Data() {
   }

   SecurityMonitor$SecurityMonitorImpl$Data(SecurityMonitor$1 x0) {
      this();
   }
}
