package net.rim.device.api.system;

public interface WLANListenerInternal extends RadioListener {
   int WLAN_UNSPECIFIED_REASON = 0;
   int WLAN_CONNECTION_TERMINATED = 1;
   int WLAN_IP_ADDRESS_LOST = 2;
   int WLAN_DHCP_UNREACHABLE = 3;
   int WLAN_OUT_OF_COVERAGE = 4;
   int WLAN_AUTH_FAIL = 5;
   int WLAN_ASSOCIATION_FAILED = 6;
   int WLAN_AUTH_FAIL_DEAUTH = 1;
   int WLAN_AUTH_FAIL_EAP = 2;
   int WLAN_AUTH_FAIL_EAP_INCOMPLETE = 3;
   int WLAN_AUTH_FAIL_EAP_MISMATCH = 4;
   int WLAN_AUTH_FAIL_WPA_PTK_EXCH_FAIL = 5;
   int WLAN_AUTH_FAIL_WPA_GTK_EXCH_FAIL = 6;
   int WLAN_AUTH_FAIL_CRED_INCOMPLETE = 7;
   int WLAN_AUTH_FAIL_SERVER_UNAVAILABLE = 8;
   int WLAN_AUTH_FAIL_AP_UNRESPONSIVE = 9;
   int WLAN_AUTH_FAIL_SERVER_AUTH = 10;
   int WLAN_AUTH_FAIL_PAC_PROVISIONED = 11;
   int WLAN_AUTH_FAIL_WPS_PROVISIONED = 12;

   void radioStatus(boolean var1);

   void networkSuccess();

   void networkFail(int var1, int var2, int var3);

   void networkFound(int var1);

   void networkApChange();
}
