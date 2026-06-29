package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.apps.internal.ribbon.skin.svg.CustomFocusOrder;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.mediaengine.MediaPlayer;

class DirectionalSkinManager extends SkinManager {
   private FocusInteractor _focusInteractor;
   private MediaLayout _mediaLayout;
   private CustomFocusOrder _customFocusOrder;
   private int _currentFocus;

   @Override
   public void setServices(MediaServices services) {
      if (services != null) {
         this._focusInteractor = (FocusInteractor)services.getService("FocusInteractor");
         this._mediaLayout = (MediaLayout)((MediaPlayer)services.getService("MediaPlayer")).getUI();
      }
   }

   @Override
   public void setMedia(Object media) {
      this._customFocusOrder = this._mediaLayout.getCustomFocusOrder();
   }

   @Override
   public boolean navigationMovement(int dx, int dy, int status, int time) {
      this._currentFocus = this._focusInteractor.getItemInFocus();
      int direction;
      if (dx < 0) {
         direction = 0;
      } else if (dx > 0) {
         direction = 1;
      } else if (dy < 0) {
         direction = 2;
      } else {
         direction = 3;
      }

      int target = this._customFocusOrder.getTargetFocusOf(this._currentFocus, direction);
      if (target != -1) {
         this._focusInteractor.setFocusToItem(target);
      }

      return true;
   }
}
