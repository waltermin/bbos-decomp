package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.PopupScreen;

final class BaseCallDisplayPopup extends PopupScreen {
   private static final int _SCALE_FACTOR = 9;
   private static final int _OUTSIDE_SPACE = 1;
   private static final int _INSIDE_SPACE = 3;

   public BaseCallDisplayPopup() {
      super((Manager)(new Object()), 0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int borderTop = Math.max(3, this.getBorderTop());
      int borderLeft = Math.max(3, this.getBorderLeft());
      int borderRight = Math.max(3, this.getBorderRight());
      int borderBottom = Math.max(3, this.getBorderBottom());
      this.setPositionDelegate(borderLeft, borderTop);
      int delegateWidth = Display.getWidth() - borderLeft - borderRight - this.getMarginRight() - this.getMarginLeft();
      int delegateHeight = Display.getHeight() - borderTop - borderBottom - this.getMarginTop() - this.getMarginBottom();
      this.layoutDelegate(delegateWidth, delegateHeight);
      XYRect fmExtent = this.getDelegate().getExtent();
      int newX = width - fmExtent.width - borderLeft - borderRight >> 1;
      int newY = height - fmExtent.height - borderTop - borderBottom >> 1;
      this.setPosition(newX, newY);
      this.setExtent(fmExtent.width + borderLeft + borderRight, fmExtent.height + borderTop + borderBottom);
   }
}
