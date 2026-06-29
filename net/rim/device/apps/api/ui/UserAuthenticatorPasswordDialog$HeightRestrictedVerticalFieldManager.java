package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;

class UserAuthenticatorPasswordDialog$HeightRestrictedVerticalFieldManager extends VerticalFieldManager {
   private int _restrictedHeight;

   public UserAuthenticatorPasswordDialog$HeightRestrictedVerticalFieldManager(long style, int restrictedHeight) {
      super(style);
      this._restrictedHeight = restrictedHeight;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight - this._restrictedHeight);
   }
}
