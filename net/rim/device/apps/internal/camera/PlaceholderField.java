package net.rim.device.apps.internal.camera;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.RichTextField;

public final class PlaceholderField extends RichTextField {
   private int mywidth;
   private int myheight;

   public PlaceholderField(int width, int height, String label) {
      super(label, 36028809903865856L);
      this.mywidth = width;
      this.myheight = height;
   }

   @Override
   protected final void layout(int width, int height) {
      super.layout(this.mywidth, this.myheight);
      this.setExtent(this.mywidth, this.myheight);
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect extent = this.getExtent();
      graphics.drawRect(extent.x, extent.y, extent.width, extent.height);
      super.paint(graphics);
   }
}
