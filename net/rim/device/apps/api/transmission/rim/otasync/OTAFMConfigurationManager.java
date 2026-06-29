package net.rim.device.apps.api.transmission.rim.otasync;

import net.rim.device.api.servicebook.ServiceRecord;

public interface OTAFMConfigurationManager {
   boolean isOTAFMAvailable();

   OTAFMConfiguration getConfiguration(ServiceRecord var1);

   boolean wirelessDeletesAllowed(ServiceRecord var1);

   boolean wirelessFilingAllowed(ServiceRecord var1);

   void updateConfiguration(ServiceRecord var1, OTAFMConfiguration var2, boolean var3);
}
