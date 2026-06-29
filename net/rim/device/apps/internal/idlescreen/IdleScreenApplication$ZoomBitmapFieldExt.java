package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.ZoomBitmapField;

final class IdleScreenApplication$ZoomBitmapFieldExt extends ZoomBitmapField {
   public IdleScreenApplication$ZoomBitmapFieldExt(EncodedImage image) {
      super(36);
      this.setImage(image);
   }

   final void paintHack(Graphics graphics) {
      this.paint(graphics);
   }

   final void layoutHack(int width, int height) {
      this.layout(width, height);
   }
}
