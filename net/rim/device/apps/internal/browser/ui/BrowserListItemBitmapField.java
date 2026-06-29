package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import org.w3c.dom.html2.HTMLElement;

public class BrowserListItemBitmapField extends BrowserBitmapField {
   private int _color;

   public BrowserListItemBitmapField(BrowserContentBaseImpl browserField, String imageUrl, long style, String altText, HTMLElement element) {
      super(browserField, null, imageUrl, style, false, -1, -1, 1, altText, element);
   }

   public void setColor(int color) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void layout(int width, int height) {
      if (this.getImage() != null || this.getBitmap() != null) {
         label30:
         try {
            super.layout(width, height);
            return;
         } finally {
            break label30;
         }
      }

      int textWidth = 0;
      int textHeight = 0;
      String altText = this.getAlt();
      if (altText != null) {
         Font font = this.getFont();
         textWidth = font.measureText(altText, 0, altText.length(), null, null);
         textHeight = font.getHeight();
      }

      this.setExtent(textWidth, textHeight);
   }

   @Override
   protected void paint(Graphics graphics) {
      boolean quickDraw = graphics.isDrawingStyleSet(32);
      if (!quickDraw && (this.getImage() != null || this.getBitmap() != null)) {
         label35:
         try {
            super.paint(graphics);
            return;
         } finally {
            break label35;
         }
      }

      if (!quickDraw) {
         String altText = this.getAlt();
         if (altText != null) {
            int oldColor = graphics.getColor();
            graphics.setColor(this._color);
            graphics.drawText(altText, 0, this.getFont().getHeight(), 8);
            graphics.setColor(oldColor);
         }
      }
   }
}
