package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class SetupWizardResources {
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(6961541642013267395L, "net.rim.device.apps.internal.setupwizard.SetupWizard");

   public static final String getString(int id) {
      return _resources.getString(id);
   }

   public static final ResourceBundleFamily getBundle() {
      return _resources;
   }
}
