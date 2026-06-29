package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;

public final class Registration {
   public static final void register() {
      TransmissionServiceManager.register(new RIMMessagingService());
      EventLogger.register(3020044433160143544L, "cmime", 2);
      OptionsProviderRegistration$OptionsProvider op = new Registration$1();
      OptionsProviderRegistration.registerOptionsProvider(op);
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.addDeviceCapabilities((byte)8, CMIMEUtilities.getEncodings());
      }
   }
}
