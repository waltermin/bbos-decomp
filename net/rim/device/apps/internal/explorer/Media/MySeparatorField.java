package net.rim.device.apps.internal.explorer.Media;

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
      graphics.setGlobalAlpha(125);
      graphics.drawLine(17, 0, this.getWidth() - 17, 0);
      graphics.setColor(graphicsColor);
      graphics.setGlobalAlpha(graphicsAlpha);
   }
}
