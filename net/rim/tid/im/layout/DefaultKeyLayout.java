package net.rim.tid.im.layout;

import java.io.InputStream;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Keypad;

public class DefaultKeyLayout {
   private String[] MAP_LOCATIONS = new String[]{"net_rim_platform_im_resource", "net_rim_tid"};
   private SLKeyLayout _layout;
   private static final long REGISTRY_NAME = 7549901632574121075L;
   private static DefaultKeyLayout _instance;

   private DefaultKeyLayout() {
      try {
         this.init();
      } catch (Throwable e) {
         throw new RuntimeException("DefaultKeyLayout init failed - " + e.toString());
      }
   }

   private synchronized void init() {
      String keyboardType = SLKeyLayout.getKeyboardType(1701729619);
      int keyboardID = Keypad.getHardwareLayout();
      Locale englishLocale = Locale.get("en", "", "");
      InputStream is = this.getLayoutData(keyboardID, keyboardType, englishLocale, false);
      if (is == null) {
         is = this.getLayoutData(keyboardID, keyboardType, englishLocale, true);
      }

      if (is == null) {
         throw new RuntimeException("Can't find Key Layout for English US locale");
      }

      this._layout = new SLKeyLayout(englishLocale, false, (byte)0, is);
   }

   private InputStream getLayoutData(int aKeyboardId, String aKeyboardType, Locale anInputLocale, boolean useDefault) {
      for (int i = 0; i < this.MAP_LOCATIONS.length; i++) {
         InputStream is = SLKeyLayout.getLayoutData(aKeyboardId, aKeyboardType, anInputLocale, this.MAP_LOCATIONS[i], useDefault);
         if (is != null) {
            return is;
         }
      }

      return null;
   }

   public static SLKeyLayout getDefaultKeyLayout() {
      return _instance._layout;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (DefaultKeyLayout)ar.getOrWaitFor(7549901632574121075L);
      if (_instance == null) {
         _instance = new DefaultKeyLayout();
         ar.put(7549901632574121075L, _instance);
      }
   }
}
