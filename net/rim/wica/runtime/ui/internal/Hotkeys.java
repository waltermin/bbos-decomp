package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.runtime.resources.RuntimeResources;

final class Hotkeys {
   private static IntIntHashtable _map = (IntIntHashtable)(new Object());
   private static Locale _locale;

   static final int map(char key) {
      checkLocale();
      if (Keypad.getHardwareLayout() != 1364346180) {
         key = CharacterUtilities.toLowerCase(key, 1701707776);
      }

      return (char)_map.get(key);
   }

   private static final void add(int id) {
      char ch = RuntimeResources.getString(id).charAt(0);
      ch = CharacterUtilities.toLowerCase(ch, 1701707776);
      _map.put(ch, id);
   }

   private static final synchronized void checkLocale() {
      Locale locale = Locale.getDefault();
      if (_locale != locale) {
         _locale = locale;
         _map.clear();
         if (Keypad.getHardwareLayout() != 1364346180) {
            add(156);
            add(155);
            add(154);
            return;
         }

         _map.put(81, 154);
         _map.put(69, 156);
         _map.put(67, 155);
      }
   }
}
