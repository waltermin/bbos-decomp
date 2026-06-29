package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

public class KeyStoreBrowserHotkeys {
   private static ResourceBundle _rb = ResourceBundle.getBundle(1882520843275877681L, "net.rim.device.apps.internal.resource.crypto.KeyStoreBrowser");
   private static IntIntHashtable _map = new IntIntHashtable();
   private static Locale _locale;

   private static void add(int id) {
      String keys = _rb.getString(id);
      int status = 0;
      if (keys.length() > 1 && keys.charAt(0) == 'A') {
         status = 1;
      }

      char key = CharacterUtilities.toUpperCase(keys.charAt(keys.length() - 1), 1701707776);
      _map.put(Keypad.keycode(key, status), id);
   }

   private static void addReducedKeyboardKey(char key, int id) {
      _map.put(Keypad.keycode(key, 1), id);
   }

   private static synchronized void checkLocale() {
      Locale locale = Locale.getDefault();
      if (_locale != locale) {
         _locale = locale;
         reload();
      }
   }

   public static int map(int keycode) {
      checkLocale();
      int hotkey = _map.get(keycode);
      if (hotkey == -1 && (Keypad.status(keycode) & 2) != 0) {
         hotkey = _map.get(Keypad.keycode(UiInternal.map(keycode), 0));
      }

      return hotkey;
   }

   private static void reload() {
      _map.clear();
      if (InternalServices.isReducedFormFactor()) {
         reloadReducedKeyboard();
      } else {
         add(6063);
         add(6062);
         add(6061);
         add(6065);
         add(6066);
         add(6059);
         add(6077);
         add(6060);
         add(6064);
      }
   }

   private static void reloadReducedKeyboard() {
      addReducedKeyboardKey('A', 6063);
      addReducedKeyboardKey('C', 6062);
      addReducedKeyboardKey('U', 6061);
      addReducedKeyboardKey('M', 6066);
      addReducedKeyboardKey('O', 6059);
      addReducedKeyboardKey('E', 6060);
      addReducedKeyboardKey('B', 6064);
      addReducedKeyboardKey('L', 6065);
   }
}
