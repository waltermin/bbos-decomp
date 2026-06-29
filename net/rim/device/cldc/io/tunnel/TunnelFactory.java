package net.rim.device.cldc.io.tunnel;

import net.rim.device.api.system.ApplicationRegistry;

public class TunnelFactory {
   public static final long ID = 4292459735430940092L;
   public static final String STR = "net.rim.tunnel";

   public static Tunnel openTunnel(TunnelConfig config) {
      return getTunnelFactory().open(config);
   }

   public static Object setupTunnel(int callType, Object context) {
      return getTunnelFactory().setup(callType, context);
   }

   public static TunnelFactory getTunnelFactory() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (TunnelFactory)ar.waitFor(4292459735430940092L);
   }

   public Tunnel open(TunnelConfig _1) {
      throw null;
   }

   public Object setup(int _1, Object _2) {
      throw null;
   }
}
