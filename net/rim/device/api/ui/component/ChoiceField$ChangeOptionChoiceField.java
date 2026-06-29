package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;

public class ChoiceField$ChangeOptionChoiceField extends ChoiceField {
   private final ChoiceField this$0;

   public ChoiceField$ChangeOptionChoiceField(ChoiceField _1, String label, int numChoices, int selectedIndex, long style) {
      super(label, numChoices, selectedIndex, style);
      this.this$0 = _1;
   }

   @Override
   public Object getChoice(int index) {
      return this.this$0.getChoice(index);
   }

   @Override
   public int getWidthOfChoice(int index) {
      return this.this$0.getWidthOfChoice(index);
   }

   @Override
   public int getHeightOfChoices() {
      return this.this$0.getChoiceLineHeight();
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
      if (this.isStyle(134217728) && Ui.getIncreaseDirection() == -1) {
         index = this.getSize() - index - 1;
      }

      int choiceHeight = this.getHeightOfChoices();
      rect.set(0, index * choiceHeight, this.getContentWidth(), choiceHeight);
   }

   @Override
   protected void layout(int width, int height) {
      int choiceHeight = this.getHeightOfChoices();
      height = choiceHeight * this.getSize();
      width = Math.min(width, this.getWidthOfChoices() + 1);
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      int choiceHeight = this.getHeightOfChoices();
      super._selectedX = 0;
      if (super._selectedWidth == 0) {
         super._selectedWidth = this.getSelectedWidth();
      }

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

      int tempLen = this.this$0._lengthOfLongestLine;
      this.this$0._lengthOfLongestLine = 0;
      int start = graphics.getClippingRect().y / choiceHeight;
      int end = Math.min(size, (graphics.getClippingRect().Y2() - 1) / choiceHeight + 1);

      for (int lv = start; lv < end; lv++) {
         int index = this.isStyle(134217728) && Ui.getIncreaseDirection() == -1 ? size - lv - 1 : lv;
         this.drawChoice(index, graphics, 0, lv * choiceHeight, flags, this.getContentWidth());
      }

      this.this$0._lengthOfLongestLine = tempLen;
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if (this.isEditable() && this.this$0._numChoices > 0) {
         this.moveChoiceFocus(amount);
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
