package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.RichTextField;

final class PhoneInfoOption$CopyableNumberField extends RichTextField {
   PhoneInfoOption$CopyableNumberField(String text, long style) {
      super(text, style);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      XYRect rct = this.getExtent();
      rect.set(0, 0, rct.width, rct.height);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      XYRect rect = (XYRect)(new Object());
      this.getFocusRect(rect);
      this.highlightSelectedArea(graphics, on, 0, this.getTextLength());
      this.drawHighlightRegion(graphics, 1, on, rect.x, rect.y, rect.width, rect.height);
   }

   @Override
   public final ContextMenu getContextMenu() {
      return ContextMenu.getInstance();
   }
}
