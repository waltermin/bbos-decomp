package net.rim.device.api.system;

import net.rim.device.api.util.NumberUtilities;

public final class WLAN {
   public static String WLAN_PSEUDO_APN = "MagicRudyAPN.rim";
   public static final int WLAN_MAX_SSID_SIZE = 32;
   public static final int WLAN_MAC_ADDRESS_SIZE = 6;
   public static final int WLAN_NUM_WEP_KEYS = 4;
   public static final int WLAN_MAX_DOMAIN_SUFFIX_LEN = 64;
   public static final int WLAN_MAX_PROFILES = 32;
   public static final int WLAN_MAX_PROFILE_SETS = 1;
   public static final int WLAN_BAND_NONE = 0;
   public static final int WLAN_BAND_BG = 1;
   public static final int WLAN_BAND_A = 2;
   public static final int WLAN_BAND_B = 4;
   public static final int WLAN_BAND_ALL = 7;
   public static final int WLAN_NETWORK_IBSS = 0;
   public static final int WLAN_NETWORK_ESS = 1;
   public static final int WLAN_AUTHENT_CAT_NONE = 0;
   public static final int WLAN_AUTHENT_CAT_OPEN = 1;
   public static final int WLAN_AUTHENT_CAT_WEP = 2;
   public static final int WLAN_AUTHENT_CAT_8021X = 4;
   public static final int WLAN_AUTHENT_CAT_WPA_PERSONAL = 8;
   public static final int WLAN_AUTHENT_CAT_WPA_ENTERPRISE = 16;
   public static final int WLAN_AUTHENT_CAT_WPA2_PERSONAL = 32;
   public static final int WLAN_AUTHENT_CAT_WPA2_ENTERPRISE = 64;
   public static final int WLAN_AUTHENT_NONE = 0;
   public static final int WLAN_AUTHENT_OPEN = 1;
   public static final int WLAN_AUTHENT_WEP = 2;
   public static final int WLAN_AUTHENT_WPAPSK = 4;
   public static final int WLAN_AUTHENT_EAPLEAP = 8;
   public static final int WLAN_AUTHENT_EAPPEAP = 16;
   public static final int WLAN_AUTHENT_EAPTLS = 32;
   public static final int WLAN_AUTHENT_EAPFAST = 64;
   public static final int WLAN_AUTHENT_EAPTTLS = 128;
   public static final int WLAN_AUTHENT_EAPSIM = 256;
   public static final int WLAN_AUTHENT_EAPAKA = 512;
   public static final int WLAN_AUTHENT_EAPWPS = 1024;
   public static final int WLAN_AUTHENT_EAPMSCHAPV2 = 1048576;
   public static final int WLAN_AUTHENT_EAPGTC = 2097152;
   public static final int WLAN_AUTHENT_PAP = 4194304;
   public static final int WLAN_AUTHENT_CHAP = 8388608;
   public static final int WLAN_AUTHENT_MSCHAP = 16777216;
   public static final int WLAN_AUTHENT_MSCHAPV2 = 33554432;
   public static final int WLAN_AUTHENT_EAPMD5 = 67108864;
   public static final int WLAN_AUTHENT_AUTO = 134217728;
   public static final int WLAN_CRED_NONE = 0;
   public static final int WLAN_CRED_WEPKEY = 1;
   public static final int WLAN_CRED_WPAPSK = 2;
   public static final int WLAN_CRED_USERNAME = 4;
   public static final int WLAN_CRED_PASSWORD = 8;
   public static final int WLAN_CRED_CA_CERT = 16;
   public static final int WLAN_CRED_CLIENT_CERT = 32;
   public static final int WLAN_CRED_INNERAUTH = 64;
   public static final int WLAN_CRED_TOKENSERIAL = 128;
   public static final int WLAN_CRED_IMSI = 256;
   public static final int WLAN_CRED_SERVER_SUBJECT = 512;
   public static final int WLAN_CRED_SERVER_SAN = 1024;
   public static final int WLAN_CRED_PAC = 2048;
   public static final int WLAN_RET_SUCCESS = 1;
   public static final int WLAN_RET_NOT_ASSOCIATED = -1;
   public static final int WLAN_RET_NULL_POINTER_ARGUMENT = -2;
   public static final int WLAN_RET_BUFFER_TOO_SMALL = -4;
   public static final int WLAN_RET_UNSUPPORTED = -7;
   public static final int WLAN_RET_RADIO_IS_OFF = -8;
   public static final int WLAN_RET_MEM_ALLOC_FAIL = -9;
   public static final int WLAN_RET_PARAMETER_OUT_OF_RANGE = -12;
   public static final int WLAN_RET_DEPRECATED_API = -13;
   public static final int WLAN_RET_RADIO_FAILURE = -14;
   public static final int WLAN_RET_CANNOT_CREATE_PROFILE_SET = -15;
   public static final int WLAN_RET_INVALID_PROFILE_SET = -16;
   public static final int WLAN_RET_NOT_REGISTERED = -18;
   public static final int WLAN_RET_DUPLICATE_PROFILE = -19;
   public static final int WLAN_RET_MAX_PROFILES_REACHED = -20;
   public static final int WLAN_RET_EVENT_TABLE_FULL = -21;
   public static final int WLAN_RET_INVALID_PROFILEID = -22;
   public static final int WLAN_RET_CHALLENGE_NOT_AVAIL = -23;
   public static final int WLAN_RET_RECORD_NOT_AVAIL = -24;
   public static final long GUID_WLAN_NET_CONNECTIVITY = 3212036545190435442L;
   public static final long GUID_WLAN_CONFIGURATION_CHANGED = 2950066364548195165L;
   private static final long ID_WLAN_SYSTEM = 6850047726709752304L;
   public static final int WLAN_EVENT_RADIO_STARTED = 4609;
   public static final int WLAN_EVENT_RADIO_STOPPED = 4610;
   public static final int WLAN_EVENT_NETWORK_AP_CHANGE = 4618;
   public static final int WLAN_EVENT_NETWORK_FOUND = 4619;
   public static final int WLAN_EVENT_NETWORK_CONNECTED = 4620;
   public static final int WLAN_EVENT_NETWORK_DISCONNECTED = 4621;
   public static final int WLAN_EVENT_NETWORK_CHALLENGE = 4622;
   public static final int WLAN_EVENT_EXTENDED_INFO_UPDATE = 4623;
   public static final int WLAN_FLAG_USE_DHCP = 1;
   public static final int WLAN_FLAG_AP_TO_AP_HANDOVER_ALLOWED = 2;
   public static final int WLAN_FLAG_GAN_ALLOWED = 4;
   public static final int WLAN_FLAG_AES_CCMP_ONLY = 8;
   public static final int WLAN_FLAG_NO_PSEUDONYMS = 16;
   public static final int WLAN_FLAG_NO_SERV_CERT_REALM_CHECK = 32;
   public static final int WLAN_FLAG_PREFIX_SSID = 64;
   public static final int WLAN_WPS_PIN_ENABLED = 128;
   public static final int WLAN_WPS_PBC_ENABLED = 256;
   public static final int WLAN_WPS_PIN_READY = 512;
   public static final int WLAN_WPS_PBC_READY = 1024;
   public static final int WLAN_PAC_ADHP_DISABLE = 2048;
   public static final int WLAN_PAC_CDHP_ENABLED = 4096;
   public static final int WLAN_NO_SERV_CERT_VALIDATION = 8192;
   public static final int WLAN_SMART_CARD_SIGNING = 16384;
   public static final int WLAN_WPS_OTHER_READY = 32768;
   public static final int WLAN_NETWORK_STATE_NONE = 0;
   public static final int WLAN_NETWORK_STATE_NA = 1;
   public static final int WLAN_NETWORK_STATE_TRYING = 2;
   public static final int WLAN_NETWORK_STATE_SUCCESS = 3;
   public static final int WLAN_NETWORK_STATE_ERROR = 4;
   public static final int WLAN_NETWORK_SIGQUAL_NONE = 0;
   public static final int WLAN_NETWORK_SIGQUAL_VERYPOOR = 1;
   public static final int WLAN_NETWORK_SIGQUAL_POOR = 2;
   public static final int WLAN_NETWORK_SIGQUAL_AVERAGE = 3;
   public static final int WLAN_NETWORK_SIGQUAL_GOOD = 4;
   public static final int WLAN_NETWORK_CIPHER_INVALID = 0;
   public static final int WLAN_NETWORK_CIPHER_NONE = 1;
   public static final int WLAN_NETWORK_CIPHER_WEP40 = 16;
   public static final int WLAN_NETWORK_CIPHER_WEP104 = 2;
   public static final int WLAN_NETWORK_CIPHER_TKIP = 4;
   public static final int WLAN_NETWORK_CIPHER_CCMP = 8;
   public static final int WLAN_RECORD_NONE = 0;
   public static final int WLAN_RECORD_DEVICE_PAC_FILE = 1;
   public static final int WLAN_RECORD_SERVER_PAC_FILE = 2;
   public static final int WLAN_RECORD_WLAN_LOG = 3;
   public static final int WLAN_NETWORK_PROMPT_PASSWORD = 1;
   public static final int WLAN_NETWORK_PROMPT_NEW_PASSWORD = 2;
   public static final int WLAN_NETWORK_PROMPT_CHALLENGE = 3;
   private static WLANSystem _system;
   private static WLANNetInfo[] _networks = new WLANNetInfo[8];
   private static WLANProfile[] _profiles = new WLANProfile[2];

