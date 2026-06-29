package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.wica.runtime.ui.internal.MultiFocusable;

class WicletTextEditField$BasicEditFieldMF extends BasicEditField implements MultiFocusable {
   public WicletTextEditField$BasicEditFieldMF(long style) {
      super(style);
   }

   @Override
   public void moveFocus(int x, int y, int status, int time) {
      if (!this.isCursorPositionSet()) {
         this.onFocus(1);
      }

      super.moveFocus(x, y, status, time);
   }

   @Override
   protected void onFocus(int direction) {
      if (direction > 0) {
         this.setCursorPosition(0, 0);
      } else {
         if (direction < 0) {
            this.setCursorPosition(Math.max(0, this.getTextLength()), 0);
         }
      }
   }
}
