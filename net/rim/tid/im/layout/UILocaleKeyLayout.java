package net.rim.tid.im.layout;

import java.io.InputStream;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.internal.proxy.Proxy;

public class UILocaleKeyLayout implements GlobalEventListener {
   private String[] MAP_LOCATIONS = new String[]{"net_rim_platform_im_resource", "net_rim_tid"};
   private SLKeyLayout _layout;
   private Locale _lastLocaleUsed;
   private static final long REGISTRY_NAME = -6971246574359176995L;
   private static UILocaleKeyLayout _instance;

   private UILocaleKeyLayout() {
      Proxy.getInstance().addGlobalEventListenerInternal(this);
      this.init();
   }

   public synchronized void init() {
      Locale uiLocale = Locale.getDefault();
      if (this._lastLocaleUsed == null || !this._lastLocaleUsed.equals(uiLocale)) {
         Locale keyboardLocale = Locale.getDefaultForKeyboard();
         String keyboardType = SLKeyLayout.getKeyboardType(keyboardLocale.getCode());
         int keyboardID = Keypad.getHardwareLayout();
         InputStream is = this.getLayoutData(keyboardID, keyboardType, uiLocale, false);
         if (is == null) {
            is = this.getLayoutData(keyboardID, keyboardType, Locale.get(uiLocale.getLanguage(), ""), false);
         }

         if (is == null) {
            is = this.getLayoutData(keyboardID, keyboardType, Locale.get(uiLocale.getLanguage(), ""), true);
         }

         if (is == null) {
            throw new RuntimeException("Can't find UI Key Layout for " + uiLocale.toString());
         }

         this._layout = new SLKeyLayout(uiLocale, false, (byte)0, is);
         this._lastLocaleUsed = uiLocale;
      }
   }

   @Override
   public synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.init();
      }
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

   public static SLKeyLayout getUIKeyLayout() {
      return _instance._layout;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (UILocaleKeyLayout)ar.getOrWaitFor(-6971246574359176995L);
      if (_instance == null) {
         _instance = new UILocaleKeyLayout();
         ar.put(-6971246574359176995L, _instance);
      }
   }
}
