package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class SecureEmailResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-6165272894895379810L, "net.rim.device.apps.internal.resource.secureemail.SecureEmail");

   public static ResourceBundleFamily getBundle() {
      return _rb;
   }

   public static String getString(int id) {
      return _rb.getString(id);
   }

   public static String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
