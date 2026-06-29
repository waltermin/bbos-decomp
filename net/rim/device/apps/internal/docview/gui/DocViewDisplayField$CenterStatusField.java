package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class DocViewDisplayField$CenterStatusField extends LabelField {
   DocViewDisplayField$CenterStatusField(String title, long style, Font initialFont) {
      super(title, style);
      if (initialFont != null) {
         this.setFont(initialFont);
      }
   }

   @Override
   protected void paint(Graphics g) {
      int h = this.getPreferredHeight() >> 1;
      int width = this.getPreferredWidth();
      int x1 = DocViewGUIInternalConstants.SCREEN_WIDTH - width >> 1;
      int x2 = x1 + width;
      g.drawLine(0, h, x1, h);
      g.drawLine(x2, h, DocViewGUIInternalConstants.SCREEN_WIDTH, h);
      super.paint(g);
   }
}
