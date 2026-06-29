package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.ui.PhoneEventListenerStatusDialog;

final class LineSwitchDialog extends PhoneEventListenerStatusDialog {
   private boolean _shouldShow = true;
   private boolean _isShowing = false;

   public LineSwitchDialog() {
      super(CommonResources.getString(9156), true, false, 0);
   }

   @Override
   public final void showModal() {
      if (this._shouldShow) {
         this._isShowing = true;
         super.showModal();
      }
   }

   @Override
   protected final void onEvent(int eventId, int param, Object context) {
      if (eventId == 150130) {
         this._shouldShow = false;
         if (this._isShowing) {
            this.close(eventId);
         }
      }
   }
}
