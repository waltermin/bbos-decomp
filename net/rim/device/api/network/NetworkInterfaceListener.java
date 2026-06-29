package net.rim.device.api.network;

public interface NetworkInterfaceListener {
   int EVENT_IP_CHANGED;

   void networkInterfaceStatusChanged(boolean var1, Object var2, NetworkInterfaceManager var3);

   void networkInterfaceEvent(int var1, Object var2, NetworkInterfaceManager var3);
}
