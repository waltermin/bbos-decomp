package net.rim.device.api.crypto;

import net.rim.device.api.system.ControlledAccessException;

public final class Certicom {
   private Certicom() {
   }

   public static final native boolean isAccessAllowed();

   public static final void assertAccessAllowed() {
      if (!isAccessAllowed()) {
         throw new ControlledAccessException("Missing RCC signature. Not allowed to access Certicom functionality");
      }
   }
}
