package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.internal.ui.RichText;

public final class CustomMenuField extends ObjectChoiceField {
   private static final int EDGE_GAP;

   public CustomMenuField(String[] choices, int initialSelection) {
      super(null, choices, initialSelection);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      int fontHeight = this.getFont().getHeight();
      int index = this.getSelectedIndex();
      rect.set(2, index * fontHeight + 2, this.getWidth() - 4, fontHeight);
   }

   @Override
   protected final void layout(int width, int height) {
      int fontHeight = this.getFont().getHeight();
      height = fontHeight * this.getSize();
      width = Math.min(width, this.getWidthOfChoices());
      this.setExtent(width + 4 + 1, height + 4);
   }

   @Override
   protected final void paint(Graphics graphics) {
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      int size = this.getSize();
      int start = graphics.getClippingRect().y / fontHeight;
      int end = Math.min(size, graphics.getClippingRect().Y2() / fontHeight + 1);

      for (int lv = start; lv < end; lv++) {
         String choice = this.getChoice(lv).toString();
         RichText.drawTextWithEllipses(graphics, choice, 3, lv * fontHeight + 2, this.getWidth() - 4, RichText.getLineDirection(choice), 70);
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int selectedIndex = this.getSelectedIndex();
      int size = this.getSize();
      if (amount == 0 || selectedIndex < 0 || size <= 0) {
         return super.moveFocus(amount, status, time);
      } else if (amount > 0) {
         this.setSelectedIndex(Math.min(size - 1, selectedIndex + amount));
         return 0;
      } else {
         this.setSelectedIndex(Math.max(0, selectedIndex + amount));
         return 0;
      }
   }
}
