package net.rim.device.apps.api.ribbon;

import net.rim.device.api.ui.Graphics;

public class StringBufferRibbonComponent extends TextRibbonComponent implements TextProviderRibbonComponent {
   protected StringBuffer getText() {
      throw null;
   }

   protected int drawText(Graphics graphics, StringBuffer text, int offset, int len, int x, int y, int flags, int width) {
      return graphics.drawText(text, offset, len, x, y, flags, width);
   }

   protected int drawText(Graphics graphics, StringBuffer text, int x, int y, int flags, int width) {
      return this.drawText(graphics, text, 0, text.length(), x, y, flags, width);
   }

   @Override
   public void setTargetNode(int node) {
   }

   @Override
   public String getStringText() {
      StringBuffer textBuff = this.getText();
      if (textBuff != null) {
         String text = textBuff.toString();
         return text == null ? "" : text;
      } else {
         return "";
      }
   }

   @Override
   public char[] getCharArrayText() {
      StringBuffer text = this.getText();
      return text == null ? new char[0] : text.toString().toCharArray();
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

      StringBuffer text;
      if (!(context instanceof Object)) {
         text = this.getText();
      } else {
         text = (StringBuffer)context;
      }

      if (text != null) {
         synchronized (text) {
            this.drawText(graphics, text, x, y, super._align, super._width);
         }
      }

      graphics.popContext();
      return 0;
   }

   protected StringBufferRibbonComponent() {
   }
}
