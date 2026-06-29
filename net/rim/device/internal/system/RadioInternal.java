package net.rim.device.internal.system;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.QOSInfo;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioPacketHeader;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;
import net.rim.vm.TraceBack;

public final class RadioInternal {
   public static final int NETWORK_MODE_UMTS = 0;
   public static final int NETWORK_MODE_GPRS = 1;
   public static final int NETWORK_MODE_DUAL = 2;
   public static final int NETWORK_MODE_UMTS_ENABLED = 1;
   public static final int NETWORK_MODE_GPRS_ENABLED = 2;
   public static final int NETWORK_MODE_DUAL_ENABLED = 3;
   public static final int NETWORK_SELECTION_MODE_AUTOMATIC = 0;
   public static final int NETWORK_SELECTION_MODE_AUTOMATIC_A = 1;
   public static final int NETWORK_SELECTION_MODE_AUTOMATIC_B = 2;
   public static final int NETWORK_SELECTION_MODE_CUSTOM = 4;
   public static final int NETWORK_SELECTION_MODE_MANUAL = 3;
   public static final int NETWORK_SELECTION_MODE_HOME_ONLY = 5;
   public static final int NETWORK_SELECTION_MODE_CDMA_ONLY = 7;
   public static final int NETWORK_SELECTION_MODE_EVDO_ONLY = 6;
   public static final int NETWORK_SELECTION_MODE_ROAM_ONLY = 8;
   public static final int NETWORK_SELECTION_MODE_ENABLED_AUTOMATIC = 1;
   public static final int NETWORK_SELECTION_MODE_ENABLED_AUTOMATIC_A = 2;
   public static final int NETWORK_SELECTION_MODE_ENABLED_AUTOMATIC_B = 4;
   public static final int NETWORK_SELECTION_MODE_ENABLED_CUSTOM = 16;
   public static final int NETWORK_SELECTION_MODE_ENABLED_MANUAL = 8;
   public static final int NETWORK_SELECTION_MODE_ENABLED_HOME_ONLY = 32;
   public static final int NETWORK_SELECTION_MODE_ENABLED_CDMA_ONLY = 128;
   public static final int NETWORK_SELECTION_MODE_ENABLED_EVDO_ONLY = 64;
   public static final int NETWORK_SELECTION_MODE_ENABLED_ROAM_ONLY = 256;
   public static final int NETWORK_CATEGORY_REGISTERED = 1;
   public static final int NETWORK_CATEGORY_FORBIDDEN = 2;
   public static final int NETWORK_CATEGORY_HOME = 4;
   public static final int NETWORK_CATEGORY_WEAK = 8;
   public static final int NETWORK_CATEGORY_GPRS_SUPPORT = 16;
   public static final int NETWORK_CATEGORY_PREFERRED = 32;
   public static final int NETWORK_CATEGORY_UMTS_SUPPORT = 64;
   public static final int NETWORK_TYPE_OPERATOR_PREFERRED = 256;
   public static final int NETWORK_TYPE_USER_PREFERRED = 128;
   public static final int GAN_PREFERENCE_CELL_ONLY = 0;
   public static final int GAN_PREFERENCE_CELL_PREFERRED = 1;
   public static final int GAN_PREFERENCE_GAN_ONLY = 2;
   public static final int GAN_PREFERENCE_GAN_PREFERRED = 3;
   private static final long GAN_PREFERENCE_KEY = 1018153016358681871L;
   private static final int _ganPreferenceKeyId = PersistentInteger.getId(1018153016358681871L, 0);
   private static final long ENABLED_RATS_KEY = 9126810432002666849L;
   private static final int _enabled3GPPRatsKeyId = PersistentInteger.getId(9126810432002666849L, 0);
   private static int _activeRadios;
   public static final int INVALID_NETWORK_ID = -1;
   private static final long MANUALLY_SELECTED_NETWORK_KEY = -2180753184139556852L;
   private static final int _manuallySelectedNetworkKeyId = PersistentInteger.getId(-2180753184139556852L, -1);
   private static SIMCardEfHandler _efHandler;
   public static final int PRIMARY_DNS_ADDRESS = 1;
   public static final int SECONDARY_DNS_ADDRESS = 2;
   public static final int PACKET_ERROR_APP_NOT_REGISTERED = -1;
   public static final int PACKET_ERROR_PACKET_NOT_FOUND = -2;
   public static final int PACKET_ERROR_NO_FREE_BUFFERS = -3;
   public static final int PACKET_ERROR_BAD_TAG = -5;
   public static final int PACKET_ERROR_GENERAL = -6;
   public static final int PACKET_ERROR_BAD_HEADER = -9;
   public static final int PACKET_ERROR_BAD_LENGTH = -10;
   public static final int PACKET_ERROR_BUFFER_TOO_SMALL = -11;
   public static final int PACKET_ERROR_APN_NOT_REGISTERED = -12;
   public static final int PACKET_ERROR_NOT_APN_OWNER = -13;
   public static final int PACKET_ERROR_APN_INVALID = -14;
   private static final long PLMN_SELECTION_MODE_KEY = -3351250463113767944L;
   private static final int _plmnSelectionModeID = PersistentInteger.getId(-3351250463113767944L, 0);
   public static final int PROTOCOL_NUM_TCP = 6;
   public static final int PROTOCOL_NUM_UDP = 17;
   public static final int RADIO_PORTS_UNDEFINED_PROTO_NUM = -100;
   public static final int RADIO_PORTS_ALREADY_REGISTERED = -101;
   public static final int RADIO_PORTS_OUT_OF_RANGE = -102;
   public static final int RADIO_PORTS_RESERVED = -103;
   public static final int RADIO_PORTS_TOO_MANY_REGISTERED = -104;
   public static final int RADIO_PORTS_NOT_REGISTERED = -105;
   public static final int RADIO_PORTS_INVALID_OP = -106;
   public static final int RADIO_PORTS_OP_DEREGISTER = 0;
   public static final int RADIO_PORTS_OP_REGISTER = 1;
   public static final int RADIO_PORTS_OP_QUERY = 2;
   public static final int RADIO_PARAM_SUBNET_MASK = 1;
   public static final int RADIO_PARAM_DEFAULT_GATEWAY = 3;
   public static final int RADIO_PARAM_DNS_SERVERS = 6;
   public static final int RADIO_PARAM_DNS_DOMAIN_NAME = 15;
   public static final int RADIO_PARAM_NTP_SERVERS = 42;
   public static final int RADIO_PARAM_HOSTNAME = 12;
   public static final int RADIO_PARAM_UDP_PAYLOAD_MTU = 100;
   public static final int RADIO_PARAM_UDP_PAYLOAD_MRU = 101;
   public static final int RADIO_PARAM_TCP_PAYLOAD_MTU = 102;
   public static final int RADIO_PARAM_TCP_PAYLOAD_MRU = 103;
   public static final int SMS_ROUTE_PACKET_SWITCHED = 0;
   public static final int SMS_ROUTE_CIRCUIT_SWITCHED = 1;
   public static final int SMS_ROUTE_PACKET_SWITCHED_PREFERRED = 2;
   public static final int SMS_ROUTE_CIRCUIT_SWITCHED_PREFERRED = 3;
   public static final int SRVPGM_LOCK_READ = 0;
   public static final int SRVPGM_BASIC_DATA_READ = 1;
   public static final int SRVPGM_ADVANCED_DATA_READ = 2;
   public static final int SRVPGM_GET_BASIC_DATA = 3;
   public static final int SRVPGM_GET_ADVANCED_DATA = 4;
   public static final int SRVPGM_BASIC_DATA_WRITE = 5;
   public static final int SRVPGM_ADVANCED_DATA_WRITE = 6;
   public static final int SRVPGM_CONFIG_READ = 7;
   public static final int SRVPGM_CONFIG_WRITE = 8;
   public static final int SRVPGM_INVALID_INPUT = 0;
   public static final int SRVPGM_OPERATION_OK = 1;
   public static final int SRVPGM_OPERATION_WRONG = 2;
   public static final int BB_XP_DISABLED = 0;
   public static final int BB_XP_ENABLED = -1;
   public static final int DATA_CALL_GO_ACTIVE = 112;
   public static final int IOTA_MESSAGE_TYPE_IS683 = 0;
   public static final int IOTA_MESSAGE_TYPE_PRLBIN = 1;
   public static final int IOTA_MESSAGE_TYPE_NAMBIN = 2;
   public static final int IOTA_MESSAGE_TYPE_AKEYBIN = 3;
   public static final int RETURN_REQUEST_TYPE_SAVE = 0;
   public static final int RETURN_REQUEST_TYPE_RESET = 1;
   public static final int RETURN_REQUEST_INVALID_INPUT = 0;
   public static final int RETURN_REQUEST_OPERATION_OK = 1;
   public static final int RETURN_REQUEST_OPERATION_FAILED = 2;
   public static final int NV_STRING_VOICEMAIL_NUMBER = 1;
   public static final int NV_STRING_VOICEMAIL_NUMBER_2 = 7;
   public static final int NV_STRING_EMERGENCY_NUMBER = 2;
   public static final int NV_STRING_NATIONAL_CODE_NUMBER = 3;
   public static final int NV_STRING_INTERNATIONAL_CODE_NUMBER = 4;
   public static final int NV_BOOLEAN_USE_NATIONAL_CODE_FOR_VOICE = 5;
   public static final int NV_BOOLEAN_USE_NATIONAL_CODE_FOR_SMS = 6;
   public static final int RAT_3GPP_GERAN = 1;
   public static final int RAT_3GPP_UTRAN = 2;
   public static final int RAT_3GPP_GAN = 4;
   public static final int RAT_PREFERENCE_UNSPECIFIED = 0;
   public static final int RAT_PREFERENCE_3GPP_CELLULAR_GAN = 1;
   public static final int RAT_PREFERENCE_3GPP_GAN_CELLULAR = 2;
   public static final int RADIO_3GPP = 1;
   public static final int RADIO_CDMA = 2;
   public static final int RADIO_WLAN = 4;
   public static final int RADIO_IDEN = 8;

