package net.rim.device.apps.internal.implus;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class IMPlusResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(8928414179361597952L, "net.rim.device.apps.internal.resource.IMPlus");

   public static final String getString(int id) {
      return _rb.getString(id);
   }
}
