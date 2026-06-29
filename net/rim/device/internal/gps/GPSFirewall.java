package net.rim.device.internal.gps;

import net.rim.device.api.system.ApplicationRegistry;

public final class GPSFirewall {
   public static final GPSFirewallInterface getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (GPSFirewallInterface)ar.waitFor(-3752949794647167067L);
   }
}
