package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.LEDEngine;

public final class LED {
   public static final int STATE_OFF = 0;
   public static final int STATE_ON = 1;
   public static final int STATE_BLINKING = 2;
   public static final int STATE_PATTERN = 3;
   public static final int STATE_AUDIO_SYNC = 4;
   public static final int BRIGHTNESS_12 = 0;
   public static final int BRIGHTNESS_25 = 1;
   public static final int BRIGHTNESS_50 = 2;
   public static final int BRIGHTNESS_100 = 3;
   public static final int LED_TYPE_STATUS = 0;
   public static final int LED_TYPE_TRACKBALL = 1;
   private static LEDEngine _ledEngine = LEDEngine.getInstance();

   private LED() {
   }

   private static final void assertPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   public static final boolean isSupported(int type) {
      return LEDEngine.isSupported(type);
   }

   public static final boolean isPolychromatic() {
      return LEDEngine.isPolychromatic();
   }

   public static final boolean isPolychromatic(int type) {
      return LEDEngine.isPolychromatic(type);
   }

   public static final void setConfiguration(int onTime, int offTime, int brightness) {
      setConfiguration(0, onTime, offTime, brightness);
   }

   public static final void setConfiguration(int type, int onTime, int offTime, int brightness) {
      assertPermission();
      _ledEngine.setConfigurationInternal(type, onTime, offTime, brightness);
   }

   public static final void setColorConfiguration(int onTime, int offTime, int color) {
      setColorConfiguration(0, onTime, offTime, color);
   }

   public static final void setColorConfiguration(int type, int onTime, int offTime, int color) {
      assertPermission();
      _ledEngine.setColorConfigurationInternal(type, onTime, offTime, color);
   }

   public static final void setState(int state) {
      setState(0, state);
   }

   public static final void setState(int type, int state) {
      assertPermission();
      _ledEngine.setStateInternal(type, state);
   }

   public static final void setColorPattern(int[] pattern, boolean repeat) {
      setColorPattern(0, pattern, repeat);
   }

   public static final void setColorPattern(int type, int[] pattern, boolean repeat) {
      assertPermission();
      _ledEngine.setColorPatternInternal(type, pattern, repeat);
   }
}
