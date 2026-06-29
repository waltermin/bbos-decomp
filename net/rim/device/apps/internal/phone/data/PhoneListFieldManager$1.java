package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ListField;

class PhoneListFieldManager$1 extends ListField {
   private final PhoneListFieldManager this$0;

   PhoneListFieldManager$1(PhoneListFieldManager _1, int x0, long x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(0);
   }

   @Override
   protected void layout(int width, int height) {
      Font font = this.getFont();
      if (this.getRowHeight() != font.getHeight()) {
         this.setRowHeight(font.getHeight());
      }

      super.layout(width, height);
   }
}
