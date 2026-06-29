package net.rim.device.internal.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.InternalServices;

public class UiSettings {
   private boolean _listenersActive = true;
   private RegistryListener[] _listeners = new RegistryListener[0];
   private static final long GUID = 4752875821328611217L;
   public static final long LED_COVERAGE_INDICATOR_GUID = 6270993390899536868L;
   public static final long OFF_PROFILE_ENABLED_GUID = 6869208671291562587L;
   public static final char CURRENCY_KEY_NONE = '\u0000';
   private static final int SCREEN_CONTRAST_DEFAULT = 50;
   private static UiSettings _instance;

   private UiSettings() {
   }

   public static void addListener(RegistryListener listener) {
      Arrays.add(_instance._listeners, listener);
   }

   public static int getBacklightBrightness() {
      return UiOptionsRegistry.getInstance().getInt(1685157992482037073L);
   }

   public static int getBacklightTimeout() {
      return UiOptionsRegistry.getInstance().getInt(-4413078574218726736L);
   }

   public static int getTrackballKeylockBacklightTimeout() {
      return UiOptionsRegistry.getInstance().getInt(5292311981504290757L);
   }

   public static int getDisplayContrast() {
      return UiOptionsRegistry.getInstance().getInt(-1460892010845079752L);
   }

   public static boolean getLEDCoverageIndicatorStatus() {
      return UiOptionsRegistry.getInstance().getBoolean(669566532873263048L);
   }

   public static boolean getOffProfileEnabled() {
      return UiOptionsRegistry.getInstance().getBoolean(-3239010168274370595L);
   }

   public static boolean getAutomaticBacklightEnabled() {
      return UiOptionsRegistry.getInstance().getBoolean(-4779732858771257140L);
   }

   public static char getCurrencyKey() {
      return UiOptionsRegistry.getInstance().getChar(-9137283790714193735L);
   }

   public static boolean getKeypadToneEnabled() {
      return UiOptionsRegistry.getInstance().getBoolean(4710809342279106215L);
   }

   public static int getKeypadRepeatRate() {
      return UiOptionsRegistry.getInstance().getInt(3372005855522553662L);
   }

   public static int getKeypadRepeatDelay() {
      return UiOptionsRegistry.getInstance().getInt(4484666050398206415L);
   }

   public static void initialize() {
      try {
         int contrast = getDisplayContrast();
         setDisplayContrast(contrast);
      } catch (Exception var10) {
      }

      try {
         int brightness = getBacklightBrightness();
         setBacklightBrightness(brightness);
      } catch (Exception var9) {
      }

      try {
         int timeout = getBacklightTimeout();
         setBacklightTimeout(timeout);
      } catch (Exception var8) {
      }

      try {
         boolean LEDStatus = getLEDCoverageIndicatorStatus();
         RIMGlobalMessagePoster.postGlobalEvent(6270993390899536868L);
         setLEDCoverageIndicatorStatus(LEDStatus);
      } catch (Exception var7) {
      }

      try {
         char value = getCurrencyKey();
         setCurrencyKey(value);
      } catch (Exception var6) {
      }

      try {
         boolean toneEnabled = getKeypadToneEnabled();
         setKeypadToneEnabled(toneEnabled);
      } catch (Exception var5) {
      }

      try {
         int keyRepeatRate = getKeypadRepeatRate();
         setKeypadRepeatRate(keyRepeatRate);
      } catch (Exception var4) {
      }

      try {
         int keyRepeatDelay = getKeypadRepeatDelay();
         setKeypadRepeatDelay(keyRepeatDelay);
      } catch (Exception var3) {
      }

      try {
         boolean isEnabled = getOffProfileEnabled();
         setOffProfileEnabled(isEnabled);
         RIMGlobalMessagePoster.postGlobalEvent(6869208671291562587L);
      } catch (Exception var2) {
      }

      try {
         boolean automaticBacklightEnabled = getAutomaticBacklightEnabled();
         setAutomaticBacklightEnabled(automaticBacklightEnabled);
      } catch (Exception var1) {
      }
   }

   static void notifyRegistryChanged() {
      if (_instance._listenersActive) {
         RegistryListener[] listeners = _instance._listeners;
         if (listeners != null) {
            for (int lv = listeners.length - 1; lv >= 0; lv--) {
               listeners[lv].registryChanged();
            }
         }
      }
   }

   static void notifyRegistryChanged(long key) {
      if (_instance._listenersActive) {
         RegistryListener[] listeners = _instance._listeners;
         if (listeners != null) {
            for (int lv = listeners.length - 1; lv >= 0; lv--) {
               listeners[lv].registryChanged(key);
            }
         }
      }
   }

   public static void setBacklightBrightness(int brightness) {
      Backlight.setBrightness(brightness);
      UiOptionsRegistry.getInstance().setInt(1685157992482037073L, brightness);
   }

   public static void setBacklightTimeout(int timeout) {
      Backlight.setTimeout(timeout);
      UiOptionsRegistry.getInstance().setInt(-4413078574218726736L, timeout);
   }

   public static void setTrackballKeyLockBacklightTimeout(int timeout) {
      Backlight.setTimeout(timeout);
      UiOptionsRegistry.getInstance().setInt(5292311981504290757L, timeout);
   }

   public static void setDisplayContrast(int contrast) {
      Display.setContrast(contrast);
      UiOptionsRegistry.getInstance().setInt(-1460892010845079752L, contrast);
   }

   public static void setLEDCoverageIndicatorStatus(boolean status) {
      UiOptionsRegistry.getInstance().setBoolean(669566532873263048L, status);
   }

   public static void setCurrencyKey(char value) {
      UiOptionsRegistry.getInstance().setChar(-9137283790714193735L, value);
   }

   public static void setOffProfileEnabled(boolean isSet) {
      UiOptionsRegistry.getInstance().setBoolean(-3239010168274370595L, isSet);
   }

   public static void setKeypadToneEnabled(boolean enabled) {
      UiOptionsRegistry.getInstance().setBoolean(4710809342279106215L, enabled);
      AudioRouter.getInstance().enableInputFeedback(1, enabled);
   }

   public static void setKeypadRepeatRate(int rate) {
      UiOptionsRegistry.getInstance().setInt(3372005855522553662L, rate);
      setRepeat(getKeypadRepeatDelay(), rate);
   }

   public static void setKeypadRepeatDelay(int delay) {
      UiOptionsRegistry.getInstance().setInt(4484666050398206415L, delay);
      setRepeat(delay, getKeypadRepeatRate());
   }

   public static void setAutomaticBacklightEnabled(boolean isEnabled) {
      boolean setting = InternalServices.isToggleAutomaticBacklightSupported() ? isEnabled : true;
      if (InternalServices.isDeviceCapable(16)) {
         InternalServices.setLightSensorMode(setting ? 1 : 3);
      }

      UiOptionsRegistry.getInstance().setBoolean(-4779732858771257140L, setting);
   }

   public static void setListenersActive(boolean active) {
      if (active != _instance._listenersActive) {
         _instance._listenersActive = active;
         if (active) {
            notifyRegistryChanged();
         }
      }
   }

   private static native void setRepeat(int var0, int var1);

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      _instance = (UiSettings)applicationRegistry.getOrWaitFor(4752875821328611217L);
      if (_instance == null) {
         _instance = new UiSettings();
         applicationRegistry.put(4752875821328611217L, _instance);
      }
   }
}
