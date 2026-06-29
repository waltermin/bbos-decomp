package net.rim.wica.runtime.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;

public final class ImageUtilities {
   public static final Bitmap paintOverlay(Bitmap background, int x, int y, Bitmap overlay) {
      Bitmap buffer = (Bitmap)(new Object(background.getWidth(), background.getHeight()));
      buffer.createAlpha(2);
      Graphics gfx = (Graphics)(new Object(buffer));
      gfx.setGlobalAlpha(0);
      gfx.clear();
      gfx.setGlobalAlpha(255);
      gfx.drawBitmap(0, 0, background.getWidth(), background.getHeight(), background, 0, 0);
      gfx.drawBitmap(x, y, overlay.getWidth(), overlay.getHeight(), overlay, 0, 0);
      return buffer;
   }
}
