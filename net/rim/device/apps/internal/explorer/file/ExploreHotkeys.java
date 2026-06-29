package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

public final class ExploreHotkeys {
   private static ResourceBundle _rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
   private static IntIntHashtable _map = (IntIntHashtable)(new Object());
   private static Locale _locale;

   private static final void add(int id) {
      String keys = _rb.getString(id);
      int status = 0;
      if (keys.length() > 1 && keys.charAt(0) == 'A') {
         status = 1;
      }

      char key = CharacterUtilities.toUpperCase(keys.charAt(keys.length() - 1));
      _map.put(Keypad.keycode(key, status), id);
   }

   private static final synchronized void checkLocale() {
      Locale locale = Locale.getDefault();
      if (_locale != locale) {
         _locale = locale;
         reload();
      }
   }

   public static final int map(int keycode) {
      checkLocale();
      int hotkey = _map.get(keycode);
      if (hotkey == -1 && (Keypad.status(keycode) & 2) != 0) {
         hotkey = _map.get(Keypad.keycode(UiInternal.map(keycode), 0));
      }

      return (char)hotkey;
   }

   private static final void reload() {
      _map.clear();
      if (InternalServices.isReducedFormFactor()) {
         reloadReducedKeyboard();
      } else {
         add(168);
         add(169);
         add(6);
         add(7);
      }
   }

   private static final void reloadReducedKeyboard() {
      _map.put(69, 168);
      _map.put(67, 169);
      _map.put(68, 7);
      _map.put(74, 6);
   }
}
