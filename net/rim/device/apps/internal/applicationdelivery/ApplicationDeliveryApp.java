package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;

class ApplicationDeliveryApp {
   public static void libMain(String[] args) {
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.addDeviceCapabilities((byte)13, new byte[]{1});
      }

      ApplicationDeliveryEventLogger.register();
      ApplicationDeliveryTransmissionService transmissionService = new ApplicationDeliveryTransmissionService();
      TransmissionServiceManager.register(transmissionService);
   }
}
