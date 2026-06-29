package net.rim.device.apps.internal.options.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class OptionsResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(5215163841290712012L, "net.rim.device.apps.internal.resource.Options");

   public static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