   public static final native int serviceProgramSetup(int var0, byte[] var1);

   public static final native int setup(int var0, int var1);

   public static final native boolean requestMasterReset(byte[] var0);

   public static final native int simulTCPCommand(int var0, int var1, int var2, int var3, int var4);

   public static final native int getBlackBerryExperienceMode();

   public static final native void setBlackBerryExperienceMode(int var0);

   public static final native int gprsAttach(boolean var0);

   public static final native void smsStoreOnSIM(boolean var0);

   public static final native void smsCountUpdated(int var0);

   public static final native void smsSetRoute(int var0);

   public static final native void enableIPModem(boolean var0);

   public static final native boolean processOTASPMessage(int var0, byte[] var1);

   public static final native byte[] getNAI(int var0);

   public static final native int getPatriotSoftwareVersion();

   public static final native int processReturnRequest(int var0);

   public static final native String readNVString(int var0);

   public static final native boolean readNVBoolean(int var0);

   public static final void dataCallGoActive() {
      if (RadioInfo.getNetworkType() == 7) {
         setup(112, 0);
      }
   }

   public static final native void setDataServiceMode(int var0);

   public static final native int getDataServiceMode();

   public static final native int sendICMPTestData(int var0, int var1, int var2, int var3);

   public static final native int sendLLCTestData(int var0, int var1, int var2);

