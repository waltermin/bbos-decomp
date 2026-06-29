package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.internal.resource.RibbonResource;

final class RibbonResources implements RibbonResource {
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");

   public static final String getString(int id) {
      return _resources.getString(id);
   }
}
