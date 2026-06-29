package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;

class BackdoorHelpScreen$1 extends SelfDrawingListField {
   private final String[] val$strings;
   private final BackdoorHelpScreen this$0;

   BackdoorHelpScreen$1(BackdoorHelpScreen _1, int x0, long x1, String[] _5) {
      super(x0, x1);
      this.this$0 = _1;
      this.val$strings = _5;
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this.val$strings[index], 0, y);
   }
}