   private WLAN() {
   }

   public static final void setWLANSystem(WLANSystem system) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      if (appReg != null) {
         if (appReg.replace(6850047726709752304L, system) != null) {
            throw new IllegalStateException("WLAN system already present");
         }

         _system = system;
      }
   }

   public static final WLANSystem getWLANSystem() {
      if (_system == null) {
         ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
         _system = appReg != null ? (WLANSystem)appReg.get(6850047726709752304L) : null;
      }

      return _system;
   }

   public static final boolean isSupported() {
      return (RadioInfo.getSupportedWAFs() & 4) != 0;
   }

   public static final boolean isWLANAllowed() {
      if (!isSupported()) {
         return false;
      }

      WLANSystem system = getWLANSystem();
      return system != null && system.isWLANAllowed();
   }

   public static final void setWLANOverride(boolean wlanOverride) {
      WLANSystem system = getWLANSystem();
      if (system != null) {
         system.setWLANOverride(wlanOverride);
      }
   }

   public static final boolean isRadioOn() {
      WLANSystem system = getWLANSystem();
      return system != null ? system.isWLANRadioOn() : false;
   }

   public static final native int getSupportedAuthModes();

   public static final native int getRequiredCredentials(int var0);

   public static final native int getInnerAuthModes(int var0);

   public static final native int getAuthCategories(int var0);

   public static final native int getAuthModes(int var0);

   public static final native byte[] getMACAddress();

   public static final String isAssociated() {
      WLANSystem system = getWLANSystem();
      return system != null ? system.getActiveProfileSSID() : null;
   }

   public static final String bssidToString(byte[] bssid) {
      String macAddress = null;
      if (bssid != null) {
         try {
            StringBuffer macStringBuffer = new StringBuffer(bssid.length * 3);

            for (int i = 0; i < bssid.length; i++) {
               if (macStringBuffer.length() > 1) {
                  macStringBuffer.append(':');
               }

               NumberUtilities.appendNumber(macStringBuffer, bssid[i], 16, 2);
            }

            return macStringBuffer.toString().toUpperCase();
         } catch (Exception uoe) {
            macAddress = null;
         }
      }

      return macAddress;
   }

   public static final native byte[] getBSSID();

   public static final native WLANExtendedNetInfo getExtendedWLANNetworkInfo();

   public static final native int scanForNetworks();

   public static final int getNumberOfAvailableNetworks() {
      return getNumberOfAvailableNetworks(-1);
   }

   public static final native int getNumberOfAvailableNetworks(int var0);

   public static final int getAvailableNetworks(WLANNetInfo[] networksInfo, int offset, int start, int length) {
      return getAvailableNetworks(-1, networksInfo, offset, start, length);
   }

   public static final int getAvailableNetworks(int handle, WLANNetInfo[] networksInfo, int offset, int start, int length) {
      if (networksInfo == null) {
         return -2;
      }

      if (offset >= 0 && start >= 0 && length >= 0 && offset + length <= networksInfo.length) {
         synchronized (_networks) {
            int retCode = 1;
            int size = 0;

            while (length > 0) {
               size = Math.min(_networks.length, length);

               for (int i = size - 1; i >= 0; i--) {
                  _networks[i] = null;
               }

               retCode = getAvailableNetworksInternal(handle, _networks, 0, start, size);
               if (retCode != 1) {
                  break;
               }

               System.arraycopy(_networks, 0, networksInfo, offset, size);
               length -= size;
               offset += size;
               start += size;
            }

            for (int i = _networks.length - 1; i >= 0; i--) {
               _networks[i] = null;
            }

            return retCode;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   private static final native int getAvailableNetworksInternal(int var0, WLANNetInfo[] var1, int var2, int var3, int var4);

   public static final WLANProfile[] getAvailableProfiles(int handle) {
      int number = getNumberOfAvailableProfiles(handle);
      if (number > 0 && number <= 32) {
         WLANProfile[] profiles = new WLANProfile[number];
         if (getAvailableProfiles(handle, profiles, 0, 0, profiles.length) == 1) {
            return profiles;
         }
      }

      return null;
   }

   private static final int getAvailableProfiles(int handle, WLANProfile[] profiles, int offset, int start, int length) {
      if (profiles == null) {
         return -2;
      }

      if (offset >= 0 && start >= 0 && length >= 0 && offset + length <= profiles.length) {
         synchronized (_profiles) {
            int retCode = 1;
            int size = 0;

            while (length > 0) {
               size = Math.min(_profiles.length, length);

               for (int i = size - 1; i >= 0; i--) {
                  _profiles[i] = null;
               }

               retCode = getAvailableProfilesInternal(handle, _profiles, 0, start, size);
               if (retCode != 1) {
                  break;
               }

               System.arraycopy(_profiles, 0, profiles, offset, size);
               length -= size;
               offset += size;
               start += size;
            }

            for (int i = _profiles.length - 1; i >= 0; i--) {
               _profiles[i] = null;
            }

            return retCode;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   private static final native int getNumberOfAvailableProfiles(int var0);

   private static final native int getAvailableProfilesInternal(int var0, WLANProfile[] var1, int var2, int var3, int var4);

   public static final native String getChallengePrompt(int var0);

   public static final native int setChallengeResponse(int var0, String var1);

   public static final native byte[] getRecord(int var0);

   public static final native int setRecord(int var0, byte[] var1);

   public static final native int setup(int var0, byte[] var1);

   private static final native boolean isWLANVersionSupported();
}
