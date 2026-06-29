package net.rim.device.cldc.impl.tunnel;

public final class Registration {
   public static final void TunnelMain(String[] args) {
      new TunnelFactoryImpl();
      new TunnelApnListFactoryImpl();
   }
}
