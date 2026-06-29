package net.rim.device.api.ui.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.InPlaceScreen;
import net.rim.device.internal.ui.UiInternal;

class ChoiceInPlaceScreen extends InPlaceScreen {
   private XYRect _rect = new XYRect();

   public ChoiceInPlaceScreen(ChoiceField original, ChoiceField fake, long style) {
      super(original, fake, style | 281474976710656L | 1);
   }

   @Override
   protected void applyFont() {
      ChoiceField original = (ChoiceField)this.getOriginalField();
      ChoiceField fake = (ChoiceField)this.getField();
      fake.setFont(original.getFontIfSet());
   }

   @Override
   protected void onDisplay() {
      UiInternal.setKeyStateIconsVisible(false);
      super.onDisplay();
   }

   @Override
   protected void onUndisplay() {
      UiInternal.setKeyStateIconsVisible(true);
      super.onUndisplay();
   }

   @Override
   protected void sublayout(int width, int height) {
      ChoiceField original = (ChoiceField)this.getOriginalField();
      int choiceHeight = original.getChoiceLineHeight();
      int borderAndPaddingLeft = this.getPaddingLeft() + this.getBorderLeft();
      int borderAndPaddingTop = this.getPaddingTop() + this.getBorderTop();
      int borderAndPaddingWidth = borderAndPaddingLeft + this.getPaddingRight() + this.getBorderRight();
      int borderAndPaddingHeight = borderAndPaddingTop + this.getPaddingBottom() + this.getBorderBottom();
      XYRect extent = this._rect;
      original.getInPlaceRect(extent);
      original.transformToScreen(extent);
      this.layoutDelegate(width, height);
      this.setPositionDelegate(0, 0);
      ChoiceField fake = (ChoiceField)this.getField();
      int fakeHeight = fake.getHeight();
      extent.height = fakeHeight + borderAndPaddingHeight;
      extent.width = fake.getWidth() + borderAndPaddingWidth;
      extent.x -= borderAndPaddingLeft;
      extent.y -= borderAndPaddingTop;
      if (fakeHeight > 3 * choiceHeight >> 1) {
         int index = original.getSelectedIndex();
         if (this.isStyle(134217728) && Ui.getIncreaseDirection() == -1) {
            index = original.getSize() - index - 1;
         }

         extent.y -= choiceHeight * index;
      }

      extent.x = extent.x < 0 ? 0 : extent.x;
      extent.y = extent.y < 0 ? 0 : extent.y;
      int screenWidth = Display.getWidth();
      extent.width = extent.width > screenWidth ? screenWidth : extent.width;
      int screenHeight = Display.getHeight();
      extent.height = extent.height > screenHeight ? screenHeight : extent.height;
      int rightOverextend = extent.X2() - screenWidth;
      if (rightOverextend > 0) {
         extent.x -= rightOverextend;
      }

      int bottomOverextend = extent.Y2() - screenHeight;
      if (bottomOverextend > 0) {
         extent.y -= bottomOverextend;
      }

      extent.width -= borderAndPaddingWidth;
      extent.height -= borderAndPaddingHeight;
      this.setPosition(extent.x, extent.y);
      this.setExtent(extent.width, extent.height);
   }
}
