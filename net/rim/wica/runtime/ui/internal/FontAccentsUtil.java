package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;

public class FontAccentsUtil {
   public static int getExtraHeightToFitAccents(Font font, String text, int width) {
      if (text == null) {
         return 0;
      }

      TextMetrics tm = new TextMetrics();
      DrawTextParam dtp = new DrawTextParam(width);
      dtp.iTruncateWithEllipsis = 2;
      font.measureText(text, 0, text.length(), dtp, tm);
      int extraHeight = font.getBaseline() + tm.iBoundsTlY;
      return extraHeight < 0 ? -extraHeight : 0;
   }

   public static void drawText(Graphics graphics, String text, int x, int y, int width) {
      Font font = graphics.getFont();
      int extraHeight = getExtraHeightToFitAccents(font, text, width);
      if (extraHeight > 0) {
         int fontHeight = font.getHeight();
         int scaledFontHeight = fontHeight * fontHeight / (extraHeight + fontHeight);
         Font scaledFont = font.derive(font.getStyle(), scaledFontHeight);
         extraHeight = getExtraHeightToFitAccents(scaledFont, text, width);
         graphics.setFont(scaledFont);
         y += extraHeight;
      }

      graphics.drawText(text, x, y, 64, width);
      graphics.setFont(font);
   }
}
