package net.rim.device.api.system;

public interface WLANListenerInternal extends RadioListener {
   int WLAN_UNSPECIFIED_REASON;
   int WLAN_CONNECTION_TERMINATED;
   int WLAN_IP_ADDRESS_LOST;
   int WLAN_DHCP_UNREACHABLE;
   int WLAN_OUT_OF_COVERAGE;
   int WLAN_AUTH_FAIL;
   int WLAN_ASSOCIATION_FAILED;
   int WLAN_AUTH_FAIL_DEAUTH;
   int WLAN_AUTH_FAIL_EAP;
   int WLAN_AUTH_FAIL_EAP_INCOMPLETE;
   int WLAN_AUTH_FAIL_EAP_MISMATCH;
   int WLAN_AUTH_FAIL_WPA_PTK_EXCH_FAIL;
   int WLAN_AUTH_FAIL_WPA_GTK_EXCH_FAIL;
   int WLAN_AUTH_FAIL_CRED_INCOMPLETE;
   int WLAN_AUTH_FAIL_SERVER_UNAVAILABLE;
   int WLAN_AUTH_FAIL_AP_UNRESPONSIVE;
   int WLAN_AUTH_FAIL_SERVER_AUTH;
   int WLAN_AUTH_FAIL_PAC_PROVISIONED;
   int WLAN_AUTH_FAIL_WPS_PROVISIONED;

   void radioStatus(boolean var1);

   void networkSuccess();

   void networkFail(int var1, int var2, int var3);

   void networkFound(int var1);

   void networkApChange();
}
