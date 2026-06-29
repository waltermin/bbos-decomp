package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;

class NumericChoiceField$1 extends NumericChoiceField {
   private final NumericChoiceField this$0;

   NumericChoiceField$1(NumericChoiceField _1, String x0, int x1, int x2, int x3, int x4) {
      super(x0, x1, x2, x3, x4);
      this.this$0 = _1;
   }

   @Override
   public int getHeightOfChoices() {
      return this.this$0.getHeightOfChoices();
   }

   @Override
   protected void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      this.this$0.drawChoice(index, graphics, x, y, flags, width);
   }

   @Override
   public Field getOriginal() {
      return this.this$0;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int index = this.getSelectedIndex();
      if (Ui.getIncreaseDirection() == -1) {
         index = this.getSize() - index - 1;
      }

      int choiceHeight = this.getHeightOfChoices();
      rect.set(0, index * choiceHeight, this.getContentWidth(), choiceHeight);
   }

   @Override
   protected void layout(int width, int height) {
      int choiceHeight = this.getHeightOfChoices();
      height = choiceHeight * this.getSize();
      width = Math.min(width, this.getWidthOfChoices());
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      int choiceHeight = this.getHeightOfChoices();
      super._selectedX = 0;
      super._selectedWidth = this.getSelectedWidth();
      int size = this.getSize();
      int flags = 64;
      switch ((int)((this.getStyle() & 12884901888L) >>> 32)) {
         case -1:
            break;
         case 0:
            if (super._isLabelOwnLine) {
               super._selectedX = 0;
               flags |= 6;
               break;
            }
         case 2:
            flags |= 5;
            break;
         case 1:
         default:
            flags |= 6;
            break;
         case 3:
            flags |= 4;
      }

      int start = graphics.getClippingRect().y / choiceHeight;
      int end = Math.min(size, graphics.getClippingRect().Y2() / choiceHeight + 1);

      for (int lv = start; lv < end; lv++) {
         int index = Ui.getIncreaseDirection() == -1 ? size - lv - 1 : lv;
         this.drawChoice(index, graphics, 0, lv * choiceHeight, flags, this.getContentWidth());
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if (this.isEditable() && super.getSize() > 0) {
         super.moveChoiceFocus(amount);
         return 0;
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      if (x > 0 && y > 0 && x < this.getContentWidth() && y < this.getContentHeight()) {
         int choiceHeight = this.getHeightOfChoices();
         this.setSelectedIndex(y / choiceHeight, 2);
      }
   }
}
