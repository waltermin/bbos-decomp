package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Graphics;

public final class EllipsesText {
   public static final void draw(Graphics graphics, String text, int x, int y, int width) {
      if (graphics.getFont().getAdvance(text) > width) {
         int quarterWidth = width >> 2;
         width -= graphics.drawText(text, x + width - quarterWidth, y, 133, quarterWidth);
      }

      graphics.drawText(text, x, y, 70, width);
   }

   public static final void draw(Graphics graphics, StringBuffer text, int x, int y, int width) {
      if (graphics.getFont().getAdvance(text, 0, text.length()) > width) {
         int quarterWidth = width >> 2;
         width -= graphics.drawText(text, 0, text.length(), x + width - quarterWidth, y, 133, quarterWidth);
      }

      graphics.drawText(text, 0, text.length(), x, y, 70, width);
   }
}
