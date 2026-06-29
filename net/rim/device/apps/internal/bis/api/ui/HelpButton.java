package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public final class HelpButton extends BitmapField {
   public HelpButton() {
      super(null, 18014398509481984L);
      this.setEditable(true);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == '\n') {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.isEditable()) {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (on) {
         graphics.invert(0, 0, this.getBitmapWidth(), this.getBitmapHeight());
      } else {
         graphics.pushContext(this.getPaddingLeft(), this.getPaddingTop(), this.getBitmapWidth(), this.getBitmapHeight(), 0, 0);
         graphics.clear();
         this.paint(graphics);
         graphics.popContext();
      }
   }
}
