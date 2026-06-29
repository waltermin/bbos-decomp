package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class MySeparatorField extends Field {
   public MySeparatorField() {
      super(0);
   }

   @Override
   public final int getPreferredHeight() {
      return 1;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, Math.min(height, this.getPreferredHeight()));
   }

   @Override
   protected final void paint(Graphics graphics) {
      int graphicsColor = graphics.getColor();
      int graphicsAlpha = graphics.getGlobalAlpha();
      int drawColor = 10066329;
      graphics.setColor(drawColor);
      graphics.setGlobalAlpha(200);
      graphics.drawLine(5, 0, this.getWidth() - 5, 0);
      graphics.setColor(graphicsColor);
      graphics.setGlobalAlpha(graphicsAlpha);
   }
}
