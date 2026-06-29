package net.rim.vm;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.text.TextRect;

class CodeUpgrade$MyTextRect extends TextRect {
   CodeUpgrade$MyTextRect(Field f, String s, int style) {
      super(f, s, style);
   }

   public void draw(Graphics g) {
      this.paint(g);
   }
}
