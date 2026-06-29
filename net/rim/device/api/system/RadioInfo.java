package net.rim.device.api.system;

import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.SE13NetworkTable;
import net.rim.device.internal.timesync.TimeSync;

public final class RadioInfo {
   public static final int WAF_3GPP = 1;
   public static final int WAF_CDMA = 2;
   public static final int WAF_IDEN = 8;
   public static final int WAF_WLAN = 4;
   public static final int NETWORK_NONE = 8;
   public static final int NETWORK_MOBITEX = 1;
   public static final int NETWORK_DATATAC = 2;
   public static final int NETWORK_GPRS = 3;
   public static final int NETWORK_CDMA = 4;
   public static final int NETWORK_IDEN = 5;
   public static final int NETWORK_802_11 = 6;
   public static final int NETWORK_UMTS = 7;
   public static final int STATE_OFF = 0;
   public static final int STATE_ON = 1;
   public static final int STATE_LOWBATT = 2;
   public static final int STATE_SERVICE_REQUIRED = 4;
   public static final int STATE_TURNING_OFF = 6;
   public static final int STATE_TURNING_ON = 5;
   public static final int STATE_RELOAD_REQUIRED = 8;
   public static final int LEVEL_NO_COVERAGE = -256;
   public static final int NETWORK_SERVICE_EMERGENCY_ONLY = 1;
   public static final int NETWORK_SERVICE_VOICE = 2;
   public static final int NETWORK_SERVICE_DATA = 4;
   public static final int NETWORK_SERVICE_DIRECT_CONNECT = 32;
   public static final int NETWORK_SERVICE_ROAMING = 8;
   public static final int NETWORK_SERVICE_SUPPRESS_ROAMING = 512;
   public static final int NETWORK_SERVICE_ROAMING_OFF_CAMPUS = 16;
   public static final int NETWORK_SERVICE_IN_HOME_ZONE = 64;
   public static final int NETWORK_SERVICE_IN_CITY_ZONE = 128;
   public static final int NETWORK_SERVICE_E911_CALLBACK_MODE = 256;
   public static final int NETWORK_SERVICE_EVDO = 1024;
   public static final int NETWORK_SERVICE_EVDO_ONLY = 8192;
   public static final int NETWORK_SERVICE_EDGE = 1024;
   public static final int NETWORK_SERVICE_MODEM_MODE_ENABLED = 2048;
   public static final int NETWORK_SERVICE_UMTS = 4096;
   public static final int NETWORK_SERVICE_GAN = 16384;
   public static final int NETWORK_SERVICE_SIMULTANEOUS_VOICE_AND_DATA = 32768;
   public static final int BAND_MOBITEX_900 = 1;
   public static final int BAND_DATATAC_800 = 2;
   public static final int BAND_GSM_900 = 4;
   public static final int BAND_GSM_1800 = 8;
   public static final int BAND_GSM_1900 = 16;
   public static final int BAND_CDMA_800 = 32;
   public static final int BAND_CDMA_1900 = 64;
   public static final int BAND_IDEN_800 = 128;
   public static final int BAND_GSM_850 = 256;
   public static final int BAND_UMTS_2100 = 512;
   private static final int HWE_BAND_900 = 0;
   private static final int HWE_BAND_1800 = 1;
   private static final int HWE_BAND_1900 = 2;
   private static final int HWE_BAND_850 = 3;
   private static final int HWE_BAND_2100 = 4;
   private static final int HWE_BAND_LAST = 15;

   private RadioInfo() {
   }

   public static final native int getSupportedWAFs();

   private static final int getDefaultWAF() {
      int wafs = getSupportedWAFs();
      if ((wafs & 2) != 0) {
         return 2;
      } else if ((wafs & 1) != 0) {
         return 1;
      } else if ((wafs & 8) != 0) {
         return 8;
      } else {
         return (wafs & 4) != 0 ? 4 : 0;
      }
   }

   public static final boolean areWAFsSupported(int WAFs) {
      return (getSupportedWAFs() & WAFs) != 0;
   }

   public static final native int getActiveWAFs();

   public static final native int getEnabledWAFs();

   public static final native int getNetworkType();

   public static final native int getState();

   public static final int getSignalLevel() {
      return getSignalLevel(getActiveWAFs() & -5);
   }

   public static final native int getSignalLevel(int var0);

   public static final native int getNetworkService();

