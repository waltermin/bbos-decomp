package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class HRTAppResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(4494867615430826199L, "net.rim.device.internal.resource.HRTApp");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }
}
