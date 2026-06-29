package net.rim.device.api.system;

public interface WLANConnectionListener extends WLANListener {
   int WLAN_UNSPECIFIED_REASON;
   int WLAN_CONNECTION_TERMINATED;
   int WLAN_IP_ADDRESS_LOST;
   int WLAN_DHCP_UNREACHABLE;
   int WLAN_OUT_OF_COVERAGE;
   int WLAN_AUTH_FAIL;
   int WLAN_ASSOCIATION_FAILED;

   void networkConnected();

   void networkDisconnected(int var1);
}
