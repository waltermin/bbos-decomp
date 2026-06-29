package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.ui.CommonResources;

final class EnableSSOptionProfileVerb extends SSOptionProfileVerb {
   private int _flags;
   public static final int OVERRIDE_ENABLED_STATE;
   public static final int ENABLE;
   public static final int DISABLE;

   public EnableSSOptionProfileVerb() {
      this(0);
   }

   public EnableSSOptionProfileVerb(int flags) {
      super(10000);
      this._flags = flags;
   }

   @Override
   public final String toString() {
      if ((this._flags & 2) != 0) {
         if ((this._flags & 4) != 0) {
            return CommonResources.getString(1760);
         }

         if ((this._flags & 8) != 0) {
            return CommonResources.getString(1770);
         }
      } else {
         SSOptionProfile profile = (SSOptionProfile)this.getProfile();
         if (profile != null) {
            if (profile.isEnabled()) {
               return CommonResources.getString(1770);
            }

            return CommonResources.getString(1760);
         }
      }

      return CommonResources.getString(1760);
   }

   @Override
   public final Object invoke(Object parameter) {
      SSOptionProfile profile = (SSOptionProfile)this.getProfile();
      if (profile != null) {
         if ((this._flags & 2) != 0) {
            if ((this._flags & 4) != 0) {
               profile.enable();
            } else if ((this._flags & 8) != 0) {
               profile.disable();
            }
         } else if (profile.isEnabled()) {
            profile.disable();
         } else {
            profile.enable();
         }
      }

      this.setProfile(null);
      return null;
   }
}
