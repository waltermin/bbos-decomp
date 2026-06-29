package net.rim.device.apps.internal.implus;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.proxy.Proxy;

final class IMPlusApp extends Application {
   public static final void main(String[] args) {
      Proxy proxy = Proxy.getInstance();
      proxy.addGlobalEventListener(IMPlusCmimeListener.getInstance());
      IMPlusCmimeListener.getInstance().init();
      IMPlusRequestOptionModelFactory optionModelFactory = new IMPlusRequestOptionModelFactory((byte)0);
      ApplicationRegistry.getApplicationRegistry().put(2620647646956286337L, optionModelFactory);
   }
}
