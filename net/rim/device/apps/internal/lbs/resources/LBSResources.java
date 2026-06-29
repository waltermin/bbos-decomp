package net.rim.device.apps.internal.lbs.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class LBSResources {
   private static ResourceBundleFamily _lbsBundle = ResourceBundle.getBundle(6514774203079918781L, "net.rim.device.apps.internal.lbs.LBS");

   public static final String getString(int id) {
      return _lbsBundle.getString(id);
   }

   public static final ResourceBundleFamily getResourceBundle() {
      return _lbsBundle;
   }
}
