package net.rim.device.apps.api.setupwizard.categories;

import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;

public class DevicePersonalizationWizardCategory extends WizardCategory {
   private static Object _lock = new Object();
   private static DevicePersonalizationWizardCategory _instance;

   private DevicePersonalizationWizardCategory() {
      super(SetupWizardAPIResources.getResourceBundle(), 9, 200, null, -4541057911757775449L);
   }

   public static DevicePersonalizationWizardCategory getInstance() {
      if (_instance == null) {
         synchronized (_lock) {
            if (_instance == null) {
               _instance = new DevicePersonalizationWizardCategory();
            }
         }
      }

      return _instance;
   }
}
