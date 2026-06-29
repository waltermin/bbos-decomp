package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.RadioInfo;

public final class InternalServices {
   public static final boolean BES_50_SUPPORTED = true;
   public static final int FORM_TACHYON1 = 1;
   public static final int FORM_TACHYON2 = 4;
   public static final int FORM_QUARK = 5;
   public static final int FORM_CHARM = 9;
   public static final int FORM_BBCLIENT = 10;
   public static final int FORM_ELECTRON = 11;
   public static final int FORM_PERIPHERAL = 12;
   public static final int FORM_POSITRON = 13;
   public static final int FORM_GAMMARAY = 14;
   public static final int FORM_MAMABEAR = 15;
   public static final int HW_CAP_SERIAL = 0;
   public static final int HW_CAP_USB = 1;
   public static final int HW_CAP_IRDA = 2;
   public static final int HW_CAP_BUILTIN_HEADSET = 3;
   public static final int HW_CAP_REMOVABLE_BATTERY = 4;
   public static final int HW_CAP_SMARTCARD_READER = 5;
   public static final int HW_CAP_GPS = 6;
   public static final int HW_CAP_LOUD_AUDIO = 7;
   public static final int HW_CAP_BLUETOOTH = 8;
   public static final int HW_CAP_BUZZER = 9;
   public static final int HW_CAP_TRICOLOUR_LED = 11;
   public static final int HW_CAP_RTC_BACKUP_POWER = 13;
   public static final int HW_CAP_NETWORK_TIME = 14;
   public static final int HW_CAP_HEADSET_DETECT = 15;
   public static final int HW_CAP_LIGHT_SENSOR = 16;
   public static final int HW_CAP_SDCARD = 19;
   public static final int HW_CAP_SDCARD_HOT_SWAPPABLE = 20;
   public static final int HW_CAP_CAMERA = 21;
   public static final int HW_CAP_WLAN = 22;
   public static final int HW_CAP_TRACKBALL_LED = 23;
   public static final int HW_CAP_BATTERY_DOOR_DETECT = 24;
   public static final int SW_CAP_POSIX_FS = 0;
   public static final int SW_CAP_STREAMING = 1;
   public static final int SW_CAP_AUDIO_STREAMING = 2;
   public static final int SW_CAP_NO_KEY_TONE = 4;
   public static final int SW_CAP_MEDIA_PLAYER = 8;
   public static final int SW_CAP_VAD = 11;
   public static final int SW_CAP_RECORDING = 12;
   public static final int SW_CAP_VIDEO_STREAMING = 7;
   public static final int SW_CAP_RTSP = 13;
   public static final int SW_CAP_MULTIBUTTON_HEADSET = 14;
   public static final int COUNTER_APP_KEY = 0;
   public static final int COUNTER_ESCAPE_KEY = 1;
   public static final int COUNTER_KEYPAD = 2;
   public static final int COUNTER_TRACKWHEEL = 3;
   public static final int COUNTER_TRACKWHEEL_MILEAGE = 4;
   public static final int COUNTER_PERIPHERAL_EVENT = 5;
   public static final int HWID_ARMFROG = 257;
   public static final int HWID_TACHYON = -2147483389;
   public static final int HWID_TACHYON2 = -2147482621;
   public static final int HWID_TACHYON2PLUS = -1811938301;
   public static final int HWID_QUARK = -2147482365;
   public static final int HWID_QUARK2PLUSMONO = -1879046909;
   public static final int HWID_QUARK2PLUS = -1811938045;
   public static final int HWID_PHOTON = -1677720317;
   public static final int HWID_CHARM = -1811937021;
   public static final int HWID_BOURNVILLE = -1761606397;
   public static final int HWID_ELECTRON = -2080371965;
   public static final int HWID_CHARMEDGE = -2080372477;
   public static final int HWID_POSITRON = -2080371453;
   public static final int HWID_GAMMARAY = -2080371197;
   public static final int HWID_PAPABEAR = -1929375997;
   public static final int HWID_BABYBEAR = -1778381053;
   public static final int HWID_LANCER = -1895822077;
   public static final int HWID_COMET = -1929376509;
   public static final int HWID_ELTRON2 = 260;
   public static final int HWID_ELTRON2GPS = 1028;
   public static final int HWID_ELTRON2PLUS = 67109892;
   public static final int HWID_CPHOTON = 469763332;
   public static final int HWID_CCHARM = 67111172;
   public static final int HWID_CELECTRON = 67111684;
   public static final int HWID_EUROPA = 67112196;
   public static final int HWID_JUPITER = 67112708;
   public static final int HWID_BARYON = 1285;
   public static final int HWID_BARYON2PLUS = 67110149;
   public static final int HWID_BARYON2PLUSAGPS = 469763333;
   public static final int HWID_ICHARM = 67111173;
   public static final int HWID_WCHARM = 67111174;
   public static final int HWID_WPHOTON = 469763334;
   public static final int HWID_WELECTRON = 67111686;
   public static final int HWID_UCHARM = -2080372473;
   public static final int HWID_UELECTRON = -2080371961;
   public static final int HWID_METEOR = -1761604857;
   public static final int HWID_DELTARAY = -1929376253;
   public static final int HWID_MAMABEAR = -2080370941;
   public static final int HWID_SATURN = 67112452;
   public static final int HWID_FERMION = 3080;
   public static final int LIGHT_SENSOR_DISABLED = 0;
   public static final int LIGHT_SENSOR_NORMAL = 1;
   public static final int LIGHT_SENSOR_LCD_AUTO_NO_DIM_KEYPAD_AUTO = 3;
   public static final int LIGHT_SENSOR_LCD_AUTO_NO_DIM_KEYPAD_ALWAYS_OFF = 4;
   public static final int OS_API_VERSION_TYPE = 1;
   public static final int BLOCKFS_VERSION_TYPE = 2;
   public static final int FAT_VERSION_TYPE = 3;
   public static final int STP_PROTOCOL_VERSION_TYPE = 4;
   public static final int USB_DRIVER_VERSION_TYPE = 5;
   public static final int WLAN_VERSION_TYPE = 6;

