package net.rim.device.apps.internal.sms.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class SMSResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-547744196055312387L, "net.rim.device.apps.internal.resource.SMS");

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
