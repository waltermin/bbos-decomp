package net.rim.device.apps.internal.messaging;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

public class MessageHotkeys {
   private static ResourceBundle _rb = ResourceBundle.getBundle(1758158344049992104L, "net.rim.device.apps.internal.resource.Message");
   private static IntIntHashtable _map = new IntIntHashtable();
   private static Locale _locale;

   private static void add(int id) {
      String keys = _rb.getString(id);
      int status = 0;
      if (keys.length() > 1 && keys.charAt(0) == 'A') {
         status = 1;
      }

      char key = CharacterUtilities.toLowerCase(keys.charAt(keys.length() - 1));
      key = (char)Keypad.getKeyCode(key, 0);
      _map.put(Keypad.keycode(key, status), id);
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
      if (InternalServices.isReducedFormFactor()) {
         reloadReducedKeyboard();
      } else {
         add(141);
         add(142);
         add(143);
         add(144);
         add(145);
         add(146);
         add(147);
         add(148);
         add(149);
         add(150);
         add(151);
         add(152);
         add(153);
         add(154);
         add(155);
         add(185);
         add(186);
      }
   }

   private static void reloadReducedKeyboard() {
      _map.put(5308416, 148);
      _map.put(5177344, 150);
      _map.put(4259840, 149);
      _map.put(1310720, 146);
      _map.put(4521984, 141);
      _map.put(4390912, 142);
      _map.put(4456448, 144);
      _map.put(4849664, 143);
   }
}
