package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;

public class DeviceFeature {
   private DeviceFeature() {
   }

   private static boolean isFeatureClassRegisteredAndAvailable(long id) {
      ApplicationRegistry appreg = ApplicationRegistry.getApplicationRegistry();
      String class_name = (String)appreg.get(id);
      if (class_name != null) {
         try {
            Class.forName(class_name);
            return true;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isPhoneEnabled() {
      return isFeatureClassRegisteredAndAvailable(8424795840406028030L);
   }
}
