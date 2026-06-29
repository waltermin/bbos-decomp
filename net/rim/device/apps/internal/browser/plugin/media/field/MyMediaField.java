package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.ui.MediaField;

final class MyMediaField extends MediaField {
   CustomFocusOrder _focusManager;
   MediaBrowserField _field;

   public MyMediaField(MediaBrowserField field, long style) {
      super(style);
      this._field = field;
   }

   public final void setFocusManager(CustomFocusOrder focusManager) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if (this._field.isFullscreen()) {
         return 0;
      } else {
         return (status & 1073741824) == 0 && this._focusManager != null ? amount : super.moveFocus(amount, status, time);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      if (this._focusManager != null) {
         this._focusManager.resetFocus();
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._field.isFullscreen()) {
         return true;
      }

      if (super.navigationMovement(dx, dy, status, time)) {
         return true;
      }

      if ((status & 1073741824) == 0 && this._focusManager != null) {
         int adx = Math.abs(dx);
         int ady = Math.abs(dy);
         int vx = 0;
         int vy = 0;
         if (adx > ady) {
            vx = dx;
         } else {
            vy = dy;
         }

         return this._focusManager.moveFocus(vx, vy);
      } else {
         return false;
      }
   }
}
