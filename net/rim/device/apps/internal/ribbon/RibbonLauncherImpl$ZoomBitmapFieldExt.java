package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.ZoomBitmapField;

final class RibbonLauncherImpl$ZoomBitmapFieldExt extends ZoomBitmapField {
   public RibbonLauncherImpl$ZoomBitmapFieldExt(EncodedImage image, int maxLoopIterations, long style) {
      super(image, maxLoopIterations, style);
   }

   final void paintHack(Graphics graphics) {
      this.paint(graphics);
   }
}
