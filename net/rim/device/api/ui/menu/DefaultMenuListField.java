package net.rim.device.api.ui.menu;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontLogicHelper;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.Image;
import net.rim.tid.im.layout.SLKeyLayout;

class DefaultMenuListField extends Field implements MenuList {
   private MenuItem[] _items;
   private byte[] _heights;
   private byte[] _offsets;
   private int _focusHeight;
   private int _fontHeight;
   private int _separatorHeight = 3;
   private int _selection;
   private boolean _focusOn;
   private int _contentPaddingLeft = 1;
   private int _contentPaddingRight = 1;
   private Font _font;
   private ThemeAttributeSet _tasFocus;
   private Font _fontFocus;
   private boolean _focusDrawWithInvalidate;
   private static Tag TAG = Tag.create("menuitem");

   DefaultMenuListField() {
      super(18014398509481984L);
      this.setTag(TAG);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._font = this.getFont();
      int locale = Locale.getDefaultForSystem().getCode();
      if (!FontLogicHelper.fontLegible(this._font, locale)) {
         this._font = FontLogicHelper.getSuggestedFont(this._font, locale, true);
      }

      Theme theme = ThemeManager.getActiveTheme();
      this._tasFocus = theme.getAttributeSet(this, 6);
      if (this._tasFocus != null) {
         this._fontFocus = this._tasFocus.getFont();
         if (this._fontFocus != null && !FontLogicHelper.fontLegible(this._fontFocus, locale)) {
            this._fontFocus = FontLogicHelper.getSuggestedFont(this._fontFocus, locale, true);
         }
      }

      if (this._fontFocus == null) {
         this._fontFocus = this._font;
      }

      this._fontHeight = this._font.getHeight();
      this._focusHeight = this._fontFocus.getHeight();
      if (this._fontHeight != this._focusHeight) {
         this._focusDrawWithInvalidate = true;
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this._focusOn = on && ThemeAttributeSet.getFocusStyle(this) == 3;
      super.drawFocus(graphics, on);
   }

   @Override
   public MenuItem getCurrentItem() {
      return this._items[this._selection];
   }

   private int getDisplayIndex(MenuItem item) {
      int length = this._items.length;

      for (int lv = 0; lv < length; lv++) {
         if (this._items[lv] == item) {
            return lv;
         }
      }

      return 0;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int y = this.getPosOfItem(this._selection);
      rect.set(0, y, this.getWidth(), this.getHeightOfItem(this._selection));
   }

   private int getHeightOfItem(int item) {
      return item == this._selection ? Math.max(this._focusHeight + this._heights[item] - this._fontHeight, this._heights[item]) : this._heights[item];
   }

   private int getItemForPosition(int y, int round) {
      y -= round;
      int yUsed = 0;
      int end = this._items.length;

      for (int lv = 0; lv < end; lv++) {
         yUsed += this.getHeightOfItem(lv);
         if (yUsed >= y) {
            return lv;
         }
      }

      return end;
   }

   private int getPosOfItem(int index) {
      int y = Arrays.sum(this._heights, 0, index, false);
      if (index > this._selection) {
         y = y + this.getHeightOfItem(this._selection) - this._heights[this._selection];
      }

      return y;
   }

   @Override
   public int getPreferredWidth() {
      int width = 0;
      int iconGapWidth = 0;
      int length = this._items.length;

      for (int lv = 0; lv < length; lv++) {
         MenuItem item = this._items[lv];
         Image icon = item.getIcon();
         if (icon != null) {
            iconGapWidth = Math.max(iconGapWidth, icon.getWidth(Display.getWidth(), Display.getHeight()));
         }
      }

      for (int lv = length - 1; lv >= 0; lv--) {
         MenuItem item = this._items[lv];
         if (!item.isSeparator()) {
            String string = item.toString();
            width = Math.max(width, this._font.getBounds(string) + iconGapWidth);
            if (this._fontFocus != this._font) {
               width = Math.max(width, this._fontFocus.getBounds(string) + iconGapWidth);
            }
         }
      }

      return width + this._contentPaddingLeft + this._contentPaddingRight;
   }

   private boolean containsChar(StringBuffer aBuffer, char aChar) {
      if (aBuffer != null) {
         for (int i = aBuffer.length() - 1; i >= 0; i--) {
            if (aBuffer.charAt(i) == aChar) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      int selectedFieldIndex = this._selection;
      int numFields = this._items.length;
      this.focusRemove();
      SLKeyLayout var10000 = Keypad.getLayout();
      Keypad.getLayout();
      StringBuffer keys = var10000.getComplementaryChars(key, SLKeyLayout.convertStatusToModifiers(status));
      if (keys != null) {
         keys = new StringBuffer(keys.toString());
      }

      for (int i = selectedFieldIndex + 1; i < numFields; i++) {
         String text = this._items[i].toString();
         char ch = text != null && text.length() > 0 ? Character.toLowerCase(CharacterUtilities.getOriginal(text.charAt(0))) : '\u0000';
         if (this.containsChar(keys, ch) || key == ch) {
            this._selection = i;
            this.focusAdd(true);
            this.invalidate(0, this.getPosOfItem(selectedFieldIndex), this.getWidth(), this.getPosOfItem(i));
            return true;
         }
      }

      for (int i = 0; i < selectedFieldIndex; i++) {
         String text = this._items[i].toString();
         char ch = text != null && text.length() > 0 ? Character.toLowerCase(CharacterUtilities.getOriginal(text.charAt(0))) : '\u0000';
         if (this.containsChar(keys, ch) || key == ch) {
            this._selection = i;
            this.focusAdd(true);
            this.invalidate(0, this.getPosOfItem(i + 1), this.getWidth(), this.getPosOfItem(selectedFieldIndex + 1));
            return true;
         }
      }

      this.focusAdd(true);
      return false;
   }

   @Override
   protected void layout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      int length = this._items.length;
      this._heights = new byte[length];
      this._offsets = new byte[length];
      TextMetrics metrics = new TextMetrics();

      for (int index = 0; index < length; index++) {
         MenuItem item = this._items[index];
         if (item.isSeparator()) {
            this._heights[index] = (byte)this._separatorHeight;
         } else {
            String text = this._items[index].toString();
            this._font.measureText(text, 0, text.length(), null, metrics);
            int above = Math.max(-metrics.iBoundsTlY, this._font.getBaseline());
            if (above <= this._font.getBaseline() && metrics.iBoundsBrY <= this._font.getDescent()) {
               this._heights[index] = (byte)this._fontHeight;
            } else {
               int below = Math.max(metrics.iBoundsBrY, this._font.getDescent());
               this._heights[index] = (byte)(above + below);
               if (metrics.iBoundsBrY > this._font.getDescent()) {
                  this._offsets[index] = (byte)(metrics.iBoundsBrY - this._font.getDescent());
               }
            }
         }
      }

      height = Arrays.sum(this._heights, 0, length, false);
      height += this._focusHeight - this._fontHeight;
      this.setExtent(width, height);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if ((status & 65536) != 0) {
         return amount;
      }

      int start = this._selection;
      int sign = MathUtilities.clamp(-1, amount, 1);
      int lengthMinusOne = this._items.length - 1;

      while (amount != 0) {
         this._selection = MathUtilities.clamp(0, this._selection + sign, lengthMinusOne);
         if (this._items[this._selection].isSeparator()) {
            this._selection = MathUtilities.clamp(0, this._selection + sign, lengthMinusOne);
         }

         amount -= sign;
      }

      if (this._focusDrawWithInvalidate) {
         int top = this.getPosOfItem(Math.min(start, this._selection));
         this.invalidate(0, top, this.getWidth(), this.getPosOfItem(Math.max(start, this._selection) + 1));
      }

      if (Ui.isTTSEnabled()) {
         this.accessibleEventOccurred(1, new Integer(1), new Integer(2), this.getCurrentItem());
      }

      return 0;
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      if (this.getExtent().contains(x, y)) {
         int newSelection = MathUtilities.clamp(0, this.getItemForPosition(y, -1), this._items.length - 1);
         if (!this._items[newSelection].isSeparator()) {
            this._selection = newSelection;
         }
      }
   }

   @Override
   public boolean stylusDrag(int x, int y, int status, int time) {
      this.focusRemove();
      this.moveFocus(x, y, status, time);
      this.focusAdd(true);
      return true;
   }

   @Override
   protected void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int width = this.getWidth();
      int x = this._contentPaddingLeft;
      graphics.setFont(this._font);
      int start = this.getItemForPosition(clip.y, -1);
      int end = this.getItemForPosition(clip.y + clip.height, 1) + 1;
      int y = this.getPosOfItem(start);
      Image icon = null;
      int iconGapWidth = 0;
      int len = this._items.length;

      for (int lv = 0; lv < len; lv++) {
         MenuItem item = this._items[lv];
         icon = item.getIcon();
         if (icon != null) {
            int itemHeight = this.getHeightOfItem(lv);
            iconGapWidth = Math.max(iconGapWidth, icon.getWidth(width, itemHeight));
         }
      }

      for (int lv = start; lv < end; lv++) {
         MenuItem item = this._items[lv];
         icon = item.getIcon();
         int itemHeight = this.getHeightOfItem(lv);
         if (item.isSeparator()) {
            graphics.drawLine(0, y + 1, width, y + 1);
         } else {
            String itemLabel = item.toString();
            Font currFont = this._font;
            if (!graphics.isDrawingStyleSet(8) || lv != this._selection || !this._focusOn && !this._focusDrawWithInvalidate) {
               graphics.setFont(currFont);
            } else {
               currFont = this._fontFocus;
               graphics.setFont(currFont);
               itemHeight = this.getHeightOfItem(lv);
            }

            graphics.drawText(itemLabel, 0, itemLabel.length(), x + iconGapWidth, y + itemHeight - this._offsets[lv], 104, width - iconGapWidth);
            graphics.setFont(this._font);
            if (icon != null) {
               icon.paint(graphics, x, y - this._offsets[lv], iconGapWidth, itemHeight);
            }

            if (item instanceof CascadingMenuItem) {
               int midY = y - this._offsets[lv] + (itemHeight >> 1);
               graphics.drawFilledPath(new int[]{x + width - 7, x + width - 7, x + width - 3}, new int[]{midY - 4, midY + 4, midY}, null, null);
            }
         }

         y += itemHeight;
      }
   }

   @Override
   public void setCurrentItem(MenuItem item) {
      this._selection = this.getDisplayIndex(item);
   }

   @Override
   public void setMenuItems(MenuItem[] items) {
      this._items = items;
   }
}
