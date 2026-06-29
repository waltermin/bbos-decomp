package net.rim.device.api.system;

public class DeviceInternal {
   private DeviceInternal() {
   }

   private static native boolean requestPowerOff0(boolean var0);

   public static boolean requestPowerOff(boolean allowAutoOn) {
      EventLogger.logEvent(-7509200465648525729L, ("POWER_DOWN: " + allowAutoOn).getBytes(), 0);
      return requestPowerOff0(allowAutoOn);
   }

   public static native boolean requestStorageMode();

   public static native boolean setDateTime(long var0);
}
