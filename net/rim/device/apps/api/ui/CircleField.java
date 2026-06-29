package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public final class CircleField extends Field {
   int _radius;
   private int _colour;

   public CircleField(int radius, int colour) {
      this._radius = radius;
      this._colour = colour;
   }

   @Override
   public final int getPreferredWidth() {
      return 2 * this._radius + 2;
   }

   @Override
   public final int getPreferredHeight() {
      return 2 * this._radius + 2;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   protected final void paint(Graphics graphics) {
      graphics.setColor(this._colour);
      graphics.fillArc(1, 1, 2 * this._radius, 2 * this._radius, 0, 360);
   }
}
