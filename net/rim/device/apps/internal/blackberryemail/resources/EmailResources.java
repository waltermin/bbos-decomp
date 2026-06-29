package net.rim.device.apps.internal.blackberryemail.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class EmailResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-6354474320315729273L, "net.rim.device.apps.internal.resource.Email");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }
}
