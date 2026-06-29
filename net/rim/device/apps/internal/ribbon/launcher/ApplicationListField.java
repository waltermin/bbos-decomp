package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;

final class ApplicationListField extends ListField {
   private XYRect _focusRect = (XYRect)(new Object());
   private int _deltaY;

   ApplicationListField(int size, int count) {
      super(size, count);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      this.getFocusRect(this._focusRect);
      this.drawHighlightRegion(graphics, 1, on, this._focusRect.x, this._focusRect.y, this._focusRect.width, this._focusRect.height);
   }

   @Override
   public final void getFocusRectPhantom(XYRect rect) {
      Manager manager = this.getManager();
      int verticalScroll = manager.getVerticalScroll();
      int height = Math.min(this.getContentHeight(), manager.getVisibleHeight());
      rect.set(this._focusRect);
      rect.y = rect.y + this._deltaY;
      this._deltaY = 0;
      int offset = height >> 1;
      int oldY = rect.y;
      if (rect.y >= verticalScroll && rect.y != 0) {
         if (rect.y + rect.height + offset >= verticalScroll + height) {
            int bottom = rect.y + rect.height;
            if (rect.y < this._focusRect.y) {
               rect.y = rect.y - (offset - rect.height);
            }

            rect.height += offset;
            if (rect.y + rect.height > this.getHeight()) {
               rect.y = this.getHeight() - height;
               rect.height = height;
            }

            if (rect.y + rect.height != bottom) {
               manager.invalidate();
            }
         } else {
            rect.y = rect.y - (offset - rect.height);
            rect.height += offset;
            if (rect.y < 0) {
               rect.height = rect.height + rect.y;
               rect.y = 0;
            }
         }
      } else {
         rect.y = rect.y - (offset - rect.height);
         rect.height += offset;
         if (rect.y < 0) {
            rect.height = rect.height + rect.y;
            rect.y = 0;
         }

         if (rect.y != oldY) {
            manager.invalidate();
         }
      }

      if (rect.y != this._focusRect.y) {
         manager.invalidate();
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      Manager manager = this.getManager();
      int contentHeight = this.getContentHeight();
      int height = Math.min(contentHeight, manager.getVisibleHeight());
      if ((status & 9) != 0) {
         amount *= height / this._focusRect.height;
         status &= -2;
      }

      this._deltaY = this._focusRect.height * amount;
      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      Manager manager = this.getManager();
      int contentHeight = this.getContentHeight();
      int height = Math.min(contentHeight, manager.getVisibleHeight());
      if ((status & 9) != 0) {
         dy *= height / this._focusRect.height;
         status &= -2;
      }

      this._deltaY = this._focusRect.height * dy;
      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if ((status & 9) != 0) {
         amount *= this._deltaY / this._focusRect.height;
         if (this._deltaY < 0) {
            amount *= -1;
         }

         status &= -2;
      }

      return super.moveFocus(amount, status, time);
   }

   @Override
   public final void setSelectedIndex(int index) {
      int oldIndex = this.getSelectedIndex();
      int dy = index - oldIndex;
      if (dy != 0 && this._deltaY == 0) {
         this._deltaY = this._focusRect.height * dy;
      }

      super.setSelectedIndex(index);
      this.getFocusRect(this._focusRect);
   }
}
