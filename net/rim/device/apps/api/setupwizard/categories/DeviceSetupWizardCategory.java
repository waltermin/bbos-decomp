package net.rim.device.apps.api.setupwizard.categories;

import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;

public class DeviceSetupWizardCategory extends WizardCategory {
   private static Object _lock = new Object();
   private static DeviceSetupWizardCategory _instance;

   private DeviceSetupWizardCategory() {
      super(SetupWizardAPIResources.getResourceBundle(), 10, 100, null, -9194663057110490751L);
   }

   public static DeviceSetupWizardCategory getInstance() {
      if (_instance == null) {
         synchronized (_lock) {
            if (_instance == null) {
               _instance = new DeviceSetupWizardCategory();
            }
         }
      }

      return _instance;
   }
}