   public static final native int get3GPPSupportedRats();

   public static final int get3GPPEnabledRats() {
      return PersistentInteger.get(_enabled3GPPRatsKeyId);
   }

   public static final void set3GPPEnabledRats(int rats) {
      PersistentInteger.set(_enabled3GPPRatsKeyId, rats);
   }

   public static final int get3GPPActiveRats() {
      int ganPreference = getGANPreference();
      int active3GPPRats = get3GPPEnabledRats();
      switch (ganPreference) {
         case 0:
            return active3GPPRats & -5;
         case 2:
            active3GPPRats &= 4;
         default:
            return active3GPPRats;
      }
   }

   public static final int get3GPPRATPreference(int ganPreference) {
      int preference = 0;
      int enabled3GPPRats = get3GPPEnabledRats();
      switch (ganPreference) {
         case 1:
            if ((enabled3GPPRats & 4) != 0 && (enabled3GPPRats & -5) != 0) {
               return 1;
            }
            break;
         case 3:
            if ((enabled3GPPRats & 4) != 0 && (enabled3GPPRats & -5) != 0) {
               preference = 2;
            }
      }

      return preference;
   }

   public static final void set3GPPRatConfig(int active3GPPRats, int ratPreference) {
      try {
         if (active3GPPRats != 0) {
            set3GPPRatConfiguration(active3GPPRats, ratPreference);
            return;
         }
      } catch (Exception e) {
         System.out.println("set3GPPRatConfiguration failed: " + Integer.toString(active3GPPRats) + ", " + Integer.toString(ratPreference));
      }
   }

