package net.rim.device.api.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.TraceBack;

public final class Keypad {
   public static final long GUID_KEYPAD_OPTIONS_CHANGED = 6498096261923284925L;
   public static final long GUID_KEYPAD_CHANGED = -3769281743063593175L;
   public static final int HW_MAP_QWERTY = 0;
   public static final int HW_MAP_AZERTY = 1718747136;
   public static final int HW_MAP_QWERTZ = 1718747136;
   public static final int HW_LAYOUT_NUM_ROW = 0;
   public static final int HW_LAYOUT_PHONE = 1179602501;
   public static final int HW_LAYOUT_REDUCED = 1364346180;
   public static final int HW_LAYOUT_REDUCED_24 = 1364341300;
   public static final int HW_LAYOUT_LEGACY = 1295594807;
   public static final int HW_LAYOUT_32 = 1364669234;
   public static final int HW_LAYOUT_39 = 1364669241;
   public static final int KEY_BACKLIGHT = 259;
   public static final int KEY_BACKSPACE = 8;
   public static final int KEY_DELETE = 127;
   public static final int KEY_ESCAPE = 27;
   public static final int KEY_ENTER = 10;
   public static final int KEY_SPACE = 32;
   public static final int KEY_SHIFT_RIGHT = 256;
   public static final int KEY_SHIFT_X = 261;
   public static final int KEY_ALT = 257;
   public static final int KEY_SHIFT_LEFT = 258;
   public static final int KEY_APPLICATION = 21;
   public static final int KEY_SPEAKERPHONE = 273;
   public static final int KEY_SEND = 17;
   public static final int KEY_END = 18;
   public static final int KEY_MIDDLE = 19;
   public static final int KEY_CONVENIENCE_1 = 19;
   public static final int KEY_CONVENIENCE_2 = 21;
   public static final int KEY_NEXT = 20;
   public static final int KEY_VOLUME_UP = 4096;
   public static final int KEY_VOLUME_DOWN = 4097;
   public static final int KEY_MENU = 4098;
   public static final int KEY_REPEAT_DELAY_SLOW = 600;
   public static final int KEY_REPEAT_DELAY_NORMAL = 450;
   public static final int KEY_REPEAT_DELAY_FAST = 300;
   public static final int KEY_REPEAT_OFF = 0;
   public static final int KEY_REPEAT_SLOW = 400;
   public static final int KEY_REPEAT_NORMAL = 200;
   public static final int KEY_REPEAT_FAST = 100;
   public static final int STATUS_CHARACTER = 12288;
   public static final int STATUS_UNALT = 16384;
   private static final int KEYPAD_DATA_VERSION_KEY = -1;
   private static final int KEYPAD_DATA_VERSION = 1;
   private static final int KEYPAD_LOCALE_VARIANT_KEY = -2;
   private static final int NUM_LAYOUT_KEY = -3;
   private static final int KEYPAD_OS_LOCALE_KEY = -4;
   private static final int KEYPAD_OS_LOCALE_VARIANT_KEY = -5;
   private static final int KEYPAD_ID = -6;
   public static final int KEYPAD_MODE_NORMAL = 0;
   public static final int KEYPAD_MODE_NUMLOCK = 1;
   public static final int KEYPAD_MODE_MULTITAP = 2;
   public static final int KEYPAD_MODE_CUSTOM = 2;
   private static final long KEYPAD_DATA_KEY = -6609599810714651977L;
   private static PersistentObject _persistentKeypadData;
   private static Keypad$KeypadData _keypadData;

   private Keypad() {
   }

   public static final void commit() {
      commit(false, false);
   }

   private static final void commit(boolean notifyOfOptionsChanges, boolean notifyOfLocaleChanges) {
      _persistentKeypadData.commit();
      if (notifyOfOptionsChanges) {
         RIMGlobalMessagePoster.postGlobalEvent(6498096261923284925L);
      }

      if (notifyOfLocaleChanges) {
         RIMGlobalMessagePoster.postGlobalEvent(-3769281743063593175L);
      }
   }

