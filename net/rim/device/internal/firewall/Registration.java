package net.rim.device.internal.firewall;

import net.rim.device.api.system.ApplicationRegistry;

public final class Registration {
   public static final void FirewallMain() {
      FirewallImpl firewallImpl = new FirewallImpl();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(6444309033832430955L, firewallImpl);
   }
}
