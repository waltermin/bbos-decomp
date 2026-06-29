package net.rim.device.api.system;

import net.rim.device.api.util.NumberUtilities;

public final class WLAN {
   public static String WLAN_PSEUDO_APN = "MagicRudyAPN.rim";
   public static final int WLAN_MAX_SSID_SIZE;
   public static final int WLAN_MAC_ADDRESS_SIZE;
   public static final int WLAN_NUM_WEP_KEYS;
   public static final int WLAN_MAX_DOMAIN_SUFFIX_LEN;
   public static final int WLAN_MAX_PROFILES;
   public static final int WLAN_MAX_PROFILE_SETS;
   public static final int WLAN_BAND_NONE;
   public static final int WLAN_BAND_BG;
   public static final int WLAN_BAND_A;
   public static final int WLAN_BAND_B;
   public static final int WLAN_BAND_ALL;
   public static final int WLAN_NETWORK_IBSS;
   public static final int WLAN_NETWORK_ESS;
   public static final int WLAN_AUTHENT_CAT_NONE;
   public static final int WLAN_AUTHENT_CAT_OPEN;
   public static final int WLAN_AUTHENT_CAT_WEP;
   public static final int WLAN_AUTHENT_CAT_8021X;
   public static final int WLAN_AUTHENT_CAT_WPA_PERSONAL;
   public static final int WLAN_AUTHENT_CAT_WPA_ENTERPRISE;
   public static final int WLAN_AUTHENT_CAT_WPA2_PERSONAL;
   public static final int WLAN_AUTHENT_CAT_WPA2_ENTERPRISE;
   public static final int WLAN_AUTHENT_NONE;
   public static final int WLAN_AUTHENT_OPEN;
   public static final int WLAN_AUTHENT_WEP;
   public static final int WLAN_AUTHENT_WPAPSK;
   public static final int WLAN_AUTHENT_EAPLEAP;
   public static final int WLAN_AUTHENT_EAPPEAP;
   public static final int WLAN_AUTHENT_EAPTLS;
   public static final int WLAN_AUTHENT_EAPFAST;
   public static final int WLAN_AUTHENT_EAPTTLS;
   public static final int WLAN_AUTHENT_EAPSIM;
   public static final int WLAN_AUTHENT_EAPAKA;
   public static final int WLAN_AUTHENT_EAPWPS;
   public static final int WLAN_AUTHENT_EAPMSCHAPV2;
   public static final int WLAN_AUTHENT_EAPGTC;
   public static final int WLAN_AUTHENT_PAP;
   public static final int WLAN_AUTHENT_CHAP;
   public static final int WLAN_AUTHENT_MSCHAP;
   public static final int WLAN_AUTHENT_MSCHAPV2;
   public static final int WLAN_AUTHENT_EAPMD5;
   public static final int WLAN_AUTHENT_AUTO;
   public static final int WLAN_CRED_NONE;
   public static final int WLAN_CRED_WEPKEY;
   public static final int WLAN_CRED_WPAPSK;
   public static final int WLAN_CRED_USERNAME;
   public static final int WLAN_CRED_PASSWORD;
   public static final int WLAN_CRED_CA_CERT;
   public static final int WLAN_CRED_CLIENT_CERT;
   public static final int WLAN_CRED_INNERAUTH;
   public static final int WLAN_CRED_TOKENSERIAL;
   public static final int WLAN_CRED_IMSI;
   public static final int WLAN_CRED_SERVER_SUBJECT;
   public static final int WLAN_CRED_SERVER_SAN;
   public static final int WLAN_CRED_PAC;
   public static final int WLAN_RET_SUCCESS;
   public static final int WLAN_RET_NOT_ASSOCIATED;
   public static final int WLAN_RET_NULL_POINTER_ARGUMENT;
   public static final int WLAN_RET_BUFFER_TOO_SMALL;
   public static final int WLAN_RET_UNSUPPORTED;
   public static final int WLAN_RET_RADIO_IS_OFF;
   public static final int WLAN_RET_MEM_ALLOC_FAIL;
   public static final int WLAN_RET_PARAMETER_OUT_OF_RANGE;
   public static final int WLAN_RET_DEPRECATED_API;
   public static final int WLAN_RET_RADIO_FAILURE;
   public static final int WLAN_RET_CANNOT_CREATE_PROFILE_SET;
   public static final int WLAN_RET_INVALID_PROFILE_SET;
   public static final int WLAN_RET_NOT_REGISTERED;
   public static final int WLAN_RET_DUPLICATE_PROFILE;
   public static final int WLAN_RET_MAX_PROFILES_REACHED;
   public static final int WLAN_RET_EVENT_TABLE_FULL;
   public static final int WLAN_RET_INVALID_PROFILEID;
   public static final int WLAN_RET_CHALLENGE_NOT_AVAIL;
   public static final int WLAN_RET_RECORD_NOT_AVAIL;
   public static final long GUID_WLAN_NET_CONNECTIVITY;
   public static final long GUID_WLAN_CONFIGURATION_CHANGED;
   private static final long ID_WLAN_SYSTEM;
   public static final int WLAN_EVENT_RADIO_STARTED;
   public static final int WLAN_EVENT_RADIO_STOPPED;
   public static final int WLAN_EVENT_NETWORK_AP_CHANGE;
   public static final int WLAN_EVENT_NETWORK_FOUND;
   public static final int WLAN_EVENT_NETWORK_CONNECTED;
   public static final int WLAN_EVENT_NETWORK_DISCONNECTED;
   public static final int WLAN_EVENT_NETWORK_CHALLENGE;
   public static final int WLAN_EVENT_EXTENDED_INFO_UPDATE;
   public static final int WLAN_FLAG_USE_DHCP;
   public static final int WLAN_FLAG_AP_TO_AP_HANDOVER_ALLOWED;
   public static final int WLAN_FLAG_GAN_ALLOWED;
   public static final int WLAN_FLAG_AES_CCMP_ONLY;
   public static final int WLAN_FLAG_NO_PSEUDONYMS;
   public static final int WLAN_FLAG_NO_SERV_CERT_REALM_CHECK;
   public static final int WLAN_FLAG_PREFIX_SSID;
   public static final int WLAN_WPS_PIN_ENABLED;
   public static final int WLAN_WPS_PBC_ENABLED;
   public static final int WLAN_WPS_PIN_READY;
   public static final int WLAN_WPS_PBC_READY;
   public static final int WLAN_PAC_ADHP_DISABLE;
   public static final int WLAN_PAC_CDHP_ENABLED;
   public static final int WLAN_NO_SERV_CERT_VALIDATION;
   public static final int WLAN_SMART_CARD_SIGNING;
   public static final int WLAN_WPS_OTHER_READY;
   public static final int WLAN_NETWORK_STATE_NONE;
   public static final int WLAN_NETWORK_STATE_NA;
   public static final int WLAN_NETWORK_STATE_TRYING;
   public static final int WLAN_NETWORK_STATE_SUCCESS;
   public static final int WLAN_NETWORK_STATE_ERROR;
   public static final int WLAN_NETWORK_SIGQUAL_NONE;
   public static final int WLAN_NETWORK_SIGQUAL_VERYPOOR;
   public static final int WLAN_NETWORK_SIGQUAL_POOR;
   public static final int WLAN_NETWORK_SIGQUAL_AVERAGE;
   public static final int WLAN_NETWORK_SIGQUAL_GOOD;
   public static final int WLAN_NETWORK_CIPHER_INVALID;
   public static final int WLAN_NETWORK_CIPHER_NONE;
   public static final int WLAN_NETWORK_CIPHER_WEP40;
   public static final int WLAN_NETWORK_CIPHER_WEP104;
   public static final int WLAN_NETWORK_CIPHER_TKIP;
   public static final int WLAN_NETWORK_CIPHER_CCMP;
   public static final int WLAN_RECORD_NONE;
   public static final int WLAN_RECORD_DEVICE_PAC_FILE;
   public static final int WLAN_RECORD_SERVER_PAC_FILE;
   public static final int WLAN_RECORD_WLAN_LOG;
   public static final int WLAN_NETWORK_PROMPT_PASSWORD;
   public static final int WLAN_NETWORK_PROMPT_NEW_PASSWORD;
   public static final int WLAN_NETWORK_PROMPT_CHALLENGE;
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
