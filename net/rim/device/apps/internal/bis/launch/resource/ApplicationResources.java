package net.rim.device.apps.internal.bis.launch.resource;

import net.rim.device.api.i18n.ResourceBundle;

public final class ApplicationResources implements BISLaunchResource {
   private static ResourceBundle _resources = ResourceBundle.getBundle(1322930605485095732L, "net.rim.device.apps.internal.bis.launch.resource.BISLaunch");

   public static final String getString(int id) {
      if (_resources == null) {
         throw new IllegalStateException("No resource strings present on device");
      } else {
         return _resources.getString(id);
      }
   }
}
