package net.rim.device.apps.api.ribbon;

import net.rim.device.api.ui.Graphics;

public class StringRibbonComponent extends TextRibbonComponent implements TextProviderRibbonComponent {
   public String getText() {
      throw null;
   }

   protected void drawText(Graphics g, String text, int offset, int len, int x, int y, int flags, int width) {
      g.drawText(text, offset, len, x, y, flags, width);
   }

   protected void drawText(Graphics g, String text, int x, int y, int flags, int width) {
      this.drawText(g, text, 0, text.length(), x, y, flags, width);
   }

   @Override
   public void setTargetNode(int node) {
   }

   @Override
   public String getStringText() {
      String text = this.getText();
      return text == null ? "" : text;
   }

   @Override
   public char[] getCharArrayText() {
      String text = this.getText();
      return text == null ? new char[0] : text.toCharArray();
   }

   @Override
   public int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      graphics.pushContext(x, y, width, height, 0, 0);
      if (super._font != null) {
         graphics.setFont(super._font);
      }

      if (super._fgColorSet) {
         graphics.setColor(super._fgColor);
      }

      String text;
      if (!(context instanceof String)) {
         text = this.getText();
      } else {
         text = (String)context;
      }

      if (text != null) {
         this.drawText(graphics, text, x, y, super._align | 64, super._width);
      }

      graphics.popContext();
      return 0;
   }

   protected StringRibbonComponent() {
   }
}
