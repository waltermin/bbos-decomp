package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;

public class InternalLowMemoryManager {
   private InternalLowMemoryManager() {
   }

   public static void exactPoll() {
      ExactLowMemoryManager lmm = (ExactLowMemoryManager)ApplicationRegistry.getApplicationRegistry().waitFor(7979320271643693911L);
      lmm.exactPoll();
   }
}
