package net.rim.device.api.system;

public interface WLANConnectionListener extends WLANListener {
   int WLAN_UNSPECIFIED_REASON = 0;
   int WLAN_CONNECTION_TERMINATED = 1;
   int WLAN_IP_ADDRESS_LOST = 2;
   int WLAN_DHCP_UNREACHABLE = 3;
   int WLAN_OUT_OF_COVERAGE = 4;
   int WLAN_AUTH_FAIL = 5;
   int WLAN_ASSOCIATION_FAILED = 6;

   void networkConnected();

   void networkDisconnected(int var1);
}
