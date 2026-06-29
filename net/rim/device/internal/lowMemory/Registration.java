package net.rim.device.internal.lowMemory;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.proxy.Proxy;

public final class Registration {
   public static final void LowMemoryMain() {
      LowMemoryManagerImpl lowMemoryManager = new LowMemoryManagerImpl();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(7979320271643693911L, lowMemoryManager);
      Proxy.getInstance().addGlobalEventListener(lowMemoryManager);
   }
}
