package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

class WicletCheckboxListField$MandatoryLabel extends LabelField {
   private final WicletCheckboxListField this$0;

   WicletCheckboxListField$MandatoryLabel(WicletCheckboxListField this$0, Object text, long style) {
      super(text, style);
      this.this$0 = this$0;
      Font currentFont = this.getFont();
      this.setFont(currentFont.derive(1, currentFont.getHeight() / 2));
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this.this$0._isMandatorySatisfied) {
         graphics.setColor(32768);
      } else {
         graphics.setColor(16711680);
      }

      super.paint(graphics);
   }

   private void invalidateMe() {
      this.invalidate();
   }
}
