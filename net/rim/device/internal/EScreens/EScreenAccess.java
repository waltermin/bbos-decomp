package net.rim.device.internal.EScreens;

import net.rim.device.api.system.Branding;
import net.rim.device.internal.system.InternalServices;

public final class EScreenAccess {
   private EScreenAccess() {
   }

   public static final int getAccessLevel() {
      if (!InternalServices.isDeviceSecure()) {
         return 0;
      } else if (Branding.getData(16) != null) {
         return 0;
      } else {
         return EScreenSecurityData.get(0).isAccessAllowed() ? 1 : -1;
      }
   }

   public static final boolean isAllowed() {
      return getAccessLevel() != -1;
   }
}