   private InternalServices() {
   }

   public static final native void initiateReset(String var0);

   public static final native void setVisibleProcess(int var0);

   public static final native long getAlarm();

   public static final native boolean setAlarm(long var0);

   public static final native void killAlarm();

   public static final native boolean setTimer(int var0, long var1, boolean var3);

   public static final native void killTimer(int var0);

   public static final native boolean isDateTimeValid();

   public static final native void setTimeZoneOffset(int var0);

   public static final boolean isNetworkTimeSupported() {
      return true;
   }

   public static final boolean isDeviceClassA() {
      boolean rc = isUMTSCapable();
      if (RadioInfo.areWAFsSupported(4)) {
         rc = true;
      }

      return rc;
   }

   public static final boolean isEDGECapable() {
      switch (getHardwareID()) {
         case -2080372477:
         case -2080371965:
         case -2080371453:
         case -2080371197:
         case -2080370941:
         case -1929376509:
         case -1929376253:
         case -1929375997:
         case -1895822077:
         case -1778381053:
         case -1761604857:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isUMTSCapable() {
      switch (getHardwareID()) {
         case -2080372473:
         case -2080371961:
         case -1761604857:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isToggleAutomaticBacklightSupported() {
      return true;
   }

   public static final native boolean isNetworkTimeValid();

   public static final native long getNetworkTimeOffset();

   public static final boolean isNetworkTimeZoneSupported() {
      return !RadioInfo.areWAFsSupported(-5) ? false : isNetworkTimeSupported();
   }

   public static final native int getNetworkTimeZoneOffset();

   public static final native int getNetworkDSTOffset();

   public static final native void enableKeyUpMessages(boolean var0);

   public static final void catastrophicFailure(int error) {
      catastrophicFailure(error, null);
   }

   public static final native void catastrophicFailure(int var0, String var1);

   public static final native int runTimingTest(int var0, int var1, int var2, Object var3, Object var4);

   public static final native int getFormFactor();

   public static final native boolean isDeviceSecure();

   public static final native void setDeviceSecure(boolean var0);

   public static final native boolean isDeviceCapable(int var0);

   public static final native boolean isSoftwareCapable(int var0);

   public static final boolean isPINMessagingSupported() {
      return RadioInfo.areWAFsSupported(-5);
   }

   public static final boolean isHolsterSupported() {
      return true;
   }

   public static final native String getSleepRatio();

   public static final native int getCounter(int var0);

   public static final native String getFastResetStatus();

   public static final native int getFastResetCount();

   public static final native void allowOSLogging(boolean var0);

   public static final boolean isICharm() {
      return getHardwareID() == 67111173;
   }

   public static final boolean isReducedFormFactor() {
      int formFactor = getFormFactor();
      return formFactor == 9 || formFactor == 13;
   }

   public static final native long getUptime();

   public static final native long getLastHourglass();

   public static final native int isRebooting();

   public static final int getSlowClockRatio() {
      return 0;
   }

   public static final native int getSlowClockRatio(int var0, int var1);

   public static final native int getLastSlowClockRatio(int var0, int var1);

   public static final int getLastIntervalSlowClockRatio() {
      return 0;
   }

   public static final native int getHardwareID();

   public static final void resetIdleTime() {
      boolean allowResetIdleTimePolicyDefault = !ITPolicyInternal.isITPolicyEnabled();
      if (ITPolicy.getBoolean(24, 76, allowResetIdleTimePolicyDefault)) {
         resetIdleTimeImpl();
      }
   }

   private static final native void resetIdleTimeImpl();

   public static final int[] parsePlatformVersionString(String pv) {
      int index = 0;
      if (pv == null) {
         return null;
      }

      int[] result = new int[4];

      int octet;
      for (octet = 0; octet < result.length && index < pv.length(); octet++) {
         int nextIndex = pv.indexOf(46, index);
         if (nextIndex == -1) {
            nextIndex = pv.length();
         }

         try {
            result[octet] = Integer.parseInt(pv.substring(index, nextIndex));
         } catch (NumberFormatException e) {
            return null;
         }

         index = nextIndex + 1;
      }

      return octet == result.length ? result : null;
   }

   public static final native int getOSAPIVersion();

   public static final boolean isFermion() {
      return getHardwareID() == 3080;
   }

   public static final native int getLightSensorMode();

   public static final native void setLightSensorMode(int var0);

   public static final native byte[] getBootRomMetrics();

   public static final native byte[] getOsMetrics();

   public static final native int getRadioFrequencies();

   private static final native long getDriverVersion(int var0);

   public static final String getDriverVersionString(int driverType) {
      long driverVersion = getDriverVersion(driverType);
      int version = (int)(driverVersion >>> 32);
      int format = (int)(driverVersion & 4294967295L);
      StringBuffer buff = new StringBuffer(16);
      switch (format) {
         case -1:
            break;
         case 0:
         default:
            buff.append(version >>> 24);
            buff.append('.');
            buff.append(version >>> 16 & 0xFF);
            buff.append('.');
            buff.append(version >>> 8 & 0xFF);
            buff.append('.');
            buff.append(version & 0xFF);
            break;
         case 1:
            buff.append(version >>> 16);
            buff.append('.');
            buff.append(version & 65535);
            break;
         case 2:
            buff.append(version >>> 24);
            buff.append('.');
            buff.append(version >>> 16 & 0xFF);
            buff.append('.');
            buff.append(version >>> 15 & 1);
            buff.append('.');
            buff.append(version & 32767);
            break;
         case 3:
            buff.append(version >>> 24);
            buff.append('.');
            buff.append(version >>> 8 & 65535);
            buff.append('.');
            buff.append(version & 0xFF);
            break;
         case 4:
            buff.append(version >>> 24);
            buff.append('.');
            buff.append(version >>> 16 & 0xFF);
            buff.append('.');
            buff.append(version >>> 14 & 3);
            buff.append('.');
            buff.append(version & 16383);
            break;
         case 5:
            int digit = version >>> 12 & 15;
            if (digit != 0) {
               buff.append(digit);
            }

            buff.append(version >>> 8 & 15);
            buff.append('.');
            digit = version >>> 4 & 15;
            if (digit != 0) {
               buff.append(digit);
            }

            buff.append(version & 15);
      }

      return buff.toString();
   }

   public static final native int getLightSensorADCReading();
}
