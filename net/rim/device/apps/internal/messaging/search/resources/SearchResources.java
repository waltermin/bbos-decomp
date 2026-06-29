package net.rim.device.apps.internal.messaging.search.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.vm.WeakReference;

public final class SearchResources {
   private static WeakReference _refFamily = new WeakReference(ResourceBundle.getBundle(570792712302434978L, "net.rim.device.apps.internal.resource.Search"));

   public static final String getString(int id) {
      return getBundle().getString(id);
   }

   public static final ResourceBundleFamily getBundle() {
      ResourceBundleFamily family = (ResourceBundleFamily)_refFamily.get();
      if (family == null) {
         family = ResourceBundle.getBundle(570792712302434978L, "net.rim.device.apps.internal.resource.Search");
         _refFamily = new WeakReference(family);
      }

      return family;
   }
}
