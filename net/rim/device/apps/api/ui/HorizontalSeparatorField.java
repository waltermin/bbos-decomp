package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class HorizontalSeparatorField extends Field {
   private static final int WIDTH_THICK = 3;
   private static final int DRAW_POS_THICK = 1;

   @Override
   public int getPreferredWidth() {
      return 3;
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(3, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      graphics.drawLine(1, 0, 1, this.getHeight());
   }
}
