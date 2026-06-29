package net.rim.device.api.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.vm.TraceBack;

public final class DeviceInfo {
   public static final int BSTAT_DEAD;
   public static final int BSTAT_TOO_COLD;
   public static final int BSTAT_TOO_HOT;
   public static final int BSTAT_LOW;
   public static final int BSTAT_NONE;
   public static final int BSTAT_REVERSED;
   public static final int BSTAT_UNKNOWN_BATTERY;
   public static final int BSTAT_NO_TURN_ON;
   public static final int BSTAT_NO_RADIO;
   public static final int BSTAT_NO_CAMERA_FLASH;
   public static final int BSTAT_CHARGING;
   public static final int BSTAT_LOW_RATE_CHARGING;
   public static final int BSTAT_IS_USING_EXTERNAL_POWER;
   public static final int BSTAT_LEVEL_CHANGED;
   public static final int BSTAT_NO_WLAN;
   public static final int BSTAT_AC_CONTACTS;
   public static final int INVALID_DEVICE_ID;
   private static final String EMPTY_STRING;

   private DeviceInfo() {
   }

   public static final native int getDeviceId();

   public static final native int getBatteryLevel();

   public static final native int getBatteryStatus();

   public static final boolean isBatteryRemovable() {
      return InternalServices.isDeviceCapable(4);
   }

   public static final int getOSVersion() {
      int[] array = InternalServices.parsePlatformVersionString(getPlatformVersion());
      if (array != null && array.length == 4) {
         int version = 0;

         for (int i = 3; i >= 0; i--) {
            if (array[i] <= 255) {
               version += (array[3 - i] & 0xFF) << i * 8;
            }
         }

         return version;
      } else {
         return 0;
      }
   }

   public static final native String getPlatformVersion();

   public static final String getSoftwareVersion() {
      int handle = CodeModuleManager.getModuleHandle("net_rim_os");
      return handle == 0 ? "" : CodeModuleManager.getModuleVersion(handle);
   }

   public static final native boolean isInHolster();

   public static final native long getIdleTime();

   public static final native boolean isSimulator();

   public static final String getDeviceName() {
      switch (InternalServices.getHardwareID()) {
         case -2080372477:
            return "7130";
         case -2080372473:
            return "7130u";
         case -2080371965:
            return "87" + getElectronHACID();
         case -2080371961:
            return "8707";
         case -2080371453:
            return "8100";
         case -2080371197:
            return "8800";
         case -2080370941:
            return "8320";
         case -1929376509:
            return "8120";
         case -1929376253:
            return "8820";
         case -1929375997:
            return "8310";
         case -1895822077:
            return "8110";
         case -1778381053:
            return "8300";
         case -1761604857:
            return "8807";
         case 3080:
            return "Smart Card Reader";
         case 67111172:
            return "7130e";
         case 67111173:
            return "7100i";
         case 67111684:
            return "8703e";
         case 67112196:
            return "8130";
         case 67112452:
            return "8830";
         case 67112708:
            return "8330";
         case 469763332:
            return "7250";
         case 469763333:
            return "7520";
         case 469763334:
            return "7270";
         default:
            return "Unknown";
      }
   }

   private static final String getElectronHACID() {
      if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
         return BluetoothME.isSupported() ? "05g" : "05";
      } else {
         return "00";
      }
   }

   public static final native int getBatteryVoltage();

   public static final native int getBatteryTemperature();

   public static final boolean isPasswordEnabled() {
      Security s = Security.getInstance();
      return s != null ? s.isPasswordEnabled() : false;
   }

   public static final native String getManufacturerName();

   public static final boolean hasCamera() {
      return InternalServices.isDeviceCapable(21);
   }

   public static final boolean canResetIdleTime() {
      return canResetIdleTime(TraceBack.getCallingModule(0));
   }

   public static final boolean canResetIdleTime(int callingModule) {
      boolean result = false;
      boolean allowResetIdleTimePolicyDefault = !ITPolicyInternal.isITPolicyEnabled();
      if (ITPolicy.getBoolean(24, 76, allowResetIdleTimePolicyDefault)
         && !ControlledAccess.verifyCodeModuleSignature(callingModule, 51)
         && ControlledAccess.verifySignatures(true, 5526098)) {
         try {
            ApplicationControl.assertIdleTimerPermitted(true, CommonResource.getBundle(), 10166);
            return true;
         } catch (ControlledAccessException var4) {
         }
      }

      return result;
   }

   public static final int getLockTimeout() {
      return Security.getInstance().getTimeout();
   }
}
