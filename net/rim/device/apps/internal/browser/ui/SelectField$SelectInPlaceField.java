package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.SystemIcon;

final class SelectField$SelectInPlaceField extends Field {
   private int _top;
   private boolean _alted;
   private boolean _firstPress;
   private final SelectField this$0;

   public SelectField$SelectInPlaceField(SelectField _1, int index, boolean alted) {
      super(22517998136852480L);
      this.this$0 = _1;
      this._firstPress = true;
      this._top = index;
      this._alted = alted;
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      rect.set(0, this._top * this.this$0._lineHeight, this.getWidth(), this.this$0._lineHeight);
   }

   @Override
   protected final void layout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      height = this.this$0._lineHeight * this.this$0._options.length;
      this.setExtent(width, height);
   }

   @Override
   public final int getPreferredWidth() {
      int width = this.this$0.getWidthOfWidestOption(true);
      if (this.this$0._multiple) {
         width += SystemIcon.COLLECTION.getWidth(this.this$0._lineHeight, this.this$0._lineHeight);
      }

      return width;
   }

   @Override
   public final int getPreferredHeight() {
      return this.this$0._lineHeight * this.this$0._options.length;
   }

   @Override
   public final void paint(Graphics graphics) {
      int start = graphics.getClippingRect().y / this.this$0._lineHeight;
      int end = Math.min(this.this$0._options.length, graphics.getClippingRect().Y2() / this.this$0._lineHeight + 1);
      int width = this.getWidth();
      int x = 0;
      int y = 0;
      int paragraphOrdering = RichText.getParagraphOrdering(this.getStyle());

      for (int index = start; index < end; index++) {
         y = index * this.this$0._lineHeight;
         x = 0;
         byte flags = this.this$0._flags[index];
         boolean optgroup = (flags & 4) != 0;
         graphics.setFont(optgroup ? this.this$0._optgroupFont : this.this$0._optionFont);
         if ((flags & 8) != 0) {
            x += 10;
         }

         if (this.this$0._multiple && !optgroup) {
            SystemIcon.COLLECTION.paint(graphics, x, y, this.this$0._iconWidth, this.this$0._iconHeight, this.this$0.isOptionSetInternal(index) ? 1 : 0);
            x += this.this$0._iconWidth;
         }

         x++;
         if (this.this$0._options[index] != null) {
            RichText.drawTextWithEllipses(graphics, this.this$0._options[index], x, y, width - x, paragraphOrdering, 0);
         }
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int size = this.this$0._options.length;
      if (this.isEditable() && size > 0) {
         int oldIndex = this.this$0._index;
         int newTop = this._top;
         int index = newTop;
         int increment = amount < 0 ? -1 : 1;

         while (amount != 0 && (increment < 0 ? index > 0 : index < size - 1)) {
            index += increment;
            if ((this.this$0._flags[index] & 4) == 0) {
               newTop = index;
               amount -= increment;
            }
         }

         this._top = newTop;
         if (newTop != oldIndex) {
            this.invalidate();
         }

         return 0;
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean result = false;
      if (this.isEditable()) {
         if (key == ' ') {
            this.this$0.toggleOptionInternal(this._top, false);
            this.invalidate();
         } else if (key == '\n') {
            this.this$0.toggleOptionInternal(this._top, false);
            this.invalidate();
            if (!this.this$0._multiple) {
               this.getScreen().close();
            }
         } else if (key == 27) {
            this.getScreen().close();
         } else {
            if (this._alted && this._firstPress) {
               key = Keypad.getUnaltedChar(key);
            }

            if (this.this$0._numeric && !Character.isDigit(key)) {
               key = Keypad.getAltedChar(key);
            } else {
               key = Character.toLowerCase(key);
            }

            int index = this.findNextItem(key);
            if (index == -1) {
               key = Keypad.getAltedChar(Character.toUpperCase(key));
               index = this.findNextItem(key);
            }

            if (index != -1 && index != this._top) {
               this._top = index;
               this.focusAdd(false);
               this.invalidate();
            }
         }

         result = true;
      }

      this._firstPress = false;
      return result;
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      Screen screen = this.getScreen();
      if ((screen.getStyle() & 1) != 0 && Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0) {
         this.this$0.toggleOptionInternal(this._top, false);
         screen.close();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.this$0.toggleOptionInternal(this._top, false);
      this.invalidate();
      if (!this.this$0._multiple) {
         this.getScreen().close();
      }

      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.navigationClick(status, time);
   }

   private final int findNextItem(char key) {
      int size = this.this$0._options.length;
      if (size == 0) {
         return -1;
      }

      StringBuffer sb = Keypad.getLayout().getComplementaryChars(key, 0);
      char[] keys = null;
      if (sb == null) {
         keys = new char[0];
      } else {
         int len = sb.length();
         keys = new char[len];

         for (int i = 0; i < len; i++) {
            keys[i] = sb.charAt(i);
         }
      }

      for (int index = this._top + 1; index != this._top; index++) {
         if (index == size) {
            index = -1;
         } else if (this.this$0._options[index] != null) {
            String item = this.this$0._options[index];
            if (item.length() > 0 && this.this$0.containsKey(item.charAt(0), keys) && (this.this$0._flags[index] & 4) == 0) {
               return index;
            }
         }
      }

      return -1;
   }
}
