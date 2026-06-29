package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.internal.system.InternalServices;

public final class BrowserHotkeys {
   private static IntIntHashtable _map = new IntIntHashtable();
   private static Locale _locale;

   private static final void add(int id) {
      char ch = BrowserResources.getString(id).charAt(0);
      ch = CharacterUtilities.toLowerCase(ch, 1701707776);
      _map.put(ch, id);
   }

   private static final synchronized void checkLocale() {
      Locale locale = Locale.getDefault();
      if (_locale != locale) {
         _locale = locale;
         _map.clear();
         if (!InternalServices.isReducedFormFactor()) {
            add(366);
            add(367);
            add(448);
            add(449);
            add(353);
            add(337);
            add(458);
            add(459);
            add(650);
            add(671);
            add(903);
            add(475);
            add(664);
            return;
         }

         _map.put(81, 475);
         _map.put(69, 366);
         _map.put(67, 367);
         _map.put(90, 903);
         _map.put(76, 650);
         _map.put(130, 664);
      }
   }

   public static final int map(char key) {
      checkLocale();
      if (!InternalServices.isReducedFormFactor()) {
         key = CharacterUtilities.toLowerCase(key, 1701707776);
      }

      return (char)_map.get(key);
   }

   private static final char getHotKeyForResource(int resource) {
      char hotKey = BrowserResources.getString(resource).charAt(0);
      if (InternalServices.isReducedFormFactor()) {
         switch (resource) {
            case 333:
               return 'O';
            case 334:
               return 'G';
            case 335:
               hotKey = 'A';
               break;
            case 339:
               return 'J';
         }
      }

      return hotKey;
   }

   public static final void registerBrowserHotKey(int resource, BrowserVerb verb) {
      HotKeys.registerHotKey(8, getHotKeyForResource(resource), verb, true);
   }

   public static final void deregisterBrowserHotKey(int resource) {
      HotKeys.unregisterHotKey(8, getHotKeyForResource(resource), true);
   }

   static final BrowserVerb getVerb(char key) {
      BrowserVerb verb = (BrowserVerb)HotKeys.getVerb(8, key);
      return verb != null && verb.isEnabled() ? verb : null;
   }
}
