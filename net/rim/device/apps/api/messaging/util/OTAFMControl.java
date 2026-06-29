package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.system.ApplicationRegistry;

public class OTAFMControl {
   private static final long OTAFM_CONTROL_GUID = 1323329749908372946L;

   protected OTAFMControl() {
   }

   protected void register() {
      ApplicationRegistry.getApplicationRegistry().put(1323329749908372946L, this);
   }

   public static OTAFMControl getControlInstance() {
      return (OTAFMControl)ApplicationRegistry.getApplicationRegistry().waitFor(1323329749908372946L);
   }

   public void flushBatchedCommands() {
      throw null;
   }
}
