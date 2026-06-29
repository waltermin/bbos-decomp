package net.rim.device.cldc.impl.tunnel;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.cldc.io.tunnel.TunnelApnList;
import net.rim.device.cldc.io.tunnel.TunnelApnListFactory;

public final class TunnelApnListFactoryImpl extends TunnelApnListFactory {
   public TunnelApnListFactoryImpl() {
      ApplicationRegistry.getApplicationRegistry().put(-977265756801572775L, this);
   }

   @Override
   public final TunnelApnList createTunnelApnList() {
      return new TunnelApnListImpl();
   }
}