   public static final native boolean set3GPPRatConfiguration(int var0, int var1);

   public static final int getSupportedRadios() {
      return translateWAFsToRadios(RadioInfo.getSupportedWAFs());
   }

   public static final boolean setEnabledRadios(int radios) {
      return Radio.setEnabledWAFs(translateRadiosToWAFs(radios));
   }

   public static final int getEnabledRadios() {
      return translateWAFsToRadios(RadioInfo.getEnabledWAFs());
   }

   private static final int translateRadiosToWAFs(int radios) {
      int wafs = 0;
      if ((radios & 1) != 0) {
         wafs |= 1;
      }

      if ((radios & 2) != 0) {
         wafs |= 2;
      }

      if ((radios & 4) != 0) {
         wafs |= 4;
      }

      if ((radios & 8) != 0) {
         wafs |= 8;
      }

      return wafs;
   }

   private static final int translateWAFsToRadios(int wafs) {
      int radios = 0;
      if ((wafs & 1) != 0) {
         radios |= 1;
      }

      if ((wafs & 2) != 0) {
         radios |= 2;
      }

      if ((wafs & 4) != 0) {
         radios |= 4;
      }

      if ((wafs & 8) != 0) {
         radios |= 8;
      }

      return radios;
   }

   public static final int getActiveRadios() {
      int activeRadios = 0;
      int activeWafs = RadioInfo.getActiveWAFs();
      if ((activeWafs & 1) != 0 && (get3GPPActiveRats() & get3GPPSupportedRats() & -5) != 0) {
         activeRadios |= 1;
      }

      if ((activeWafs & 2) != 0) {
         activeRadios |= 2;
      }

      if ((activeWafs & 4) != 0) {
         activeRadios |= 4;
      }

      if ((activeWafs & 8) != 0) {
         activeRadios |= 8;
      }

      return activeRadios;
   }

   public static final boolean reactivateRadios() {
      return activateRadios(_activeRadios);
   }

   public static final boolean activateRadios(int radios) {
      int wafs = translateRadiosToWAFs(radios);
      if (radios == 0) {
         return true;
      }

      _activeRadios = radios;
      return Radio.activateWAFs(wafs);
   }

   public static final native void activateWAFsInternal(int var0);

   public static final native void deactivateWAFsInternal(int var0);

   public static final void deactivateRadios(int radios) {
      Radio.deactivateWAFs(translateRadiosToWAFs(radios));
      _activeRadios &= ~radios;
   }

   public static final int sendPacket(RadioPacketHeader header, byte[] data) {
      return sendPacket(header, data, 0, data.length);
   }

   public static final native int sendPacket(RadioPacketHeader var0, byte[] var1, int var2, int var3);

