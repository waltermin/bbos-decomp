package net.rim.device.internal.crypto.vpn;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.WLAN;

public final class VPN {
   public static final int GATEWAY_ALCATEL;
   public static final int GATEWAY_AVAYA;
   public static final int GATEWAY_CHECKPOINT;
   public static final int GATEWAY_CISCO3000;
   public static final int GATEWAY_CISCOUNITY;
   public static final int GATEWAY_CISCOSECURE_FIREWALL;
   public static final int GATEWAY_CISCOIOS;
   public static final int GATEWAY_COSINE;
   public static final int GATEWAY_CYLINK;
   public static final int GATEWAY_INTEL;
   public static final int GATEWAY_LUCENT;
   public static final int GATEWAY_NETSCREEN;
   public static final int GATEWAY_NORTEL;
   public static final int GATEWAY_REEFEDGE;
   public static final int GATEWAY_SECURECOMPUTING;
   public static final int GATEWAY_SYMANTEC;
   public static final int GATEWAY_STARENT;
   public static final int GATEWAY_IKEV2;
   public static final int XAUTHTYPE_USERNAME_AND_PASSWORD;
   public static final int XAUTHTYPE_SECURID;
   public static final int GROUP_1;
   public static final int GROUP_2;
   public static final int GROUP_5;
   public static final int GROUP_7;
   public static final int GROUP_9;
   public static final int CIPHER_DES;
   public static final int CIPHER_3DES;
   public static final int CIPHER_AES128;
   public static final int CIPHER_AES192;
   public static final int CIPHER_AES256;
   public static final int HASH_MD5;
   public static final int HASH_SHA;
   public static final int SUITE_NULL_MD5;
   public static final int SUITE_NULL_SHA;
   public static final int SUITE_DES_NULL;
   public static final int SUITE_DES_MD5;
   public static final int SUITE_DES_SHA;
   public static final int SUITE_3DES_NULL;
   public static final int SUITE_3DES_MD5;
   public static final int SUITE_3DES_SHA;
   public static final int SUITE_AES128_NULL;
   public static final int SUITE_AES128_MD5;
   public static final int SUITE_AES128_SHA;
   public static final int SUITE_AES192_NULL;
   public static final int SUITE_AES192_MD5;
   public static final int SUITE_AES192_SHA;
   public static final int SUITE_AES256_NULL;
   public static final int SUITE_AES256_MD5;
   public static final int SUITE_AES256_SHA;
   public static final int USE_PFS;
   public static final int USE_DHCP;
   public static final int USE_XAUTH;
   public static final int USE_TOKEN;
   public static final int EVENT_FLAG_PROMPT_NONE;
   public static final int EVENT_FLAG_PROMPT_USERID;
   public static final int EVENT_FLAG_PROMPT_PASSWORD;
   public static final int STATUS_ERROR_INSUFFICIENT_RANDOMNESS;
   public static final int STATUS_ERROR_POLICY_IN_USE;
   public static final int STATUS_ERROR_NO_MEMORY;
   public static final int STATUS_ERROR_BAD_PARAM;
   public static final int STATUS_ERROR;
   public static final int STATUS_OK;
   static final int STATUS_ERROR_INVALID_HANDLE_ID;
   static final int STATUS_ERROR_BAD_CERT_VALIDITY_PERIOD;
   static final int STATUS_ERROR_TIMEOUT;
   static final int STATUS_ERROR_EAPAKA_FAILED;
   static final int STATUS_ERROR_EAPSIM_FAILED;
   static final int STATUS_ERROR_NO_SIM_CHALLENGE;
   static final int STATUS_DISCONNECTED_BY_GATEWAY;
   static final int STATUS_ERROR_PASSWORD_REQUIRED;
   static final int STATUS_ERROR_USERNAME_REQUIRED;
   static final int STATUS_ERROR_GROUPPASSWORD_REQUIRED;
   static final int STATUS_ERROR_GROUPNAME_REQUIRED;
   static final int STATUS_ERROR_BAD_GATEWAY_TYPE;
   static final int STATUS_ERROR_BAD_PSK;
   static final int STATUS_ERROR_BAD_GATEWAY_AUTH_ID_TYPE;
   static final int STATUS_ERROR_BAD_GATEWAY_AUTH_ID;
   static final int STATUS_ERROR_NOT_LOGGED_IN;
   static final int STATUS_ABORTED;
   static final int STATUS_OK_CONNECTED;
   static final int STATUS_OK_DISCONNECTED;
   static final int STATUS_OK_SIM_CHALLENGE;
   static final int STATUS_OK_AKA_CHALLENGE;
   static final int STATUS_OK_RESOLVE_GATEWAY;
   static final int STATUS_OK_PROMPT_USER;
   static final int STATUS_OK_CHECK_CERTIFICATE;
   static final int EVENT_CONNECT;
   static final int EVENT_DISCONNECT;
   static final int EVENT_CONNECTED;
   static final int EVENT_DISCONNECTED;
   public static final long GUID_VPN_CONFIGURATION_CHANGED;
   private static final long ID_VPN_SYSTEM;
   private static VPNSystem _system;

   private VPN() {
   }

   public static final void setVPNSystem(VPNSystem system) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      if (appReg != null) {
         if (appReg.replace(4078127605297297308L, system) != null) {
            throw new IllegalStateException("VPN system already present");
         }

         _system = system;
      }
   }

   public static final VPNSystem getVPNSystem() {
      if (_system == null) {
         ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
         _system = appReg != null ? (VPNSystem)appReg.get(4078127605297297308L) : null;
      }

      return _system;
   }

   public static final boolean isSupported() {
      return isVPNVersionSupported();
   }

   public static final boolean isVPNAllowed() {
      if (WLAN.isSupported() && isSupported()) {
         VPNSystem vpn = getVPNSystem();
         if (vpn != null) {
            return vpn.isVPNAllowed();
         }
      }

      return false;
   }

   public static final boolean isIPSecRequiredForNetwork(String network, int networkType) {
      if (WLAN.isSupported() && isSupported()) {
         VPNSystem vpn = getVPNSystem();
         if (vpn != null) {
            return vpn.isIPSecRequiredForNetwork(network, networkType);
         }
      }

      return false;
   }

   public static final boolean isConnected() {
      if (WLAN.isSupported() && isSupported()) {
         VPNSystem vpn = getVPNSystem();
         if (vpn != null) {
            return vpn.isConnected();
         }
      }

      return false;
   }

   public static final boolean livenessCheckEnabled() {
      if (WLAN.isSupported() && isSupported()) {
         VPNSystem vpn = getVPNSystem();
         if (vpn != null) {
            return vpn.livenessCheckEnabled();
         }
      }

      return false;
   }

   static final native String getBanner(int var0);

   static final native String getChallenge(int var0, int[] var1);

   static final native int setResponse(int var0, String var1, String var2, int var3);

   static final native int getSessionLifetime(int var0);

   static final native byte[] getCertificate(int var0);

   static final native int acceptCertificate(int var0, boolean var1);

   private static final native boolean isVPNVersionSupported();
}
