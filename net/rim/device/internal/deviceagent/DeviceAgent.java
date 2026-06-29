package net.rim.device.internal.deviceagent;

import net.rim.device.api.util.DataBuffer;

public interface DeviceAgent {
   boolean addDeviceCapabilities(byte var1, byte[] var2);

   byte[] getDeviceCapabilities(byte var1);

   boolean setDeviceCapabilitiesFlag(byte var1, byte[] var2);

   boolean clearDeviceCapabilitiesFlag(byte var1, byte[] var2);

   boolean removeDeviceCapabilities(byte var1);

   DataBuffer getDeviceAgentInfo(int var1);

   DataBuffer getDeviceAgentInfo(byte var1);
}
