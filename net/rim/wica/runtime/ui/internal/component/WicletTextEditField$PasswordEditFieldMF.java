package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.wica.runtime.ui.internal.MultiFocusable;

class WicletTextEditField$PasswordEditFieldMF extends PasswordEditField implements MultiFocusable {
   public WicletTextEditField$PasswordEditFieldMF(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
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
