package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class KeyStoreBrowserResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(1882520843275877681L, "net.rim.device.apps.internal.resource.crypto.KeyStoreBrowser");

   public static ResourceBundleFamily getResourceBundle() {
      return _rb;
   }

   public static String getString(int id) {
      return _rb.getString(id);
   }

   public static String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
