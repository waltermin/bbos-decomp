package net.rim.device.internal.deviceagent;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.internal.crypto.CryptoBlock;

public class Registration {
   public static void DeviceAgentMain() {
      DeviceAgentEventLogger.register();
      SyncManager syncManager = SyncManager.getInstance();
      syncManager.enableSynchronization(IncomingDeviceAgentCollection.getInstance());
      syncManager.enableSynchronization(OutgoingDeviceAgentCollection.getInstance());
      OutgoingDeviceAgentCollection da = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      da.addDeviceCapabilities((byte)6, CryptoBlock.TRANSPORT_ENCRYPTION_ALGORITHM_BITFIELD);
      da.addDeviceCapabilities((byte)14, new byte[]{1});
   }
}
