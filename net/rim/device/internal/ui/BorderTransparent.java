package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class BorderTransparent extends Border {
   public BorderTransparent(int top, int right, int bottom, int left) {
      super(top, right, bottom, left);
      this.setTransparent(true);
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
   }
}
