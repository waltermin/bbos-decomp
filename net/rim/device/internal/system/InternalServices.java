package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.RadioInfo;

public final class InternalServices {
   public static final boolean BES_50_SUPPORTED;
   public static final int FORM_TACHYON1;
   public static final int FORM_TACHYON2;
   public static final int FORM_QUARK;
   public static final int FORM_CHARM;
   public static final int FORM_BBCLIENT;
   public static final int FORM_ELECTRON;
   public static final int FORM_PERIPHERAL;
   public static final int FORM_POSITRON;
   public static final int FORM_GAMMARAY;
   public static final int FORM_MAMABEAR;
   public static final int HW_CAP_SERIAL;
   public static final int HW_CAP_USB;
   public static final int HW_CAP_IRDA;
   public static final int HW_CAP_BUILTIN_HEADSET;
   public static final int HW_CAP_REMOVABLE_BATTERY;
   public static final int HW_CAP_SMARTCARD_READER;
   public static final int HW_CAP_GPS;
   public static final int HW_CAP_LOUD_AUDIO;
   public static final int HW_CAP_BLUETOOTH;
   public static final int HW_CAP_BUZZER;
   public static final int HW_CAP_TRICOLOUR_LED;
   public static final int HW_CAP_RTC_BACKUP_POWER;
   public static final int HW_CAP_NETWORK_TIME;
   public static final int HW_CAP_HEADSET_DETECT;
   public static final int HW_CAP_LIGHT_SENSOR;
   public static final int HW_CAP_SDCARD;
   public static final int HW_CAP_SDCARD_HOT_SWAPPABLE;
   public static final int HW_CAP_CAMERA;
   public static final int HW_CAP_WLAN;
   public static final int HW_CAP_TRACKBALL_LED;
   public static final int HW_CAP_BATTERY_DOOR_DETECT;
   public static final int SW_CAP_POSIX_FS;
   public static final int SW_CAP_STREAMING;
   public static final int SW_CAP_AUDIO_STREAMING;
   public static final int SW_CAP_NO_KEY_TONE;
   public static final int SW_CAP_MEDIA_PLAYER;
   public static final int SW_CAP_VAD;
   public static final int SW_CAP_RECORDING;
   public static final int SW_CAP_VIDEO_STREAMING;
   public static final int SW_CAP_RTSP;
   public static final int SW_CAP_MULTIBUTTON_HEADSET;
   public static final int COUNTER_APP_KEY;
   public static final int COUNTER_ESCAPE_KEY;
   public static final int COUNTER_KEYPAD;
   public static final int COUNTER_TRACKWHEEL;
   public static final int COUNTER_TRACKWHEEL_MILEAGE;
   public static final int COUNTER_PERIPHERAL_EVENT;
   public static final int HWID_ARMFROG;
   public static final int HWID_TACHYON;
   public static final int HWID_TACHYON2;
   public static final int HWID_TACHYON2PLUS;
   public static final int HWID_QUARK;
   public static final int HWID_QUARK2PLUSMONO;
   public static final int HWID_QUARK2PLUS;
   public static final int HWID_PHOTON;
   public static final int HWID_CHARM;
   public static final int HWID_BOURNVILLE;
   public static final int HWID_ELECTRON;
   public static final int HWID_CHARMEDGE;
   public static final int HWID_POSITRON;
   public static final int HWID_GAMMARAY;
   public static final int HWID_PAPABEAR;
   public static final int HWID_BABYBEAR;
   public static final int HWID_LANCER;
   public static final int HWID_COMET;
   public static final int HWID_ELTRON2;
   public static final int HWID_ELTRON2GPS;
   public static final int HWID_ELTRON2PLUS;
   public static final int HWID_CPHOTON;
   public static final int HWID_CCHARM;
   public static final int HWID_CELECTRON;
   public static final int HWID_EUROPA;
   public static final int HWID_JUPITER;
   public static final int HWID_BARYON;
   public static final int HWID_BARYON2PLUS;
   public static final int HWID_BARYON2PLUSAGPS;
   public static final int HWID_ICHARM;
   public static final int HWID_WCHARM;
   public static final int HWID_WPHOTON;
   public static final int HWID_WELECTRON;
   public static final int HWID_UCHARM;
   public static final int HWID_UELECTRON;
   public static final int HWID_METEOR;
   public static final int HWID_DELTARAY;
   public static final int HWID_MAMABEAR;
   public static final int HWID_SATURN;
   public static final int HWID_FERMION;
   public static final int LIGHT_SENSOR_DISABLED;
   public static final int LIGHT_SENSOR_NORMAL;
   public static final int LIGHT_SENSOR_LCD_AUTO_NO_DIM_KEYPAD_AUTO;
   public static final int LIGHT_SENSOR_LCD_AUTO_NO_DIM_KEYPAD_ALWAYS_OFF;
   public static final int OS_API_VERSION_TYPE;
   public static final int BLOCKFS_VERSION_TYPE;
   public static final int FAT_VERSION_TYPE;
   public static final int STP_PROTOCOL_VERSION_TYPE;
   public static final int USB_DRIVER_VERSION_TYPE;
   public static final int WLAN_VERSION_TYPE;

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