   public static final int registerAccessPointNumber(String name) {
      return registerAccessPointNumber(name, 0, name == null ? 0 : name.length(), null);
   }

   public static final int registerAccessPointNumber(String data, int offset, int length) {
      return registerAccessPointNumber(data, offset, length, null);
   }

   public static final int registerAccessPointNumber(String data, int offset, int length, QOSInfo qos) {
      return registerAccessPointNumber(data, offset, length, null, qos, null, null);
   }

   public static final int registerAccessPointNumber(String data, int offset, int length, QOSInfo qos, String username, String password) {
      return registerAccessPointNumber(data, offset, length, null, qos, username, password);
   }

   public static final native int registerAccessPointNumber(String var0, int var1, int var2, byte[] var3, QOSInfo var4, String var5, String var6);

   public static final native void deregisterAccessPointNumber(int var0);

   public static final byte[] getDNSIPAddress(int apnId, int type) {
      int addr = getIPv4DNSAddress(apnId, type);
      return addr != 0 ? UDPPacketHeader.IPv4IntToByteArray(addr) : null;
   }

   public static final native void getDefaultSMSParameters(SMSParameters var0);

   public static final native void setDefaultSMSParameters(SMSParameters var0);

   public static final native void scanForNetworks();

   public static final native void abortScanForNetworks();

   public static final native int getAvailableNetworkSelectionModes();

   public static final int getNetworkSelectionMode() {
      switch (RadioInfo.getNetworkType()) {
         case 4:
         case 7:
            return getNetworkSelectionModeOS();
         default:
            return PersistentInteger.get(_plmnSelectionModeID);
      }
   }

   public static final native int getAvailableNetworkModes();

   public static final void setNetworkSelectionMode(int mode) {
      PersistentInteger.set(_plmnSelectionModeID, mode);
      setNetworkSelectionModeOS(mode);
      if (mode == 3 && SIMCard.isSupported()) {
         if (_efHandler == null) {
            _efHandler = new SIMCardEfHandler();
         }

         if (!_efHandler.isRunning()) {
            try {
               _efHandler.startTask(new DefaultManuallySelectedNetIdReader(), false);
               return;
            } catch (Exception var2) {
            }
         }
      }
   }

   public static final native void setNetworkSelectionModeOS(int var0);

   private static final native int getNetworkSelectionModeOS();

   public static final native int getNetworkMode();

   public static final native void setNetworkMode(int var0);

   public static final int getGANPreference() {
      return PersistentInteger.get(_ganPreferenceKeyId);
   }

   public static final void setGANPreference(int ganPreference) {
      int enabled3GPPRATs = 0;
      switch (ganPreference) {
         case -1:
            return;
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            PersistentInteger.set(_ganPreferenceKeyId, ganPreference);
            if (ganPreference != 2 && (getActiveRadios() & 1) != 0) {
               enabled3GPPRATs |= get3GPPSupportedRats() & -5;
            }

            if (ganPreference != 0 && GAN.isGANAllowed() && (getActiveRadios() & 4) != 0) {
               enabled3GPPRATs |= get3GPPSupportedRats() & 4;
            }

            if (get3GPPEnabledRats() != 0) {
               set3GPPEnabledRats(enabled3GPPRATs);
            }

            set3GPPRatConfig(get3GPPActiveRats(), get3GPPRATPreference(ganPreference));
            if (get3GPPActiveRats() != 0) {
               if ((RadioInfo.getActiveWAFs() & 1) == 0) {
                  activateWAFsInternal(1);
                  return;
               }
            } else if ((RadioInfo.getActiveWAFs() & 1) != 0) {
               deactivateWAFsInternal(1);
            }
      }
   }

   public static final native void setFastDormancy(int var0, boolean var1);

   private static final native int getIPv4DNSAddress(int var0, int var1);

   public static final native void setQos(int var0, QOSInfo var1);

   public static final native void getQos(int var0, QOSInfo var1);

   public static final native int registerPort(int var0, int var1, int var2, int var3);

