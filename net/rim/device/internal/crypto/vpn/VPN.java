package net.rim.device.internal.crypto.vpn;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.WLAN;

public final class VPN {
   public static final int GATEWAY_ALCATEL = 1;
   public static final int GATEWAY_AVAYA = 2;
   public static final int GATEWAY_CHECKPOINT = 3;
   public static final int GATEWAY_CISCO3000 = 4;
   public static final int GATEWAY_CISCOUNITY = 5;
   public static final int GATEWAY_CISCOSECURE_FIREWALL = 5;
   public static final int GATEWAY_CISCOIOS = 133;
   public static final int GATEWAY_COSINE = 6;
   public static final int GATEWAY_CYLINK = 7;
   public static final int GATEWAY_INTEL = 8;
   public static final int GATEWAY_LUCENT = 9;
   public static final int GATEWAY_NETSCREEN = 10;
   public static final int GATEWAY_NORTEL = 11;
   public static final int GATEWAY_REEFEDGE = 12;
   public static final int GATEWAY_SECURECOMPUTING = 13;
   public static final int GATEWAY_SYMANTEC = 14;
   public static final int GATEWAY_STARENT = 18;
   public static final int GATEWAY_IKEV2 = 15;
   public static final int XAUTHTYPE_USERNAME_AND_PASSWORD = 2;
   public static final int XAUTHTYPE_SECURID = 3;
   public static final int GROUP_1 = 1;
   public static final int GROUP_2 = 2;
   public static final int GROUP_5 = 5;
   public static final int GROUP_7 = 7;
   public static final int GROUP_9 = 9;
   public static final int CIPHER_DES = 2;
   public static final int CIPHER_3DES = 3;
   public static final int CIPHER_AES128 = 4;
   public static final int CIPHER_AES192 = 5;
   public static final int CIPHER_AES256 = 6;
   public static final int HASH_MD5 = 2;
   public static final int HASH_SHA = 3;
   public static final int SUITE_NULL_MD5 = 1792;
   public static final int SUITE_NULL_SHA = 1793;
   public static final int SUITE_DES_NULL = 1794;
   public static final int SUITE_DES_MD5 = 1795;
   public static final int SUITE_DES_SHA = 1796;
   public static final int SUITE_3DES_NULL = 1797;
   public static final int SUITE_3DES_MD5 = 1798;
   public static final int SUITE_3DES_SHA = 1799;
   public static final int SUITE_AES128_NULL = 1800;
   public static final int SUITE_AES128_MD5 = 1801;
   public static final int SUITE_AES128_SHA = 1802;
   public static final int SUITE_AES192_NULL = 1803;
   public static final int SUITE_AES192_MD5 = 1804;
   public static final int SUITE_AES192_SHA = 1805;
   public static final int SUITE_AES256_NULL = 1806;
   public static final int SUITE_AES256_MD5 = 1807;
   public static final int SUITE_AES256_SHA = 1808;
   public static final int USE_PFS = 2;
   public static final int USE_DHCP = 32;
   public static final int USE_XAUTH = 268435456;
   public static final int USE_TOKEN = 536870912;
   public static final int EVENT_FLAG_PROMPT_NONE = 0;
   public static final int EVENT_FLAG_PROMPT_USERID = 1;
   public static final int EVENT_FLAG_PROMPT_PASSWORD = 2;
   public static final int STATUS_ERROR_INSUFFICIENT_RANDOMNESS = -18;
   public static final int STATUS_ERROR_POLICY_IN_USE = -8;
   public static final int STATUS_ERROR_NO_MEMORY = -4;
   public static final int STATUS_ERROR_BAD_PARAM = -3;
   public static final int STATUS_ERROR = -2;
   public static final int STATUS_OK = 0;
   static final int STATUS_ERROR_INVALID_HANDLE_ID = -27;
   static final int STATUS_ERROR_BAD_CERT_VALIDITY_PERIOD = -26;
   static final int STATUS_ERROR_TIMEOUT = -25;
   static final int STATUS_ERROR_EAPAKA_FAILED = -24;
   static final int STATUS_ERROR_EAPSIM_FAILED = -23;
   static final int STATUS_ERROR_NO_SIM_CHALLENGE = -21;
   static final int STATUS_DISCONNECTED_BY_GATEWAY = -20;
   static final int STATUS_ERROR_PASSWORD_REQUIRED = -17;
   static final int STATUS_ERROR_USERNAME_REQUIRED = -16;
   static final int STATUS_ERROR_GROUPPASSWORD_REQUIRED = -15;
   static final int STATUS_ERROR_GROUPNAME_REQUIRED = -14;
   static final int STATUS_ERROR_BAD_GATEWAY_TYPE = -13;
   static final int STATUS_ERROR_BAD_PSK = -12;
   static final int STATUS_ERROR_BAD_GATEWAY_AUTH_ID_TYPE = -11;
   static final int STATUS_ERROR_BAD_GATEWAY_AUTH_ID = -10;
   static final int STATUS_ERROR_NOT_LOGGED_IN = -7;
   static final int STATUS_ABORTED = -1;
   static final int STATUS_OK_CONNECTED = 4;
   static final int STATUS_OK_DISCONNECTED = 5;
   static final int STATUS_OK_SIM_CHALLENGE = 8;
   static final int STATUS_OK_AKA_CHALLENGE = 9;
   static final int STATUS_OK_RESOLVE_GATEWAY = 11;
   static final int STATUS_OK_PROMPT_USER = 12;
   static final int STATUS_OK_CHECK_CERTIFICATE = 13;
   static final int EVENT_CONNECT = 8960;
   static final int EVENT_DISCONNECT = 8961;
   static final int EVENT_CONNECTED = 8962;
   static final int EVENT_DISCONNECTED = 8966;
   public static final long GUID_VPN_CONFIGURATION_CHANGED = 7181491349594683390L;
   private static final long ID_VPN_SYSTEM = 4078127605297297308L;
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
