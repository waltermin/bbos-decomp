package net.rim.device.apps.api.transmission.rim.otasync;

import net.rim.device.api.system.ApplicationRegistry;

public final class OTAFMConfigurationManager$Instance {
   public static final long OTAFM_CONFIG_MANAGER_GUID = -2200702284699521671L;

   private OTAFMConfigurationManager$Instance() {
   }

   public static final OTAFMConfigurationManager getInstance() {
      return (OTAFMConfigurationManager)ApplicationRegistry.getApplicationRegistry().waitFor(-2200702284699521671L);
   }
}
