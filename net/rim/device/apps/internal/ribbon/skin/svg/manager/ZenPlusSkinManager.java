package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.ribbon.skin.svg.CustomFocusOrder;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class ZenPlusSkinManager extends SkinManager {
   private FocusInteractor _focusInteractor;
   private MediaLayout _mediaLayout;
   private int[] _vertical;
   private int[] _horizontal;
   private int[] _pivot;
   private int[] _currentGroup;
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
      CustomFocusOrder customFocusOrder = this._mediaLayout.getCustomFocusOrder();
      this._vertical = customFocusOrder.getGroup(0);
      this._pivot = customFocusOrder.getGroup(1);
      this._horizontal = customFocusOrder.getGroup(2);
      this._currentFocus = this._vertical.length > 0 ? this._vertical[0] : -1;
      this._currentGroup = this._vertical;
      this._focusInteractor.setFocusToItem(this._currentFocus);
   }

   @Override
   public boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._currentGroup == this._vertical) {
         if (dx > 0) {
            dx--;
            this._currentGroup = this._horizontal;
            this._currentFocus = this._horizontal[dx >= this._horizontal.length ? this._horizontal.length - 1 : dx];
         } else if (dy != 0) {
            int index = Arrays.getIndex(this._vertical, this._currentFocus);
            if (index < 0) {
               return true;
            }

            index += dy;
            if (index < 0) {
               index = 0;
            }

            if (index >= this._vertical.length) {
               this._currentGroup = this._pivot;
               this._currentFocus = this._pivot[0];
            } else {
               this._currentFocus = this._vertical[index];
            }
         }
      } else if (this._currentGroup == this._horizontal) {
         if (dy < 0) {
            int index = this._vertical.length + dy;
            this._currentGroup = this._vertical;
            this._currentFocus = this._vertical[index < 0 ? 0 : index];
         } else if (dx != 0) {
            int index = Arrays.getIndex(this._horizontal, this._currentFocus);
            if (index < 0) {
               return true;
            }

            index += dx;
            if (index >= this._horizontal.length) {
               index = this._horizontal.length - 1;
            }

            if (index < 0) {
               this._currentGroup = this._pivot;
               this._currentFocus = this._pivot[0];
            } else {
               this._currentFocus = this._horizontal[index];
            }
         }
      } else if (dx > 0) {
         int index = dx - 1;
         this._currentGroup = this._horizontal;
         this._currentFocus = this._horizontal[index >= this._horizontal.length ? this._horizontal.length - 1 : index];
      } else if (dy < 0) {
         int index = this._vertical.length + dy;
         this._currentGroup = this._vertical;
         this._currentFocus = this._vertical[index < 0 ? 0 : index];
      }

      this._focusInteractor.setFocusToItem(this._currentFocus);
      return true;
   }
}
