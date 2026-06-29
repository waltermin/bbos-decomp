package javax.microedition.lcdui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;

class CustomField extends Field {
   private CustomItem _customItem;
   private Graphics _graphics;
   private boolean _isVisible;
   private int _translateOffset = 0;
   private boolean _traverseSuccessful = false;
   private int[] _focusRectInfo;

   public CustomField(CustomItem customItem) {
      super(18014398509481984L);
      this._customItem = customItem;
      this._graphics = new Graphics();
   }

   @Override
   protected void layout(int width, int height) {
      int oldWidth = this.getWidth();
      int oldHeight = this.getHeight();
      int newWidth = Math.min(this._customItem.getPrefContentWidth(-1), width);
      int newHeight = Math.min(this._customItem.getPrefContentHeight(-1), height);
      this.setExtent(newWidth, newHeight);
      if (newWidth != oldWidth || newHeight != oldHeight) {
         this._customItem.sizeChanged(newWidth, newHeight);
      }
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      if (visible != this._isVisible) {
         this._isVisible = visible;
         if (visible) {
            this._customItem.showNotify();
            return;
         }

         this._customItem.hideNotify();
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int dir;
      if ((status & 1) == 1) {
         if (amount > 0) {
            dir = 5;
         } else if (amount < 0) {
            dir = 2;
         } else {
            dir = 0;
         }
      } else if (amount > 0) {
         dir = 6;
      } else if (amount < 0) {
         dir = 1;
      } else {
         dir = 0;
      }

      XYRect focusRect = new XYRect(0, 0, 0, 0);
      this.getFocusRect(focusRect);
      this._focusRectInfo = new int[]{focusRect.x, focusRect.y, focusRect.width, focusRect.height};
      int iterate = amount < 0 ? 0 - amount : amount;

      for (int i = 0; i < iterate; i++) {
         this._traverseSuccessful = this._customItem.traverse(dir, this.getWidth(), this.getHeight(), this._focusRectInfo);
         if (!this._traverseSuccessful) {
            if (amount < 0) {
               return amount + i;
            }

            return amount - i;
         }
      }

      return 0;
   }

   @Override
   protected void onFocus(int direction) {
      XYRect focusRect = new XYRect(0, 0, 0, 0);
      this.getFocusRect(focusRect);
      this._focusRectInfo = new int[]{focusRect.x, focusRect.y, focusRect.width, focusRect.height};
      if (direction < 0) {
         this._traverseSuccessful = this._customItem.traverse(1, this.getWidth(), this.getHeight(), this._focusRectInfo);
      } else {
         if (direction > 0) {
            this._traverseSuccessful = this._customItem.traverse(6, this.getWidth(), this.getHeight(), this._focusRectInfo);
         }
      }
   }

   @Override
   protected void onUnfocus() {
      this._translateOffset = 0;
      super.onUnfocus();
      this._customItem.traverseOut();
   }

   @Override
   protected void drawFocus(net.rim.device.api.ui.Graphics graphics, boolean on) {
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      char c = Keypad.map(keycode);
      if (c == 27) {
         return false;
      } else if (0 != c) {
         this._customItem.keyPressed(c);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      char c = Keypad.map(keycode);
      if (0 != c) {
         this._customItem.keyReleased(c);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void paint(net.rim.device.api.ui.Graphics graphics) {
      if (this._graphics.getPeer() == null) {
         this._graphics.setGraphics(graphics, true);
      }

      if (this._focusRectInfo != null && this._traverseSuccessful) {
         int screenHeight = this._graphics.getClipHeight() - this._focusRectInfo[3];
         if (this._translateOffset + this._focusRectInfo[1] < 0) {
            this._translateOffset = -this._focusRectInfo[1];
         } else if (this._translateOffset + this._focusRectInfo[1] > screenHeight) {
            this._translateOffset = screenHeight - this._focusRectInfo[1];
         }

         this._traverseSuccessful = false;
      }

      graphics.translate(0, this._translateOffset);
      this._graphics.setGraphics(graphics, true);
      this._customItem.paint(this._graphics, this.getWidth(), this.getHeight());
   }

   public void redoLayout() {
      this.updateLayout();
   }

   public void callInvalidate() {
      this.invalidate();
   }

   public void callInvalidate(int x, int y, int width, int height) {
      this.invalidate(x, y, width, height);
   }
}