   private static final native int getNetworkParameter(int var0, int var1, int var2, int var3, byte[] var4, byte[] var5);

   public static final byte[] getNetworkParameter(int apnID, int paramType, int index) {
      if (index >= 0 && apnID >= 0) {
         byte[] data = new byte[1];
         int result = getNetworkParameter(-1, apnID, paramType, index, null, data) - 1;
         if (result >= index && index >= 0 && data[0] > 0) {
            Array.resize(data, data[0] & 255);
            Arrays.zero(data);
            getNetworkParameter(-1, apnID, paramType, index, data, null);
            return data;
         }
      }

      return null;
   }

   public static final int getNetworkParameterListSize(int apnID, int paramType) {
      return apnID >= 0 ? getNetworkParameter(-1, apnID, paramType, 0, null, null) : 0;
   }

   public static final native void reportNetworkDisplayName(String var0, int var1);

   public static final native boolean getServingNetworkNameString(Object var0);

   public static final native int getMaxSMSPacketSize();

   public static final native int getNetworkCategory(int var0);

   public static final native int getNetworkLAC(int var0);

   public static final void changeNetworks(int index) {
      int networkId = RadioInfo.getNetworkId(index);
      changeNetworksOS(index);
      setManuallySelectedNetworkID(networkId);
   }

   public static final native void changeNetworksOS(int var0);

   public static final boolean isUMTSCapable() {
      return RadioInfo.areWAFsSupported(1) && (get3GPPSupportedRats() & 2) != 0;
   }

   public static final boolean isDTMCapable() {
      if (RadioInfo.areWAFsSupported(1)) {
         switch (InternalServices.getHardwareID()) {
            case -2080372477:
            case -2080372473:
            case -2080371965:
            case -2080371961:
            case -2080371453:
            case 67112452:
               return false;
            default:
               return true;
         }
      } else {
         return false;
      }
   }

   public static final int getPrimaryWAF() {
      int supportedWafs = RadioInfo.getSupportedWAFs();
      int primaryWAF;
      if ((supportedWafs & 2) != 0) {
         primaryWAF = 2;
      } else if ((supportedWafs & 1) != 0) {
         primaryWAF = 1;
      } else if ((supportedWafs & 8) != 0) {
         primaryWAF = 8;
      } else {
         primaryWAF = 4;
      }

      byte[] buf = Branding.getData(14336);
      if (buf != null && buf.length > 0) {
         int brandingWAF = buf[0] & 255;
         if (brandingWAF == 2) {
            return 2;
         }

         if (brandingWAF == 1) {
            return 1;
         }

         if (brandingWAF == 4) {
            return 8;
         }

         if (brandingWAF == 3) {
            primaryWAF = 4;
         }
      }

      return primaryWAF;
   }

   public static final native void enableDTM(boolean var0);

   public static final native boolean isDTMEnabled();

   public static final native void setAGPSOptions(boolean var0, boolean var1);

   public static final native boolean isSUPLEnabled();

   public static final boolean isSIMCardPresent() {
      boolean simCardIsPresent = false;

      try {
         return SIMCard.isSupported() && SIMCard.isValid();
      } catch (SIMCardException var2) {
         return simCardIsPresent;
      }
   }

   public static final void setManuallySelectedNetworkID(int networkId) {
      if (getNetworkSelectionMode() == 3) {
         PersistentInteger.set(_manuallySelectedNetworkKeyId, networkId);
      }
   }

   public static final int getManuallySelectedNetworkID() {
      int networkId = -1;
      if (getNetworkSelectionMode() == 3) {
         networkId = PersistentInteger.get(_manuallySelectedNetworkKeyId);
      }

      return networkId;
   }

   public static final void assertWAFAccessPermission(int WAFs) {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(2), 51)) {
         ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
         if ((WAFs & 4) != 0) {
            ApplicationControl.assertWiFiPermitted(true, CommonResource.getBundle(), 10165);
         }
      }
   }
}
