package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.TraceBack;

public final class Backlight {
   public static final int BACKLIGHT_LCD;
   public static final int BACKLIGHT_KEYPAD;
   public static final int BACKLIGHT_LCD_KEYPAD;

   private static final void assertPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   public Backlight() {
      System.err.println("Warning: deprecated API new Backlight() called.");
   }

   private static final native void enable0(boolean var0, int var1);

   public static final void enable(boolean enable) {
      assertPermission();
      if (enable) {
         resetIdleTime(TraceBack.getCallingModule(0));
      }

      enable0(enable, 0);
      Thread.yield();
   }

   public static final void enable(boolean enable, int seconds) {
      assertPermission();
      if (seconds < 1) {
         throw new IllegalArgumentException();
      }

      if (seconds > 255) {
         seconds = 255;
      }

      if (enable) {
         resetIdleTime(TraceBack.getCallingModule(0));
      }

      enable0(enable, seconds);
      Thread.yield();
   }

   private static final void resetIdleTime(int callingModule) {
      if (DeviceInfo.canResetIdleTime(callingModule)) {
         InternalServices.resetIdleTime();
      }
   }

   public static final native boolean isEnabled();

   public static final native int getTimeoutDefault();

   public static final void setTimeout(int seconds) {
      assertPermission();
      setTimeout0(seconds);
   }

   private static final native void setTimeout0(int var0);

   public static final native int getBrightness();

   public static final native boolean isBrightnessConfigurable();

   public static final native int getBrightnessIncrement();

   public static final native int getBrightnessDefault();

   public static final void setBrightness(int brightness) {
      assertPermission();
      setBrightness0(brightness);
   }

   private static final native void setBrightness0(int var0);

   public static final native long getCumulativeOnTime();

   public static final void resetElapsedTime() {
      assertPermission();
      resetElapsedTime0();
   }

   private static final native void resetElapsedTime0();

   public static final void enable(int backlight, boolean enable) {
      assertPermission();
      enable0(backlight, enable);
   }

   private static final native void enable0(int var0, boolean var1);
}
