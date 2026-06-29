package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.internal.ui.ArticInterface$LineInfo;

final class LabelSeparatorField extends RichTextField {
   LabelSeparatorField() {
      super(1188950301626073088L);
   }

   LabelSeparatorField(String label, Tag tag) {
      this();
      this.setText(label);
      if (tag != null) {
         this.setTag(tag);
      }
   }

   @Override
   protected final void paint(Graphics g) {
      ArticInterface$LineInfo info = this.getLineInfoForDocPos(0, true);
      int lineWidth = info._line._boundsRight - info._line._boundsLeft;
      int width = this.getWidth();
      int h = info._line._boundsBottom - info._line._boundsTop >> 1;
      int x1 = width - lineWidth >> 1;
      int x2 = x1 + lineWidth;
      g.drawLine(0, h, x1, h);
      g.drawLine(x2, h, width, h);
      super.paint(g);
   }
}