   public static final char getAltedChar(char ch) {
      SLKeyLayout layout = getLayout();
      return layout == null ? '\u0000' : layout.getAltedChar(ch);
   }

   public static final Locale[] getAvailableLocales() {
      String[] keys = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad").getStringArray(100);
      Locale[] locales = new Locale[0];

      for (int i = 0; i < keys.length; i++) {
         addLocaleTo(locales, Locale.parse(keys[i]));
      }

      return locales;
   }

   private static final void addLocaleTo(Locale[] array, Locale locale) {
      ResourceBundleFamily family = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad");
      ResourceBundle bundle = family.getBundle(locale);
      String[] iIDs = bundle.getStringArray(102);

      for (int i = 0; i < iIDs.length; i++) {
         Locale l = Locale.get(locale.getCode(), "", Locale.convertStringToKeyboardID(iIDs[i]));
         Arrays.add(array, l);
      }
   }

   public static final Locale getLocale() {
      return _keypadData.getLocale();
   }

   public static final char getUnaltedChar(char ch) {
      SLKeyLayout layout = getLayout();
      return layout == null ? '\u0000' : Character.toUpperCase(layout.getUnaltedChar(ch));
   }

   public static final SLKeyLayout getLayout() {
      SLControlObject obj = (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
      return obj == null ? null : obj.getKeyLayout();
   }

   public static final boolean isKeyToneSupported() {
      return !InternalServices.isSoftwareCapable(4);
   }

   public static final char map(int keycode) {
      return map(key(keycode), status(keycode));
   }

   public static final char map(int key, int status) {
      status &= 28695;
      if ((status & 4) != 0 && (status & 16) != 0) {
         status &= -7;
      }

      if ((status & 4) != 0) {
         status &= -3;
         if ((status & 1) != 0) {
            status &= -5;
         }
      }

      if ((status & 16) != 0) {
         status &= -2;
         if ((status & 2) != 0) {
            status &= -17;
         }
      }

      SLKeyLayout layout = getLayout();
      return layout == null ? '\u0000' : layout.getKeyChars(key, SLKeyLayout.convertStatusToModifiers(status), false).charAt(0);
   }

   public static final int key(int keycode) {
      return keycode >>> 16;
   }

   public static final native int getHardwareMap();

   public static final native int getHardwareLayout();

   public static final int getKeyCode(char ch, int status) {
      SLKeyLayout layout = getLayout();
      return layout == null ? 0 : getLayout().getOriginalKeyCode(ch, SLKeyLayout.convertStatusToModifiers(status));
   }

   public static final int status(int keycode) {
      return keycode & 65535;
   }

   public static final void updateKeyTone() {
      if (isKeyToneSupported()) {
         boolean uiKeyToneSetting = UiSettings.getKeypadToneEnabled();
         if (uiKeyToneSetting != isKeyToneEnabled()) {
            AudioRouter.getInstance().enableInputFeedback(1, uiKeyToneSetting);
         }
      }
   }

   public static final boolean isOnKeypad(char ch) {
      SLKeyLayout layout = getLayout();
      return layout.contains(ch);
   }

   public static final boolean isValidKeyCode(int keycode) {
      int scancode = key(keycode);
      return isValidScanCode(scancode);
   }

   private static final boolean isValidScanCode(int scancode) {
      switch (scancode) {
         case 0:
         case 8:
         case 10:
         case 17:
         case 18:
         case 19:
         case 21:
         case 27:
         case 32:
         case 127:
         case 256:
         case 257:
         case 258:
         case 259:
         case 261:
         case 273:
         case 4096:
         case 4097:
         case 4098:
            return true;
         default:
            return scancode >= 65 && scancode <= 90;
      }
   }

   public static final int keycode(char scancode, int status) {
      if ((status & 12288) == 0 && !isValidScanCode(scancode)) {
         return 0;
      }

      if (scancode < 127) {
         scancode = CharacterUtilities.toUpperCase(scancode, 1701707776);
      }

      return scancode << 16 | status & 65535;
   }

   private static final void loadKeypadData() {
      if (_keypadData == null) {
         _persistentKeypadData = RIMPersistentStore.getPersistentObject(-6609599810714651977L);
         synchronized (_persistentKeypadData) {
            Keypad$KeypadData keyPadData = (Keypad$KeypadData)_persistentKeypadData.getContents();
            if (keyPadData == null) {
               keyPadData = new Keypad$KeypadData();
               _persistentKeypadData.setContents(keyPadData, 51, false);
               commit(false, false);
            } else {
               Locale oslocale = Locale.getDefaultForKeyboard();
               if (!keyPadData.getOSLocale().equals(oslocale)) {
                  keyPadData.setLocale(oslocale);
                  _keypadData = keyPadData;
                  commit(false, true);
               }
            }

            _keypadData = keyPadData;
         }
      }
   }

   public static final boolean setKeypadLocale(Locale locale) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      _keypadData.setLocale(locale);
      commit(false, true);
      return true;
   }