   public static final int getSupportedBands() {
      int wafs = getSupportedWAFs();
      int bands = 0;
      if ((wafs & 2) != 0) {
         bands |= 96;
      }

      if ((wafs & 8) != 0) {
         bands |= 128;
      }

      if ((wafs & 1) != 0) {
         int osBands = getNetworkBands();
         if ((osBands & 8) != 0) {
            bands |= 256;
         }

         if ((osBands & 1) != 0) {
            bands |= 4;
         }

         if ((osBands & 2) != 0) {
            bands |= 8;
         }

         if ((osBands & 4) != 0) {
            bands |= 16;
         }

         if ((osBands & 16) != 0) {
            bands |= 512;
         }
      }

      return bands;
   }

   private static final native int getNetworkBands();

   public static final native long getNumberOfPacketsSent();

   public static final native long getNumberOfPacketsReceived();

   public static final native int getNumberOfNetworks();

   public static final native int getCurrentNetworkIndex();

   public static final int getNetworkId(int index) {
      int networkId = getNetworkIdFromOS(index);
      return convertNetworkId(networkId);
   }

   private static final native int getNetworkIdFromOS(int var0);

   public static final String getNetworkName(int index) {
      String networkName = null;
      if (!DeviceInfo.isSimulator() && (getActiveWAFs() & 1) != 0) {
         SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
         if (se13NetTable != null) {
            return se13NetTable.getNetworkName(getNetworkId(index));
         }
      } else {
         networkName = getNetworkNameFromOS(index);
      }

      return networkName;
   }

   private static final native String getNetworkNameFromOS(int var0);

   public static final String getNetworkCountryCode(int index) {
      String networkCountryCode = null;
      if (!DeviceInfo.isSimulator() && (getActiveWAFs() & 1) != 0) {
         SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
         if (se13NetTable != null) {
            return se13NetTable.getCountryInitials((short)(getNetworkId(index) & 65535));
         }
      } else {
         networkCountryCode = getNetworkCountryCodeFromOS(index);
      }

      return networkCountryCode;
   }

   private static final native String getNetworkCountryCodeFromOS(int var0);

   public static final int convertNetworkId(int networkId) {
      if (!DeviceInfo.isSimulator() && (getActiveWAFs() & 1) != 0 && (networkId & 983040) == 983040) {
         SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
         if (se13NetTable != null) {
            if (se13NetTable.is3DigitMNC(networkId)) {
               return networkId & -983041;
            }

            networkId = (networkId & 267386880) >>> 4 | networkId & 4095;
         }
      }

      return networkId;
   }

   public static final int calculateNetworkId(int mcc, int mnc) {
      int temp = mcc % 100;
      int hexMCC = mcc / 100 << 8 | temp / 10 << 4;
      hexMCC |= temp % 10;
      temp = mnc % 100;
      int hexMNC = mnc / 100 << 8 | temp / 10 << 4;
      hexMNC |= temp % 10;
      return hexMNC << 16 | hexMCC;
   }

   public static final int getMNC(int index) {
      int netID = getNetworkId(index);
      return (netID & -65536) >> 16;
   }

   public static final int getMCC(int index) {
      int netID = getNetworkId(index);
      return netID & 65535;
   }

   public static final native boolean isPDPContextActive(int var0);

   public static final native String getAccessPointName(int var0);

   public static final int getAccessPointNumber(String name) {
      return getAccessPointNumber(name, 0, name.length());
   }

   public static final native int getAccessPointNumber(String var0, int var1, int var2);

   public static final byte[] getIPAddress(int apnId) {
      return UDPPacketHeader.IPv4IntToByteArray(getIPv4Address(apnId));
   }

   private static final native int getIPv4Address(int var0);

   public static final long getNetworkTime(long deviceTime) {
      return TimeSync.GetNetworkTime(deviceTime);
   }

   public static final boolean isDataServiceOperational() {
      return getState() == 1 && getSignalLevel() != -256 && (getNetworkService() & 4) != 0 && !isDataServiceSuspended();
   }

   public static final boolean isDataServiceSuspended() {
      int networkService = getNetworkService();
      int supportedWAFs = getSupportedWAFs();
      if ((supportedWAFs & 2) != 0 && (networkService & 2048) != 0) {
         return true;
      }

      if ((supportedWAFs & 1) != 0) {
         if ((RadioInternal.get3GPPSupportedRats() & 2) != 0 && (networkService & 4096) != 0) {
            return false;
         }

         if ((networkService & 32768) != 0) {
            return false;
         }
      }

      return (supportedWAFs & 4) != 0 && WLAN.isRadioOn() && WLAN.isAssociated() != null ? false : Phone.isSupported() && Phone.getInstance().isActive();
   }

   public static final String getCurrentNetworkName() {
      int index = getCurrentNetworkIndex();
      return index != -1 ? getNetworkName(index) : null;
   }
}
