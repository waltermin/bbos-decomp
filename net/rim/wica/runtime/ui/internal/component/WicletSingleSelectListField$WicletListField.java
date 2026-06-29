package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.wica.runtime.ui.internal.FontAccentsUtil;
import net.rim.wica.runtime.ui.internal.MultiFocusable;

class WicletSingleSelectListField$WicletListField extends ObjectListField implements MultiFocusable {
   public WicletSingleSelectListField$WicletListField(long style) {
      super(style | 8);
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int remaining = super.moveFocus(amount, status, time);
      if (remaining != amount) {
         this.focusChangeNotify(2);
      }

      return remaining;
   }

   @Override
   public int getPreferredWidth() {
      return Graphics.getScreenWidth() / 3;
   }

   @Override
   public void moveFocus(int x, int y, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void onFocus(int direction) {
      super.onFocus(direction);
      if (direction > 0) {
         this.setSelectedIndex(0);
      } else {
         if (direction < 0) {
            this.setSelectedIndex(this.getSize() - 1);
         }
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      super.applyFont();
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      FontAccentsUtil.drawText(graphics, this.get(listField, index).toString(), 0, y, width);
   }
}
