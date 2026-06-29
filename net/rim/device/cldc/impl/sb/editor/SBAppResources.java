package net.rim.device.cldc.impl.sb.editor;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class SBAppResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-244523119428005625L, "net.rim.device.internal.resource.SBApp");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }
}
