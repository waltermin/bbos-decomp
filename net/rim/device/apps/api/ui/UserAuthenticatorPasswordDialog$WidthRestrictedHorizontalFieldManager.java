package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.container.HorizontalFieldManager;

class UserAuthenticatorPasswordDialog$WidthRestrictedHorizontalFieldManager extends HorizontalFieldManager {
   private int _restrictedWidth;

   public UserAuthenticatorPasswordDialog$WidthRestrictedHorizontalFieldManager(int restrictedWidth) {
      this._restrictedWidth = restrictedWidth;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth - this._restrictedWidth, maxHeight);
   }
}
