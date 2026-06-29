package net.rim.device.cldc.impl.tunnel;

import net.rim.device.cldc.io.tunnel.TunnelFactory;

final class TunnelManagerImpl$1 implements Runnable {
   private final TunnelManagerImpl this$0;

   TunnelManagerImpl$1(TunnelManagerImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      ((TunnelFactoryImpl)TunnelFactory.getTunnelFactory()).kickManagers(false);
   }
}
