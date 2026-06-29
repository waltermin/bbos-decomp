package net.rim.device.apps.internal.options;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class OptionsResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(5215163841290712012L, "net.rim.device.apps.internal.resource.Options");

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
