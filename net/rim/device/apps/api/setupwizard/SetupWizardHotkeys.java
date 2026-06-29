package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.ui.UiInternal;

public class SetupWizardHotkeys {
   private static IntIntHashtable _map = (IntIntHashtable)(new Object());
   private static ResourceBundle _rb = ResourceBundle.getBundle(894458828807867933L, "net.rim.device.apps.api.setupwizard.SetupWizardAPI");
   private static Locale _locale;

   private static void add(int id) {
      String keys = _rb.getString(id);
      if (keys != null && keys.length() > 0) {
         char key = CharacterUtilities.toUpperCase(keys.charAt(0));
         int status = 0;
         _map.put(Keypad.keycode(key, status), id);
      }
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

      return (char)hotkey;
   }

   private static void reload() {
      _map.clear();
      add(0);
      add(1);
   }
}
