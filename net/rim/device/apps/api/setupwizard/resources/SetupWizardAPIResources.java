package net.rim.device.apps.api.setupwizard.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class SetupWizardAPIResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(894458828807867933L, "net.rim.device.apps.api.setupwizard.SetupWizardAPI");

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