   private static final native void changeShiftState0(int var0);

   public static final void changeShiftState(int event) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      changeShiftState0(event);
   }

   private static final native void enableStandbyMode0(boolean var0);

   public static final void enableStandbyMode(boolean enabled) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      enableStandbyMode0(enabled);
   }

   public static final boolean hasSendEndKeys() {
      switch (getHardwareLayout()) {
         case 1364341300:
         case 1364346180:
         case 1364669234:
         case 1364669241:
            return true;
         default:
            return false;
      }
   }

   public static final boolean hasCurrencyKey() {
      switch (getHardwareLayout()) {
         case 1364669234:
         case 1364669241:
            return true;
         default:
            return false;
      }
   }

   public static final boolean hasFrontConvenienceKey() {
      switch (getHardwareLayout()) {
         case 1364346180:
         case 1364669234:
            return true;
         default:
            return false;
      }
   }

   public static final boolean hasLeftSideConvenienceKey() {
      switch (getHardwareLayout()) {
         case 1364341300:
         case 1364669234:
         case 1364669241:
            return true;
         case 1364346180:
            if (InternalServices.getHardwareID() == -2080372477) {
               return true;
            }
         default:
            return false;
      }
   }

   public static final boolean hasRightSideConvenienceKey() {
      switch (getHardwareLayout()) {
         case 1364341300:
            return true;
         case 1364669241:
            if (InternalServices.getFormFactor() == 15) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   public static final boolean hasMuteKey() {
      switch (getHardwareLayout()) {
         case 1364341300:
         case 1364669234:
         case 1364669241:
            return true;
         case 1364346180:
            if (InternalServices.getHardwareID() == -2080372477) {
               return true;
            }
         default:
            return false;
      }
   }

   public static final boolean hasSpeakerphoneKey() {
      return InternalServices.getHardwareID() == 67111173;
   }

   public static final void setMode(int mode) {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
         ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
      }

      setModeImpl(mode);
   }

   private static final native void setModeImpl(int var0);

   public static final void setMode(int mode, String iconName) {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
         ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
      }

      if ((iconName != null || mode != 2) && (iconName == null || mode == 2)) {
         if (iconName != null) {
            EncodedImage image = ThemeManager.getActiveTheme().getImage(iconName, true);
            if (image != null) {
               UiInternal.setThemeIcon(8, image);
            }
         }

         setModeImpl(mode);
      }
   }

   public static final void setKeyTone(boolean enable) {
      if (isKeyToneSupported()) {
         AudioRouter.getInstance().enableInputFeedback(1, enable);
      }
   }

   public static final boolean isKeyToneEnabled() {
      return isKeyToneSupported() && UiSettings.getKeypadToneEnabled();
   }

   static {
      loadKeypadData();
   }
}
