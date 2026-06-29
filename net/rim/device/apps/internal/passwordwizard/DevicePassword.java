package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.internal.system.Security;

public final class DevicePassword extends Password {
   private Security _security = Security.getInstance();

   public final boolean isPasswordEnabled() {
      return this._security.isPasswordEnabled();
   }

   public final boolean isPasswordValid(String password) {
      return this._security.isPasswordValid(password) == 0;
   }

   public final boolean setPassword(String password) {
      if (this.isPasswordEnabled()) {
         throw new RuntimeException("Device Password is already set");
      }

      try {
         return this._security.setPassword(null, password, password);
      } finally {
         ;
      }
   }
}
