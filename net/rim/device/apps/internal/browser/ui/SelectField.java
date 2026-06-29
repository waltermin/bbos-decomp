package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.vm.Array;

public class SelectField extends Field {
   private int _size;
   private int _index;
   protected boolean _multiple;
   private boolean _numeric = true;
   private boolean _oneOptionSet;
   private int _widestWidth;
   private int _widestWidthWithIndent;
   private String[] _options;
   private byte[] _flags;
   private int _iconWidth;
   private int _iconHeight;
   private int _bitmapWidth;
   private Bitmap _bitmapUp;
   private Bitmap _bitmapDown;
   private Font _optionFont;
   private Font _optgroupFont;
   private int _lineHeight;
   private static final int BORDER_WIDTH = 3;
   public static final int CONTEXT_CHANGE_OPTION = 2;
   public static final int CHANGE_MENU_ITEM_ORDERING = 30270;
   public static final int FLAG_SELECTED_BY_USER = 0;
   public static final int FLAG_SELECTED_BY_DEFAULT = 1;
   public static final int FLAG_OPTGROUP = 2;
   public static final int FLAG_WITHIN_OPTGROUP = 3;
   private static final int INDENT_FOR_GROUPED_OPTION = 10;
   private static MenuItem _changeMenuItem = new SelectField$1(CommonResource.getBundle(), 1, 30270, 10);

   public SelectField(int size, boolean multiple, long style) {
      super(verifyStyle(style));
      this._size = size;
      this._multiple = multiple;
      this._optionFont = this.getFont();
      this._lineHeight = this._optionFont.getHeight();
      this._options = new Object[0];
      this._flags = new byte[0];
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
      this._optionFont = font;
      this._lineHeight = this._optionFont.getHeight();
      if (this._optgroupFont != null) {
         this._optgroupFont = this._optionFont.derive(3);
         int optgroupFontHeight = this._optgroupFont.getHeight();
         if (optgroupFontHeight > this._lineHeight) {
            this._lineHeight = optgroupFontHeight;
         }
      }

      this._widestWidth = -1;
      this._widestWidthWithIndent = -1;
   }

   protected void updateIndex() {
      int flags = 0;
      this._index = -1;

      for (int index = 0; index < this._flags.length; index++) {
         int var3 = this._flags[index];
         if ((var3 & 1) != 0) {
            this._index = index;
            break;
         }
      }

      if (this._index == -1) {
         this._index = 0;
      }
   }

   @Override
   public int getPreferredWidth() {
      int width = 6;
      width += (this._bitmapDown != null ? this._bitmapDown.getWidth() : 0) + 2;
      width += this.getWidthOfWidestOption(this._size > 1);
      if (this._multiple) {
         width += SystemIcon.COLLECTION.getWidth(this._lineHeight, this._lineHeight);
      }

      return width;
   }

   @Override
   public int getPreferredHeight() {
      return this._lineHeight * this._size + 6;
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._bitmapUp = Theme.getThemeBitmap(0);
      this._bitmapDown = Theme.getThemeBitmap(1);
      this._bitmapWidth = this._bitmapUp != null ? this._bitmapUp.getWidth() : 0;
   }

   @Override
   protected void layout(int width, int height) {
      int fontHeight = this._lineHeight;
      this._iconWidth = this._multiple ? SystemIcon.COLLECTION.getWidth(fontHeight, fontHeight) : 0;
      this._iconHeight = SystemIcon.COLLECTION.getHeight(fontHeight, fontHeight);
      width = Math.min(width, this.getPreferredWidth());
      height = Math.min(height, this.getPreferredHeight());
      this.setExtent(width, height);
   }

   @Override
   public void paint(Graphics graphics) {
      if ((this.getStyle() & 137438953472L) != 0) {
         XYRect clip = graphics.getClippingRect();
         int oldColor = graphics.getColor();
         if (this.isEditable()) {
            graphics.setColor(ThemeAttributeSet.getColor(this, 0));
         } else {
            graphics.setColor(13882323);
         }

         graphics.fillRect(clip.x, clip.y, clip.width, clip.height);
         graphics.setColor(oldColor);
      }

      int paragraphOrdering = RichText.getParagraphOrdering(this.getStyle());
      boolean rtl = (this.getStyle() & 274877906944L) != 0;
      int width = this.getContentWidth();
      int height = this.getContentHeight();
      graphics.setColor(ThemeAttributeSet.getColor(this, 1));
      graphics.drawRect(1, 1, width - 2, height - 2);
      int verticalLine = rtl ? this._bitmapWidth + 3 : width - 3 - this._bitmapWidth - 1;
      if (this._size == 1) {
         graphics.drawLine(verticalLine, 1, verticalLine, height - 2);
      }

      int bitmapPos = rtl ? 3 : width - this._bitmapWidth - 3;
      int start = Math.max(0, Math.min(this._index, this._options.length - this._size));
      int end = Math.min(this._options.length, start + this._size);
      if (this._size == 1) {
         if (this._bitmapDown != null) {
            int color = graphics.getColor();
            graphics.setColor(14079694);
            graphics.fillRect(bitmapPos, 3, this._bitmapDown.getWidth(), height - 6);
            int vertAlignDiff = height - 6 - this._bitmapDown.getHeight();
            if (vertAlignDiff < 0) {
               vertAlignDiff = 0;
            }

            graphics.drawBitmap(bitmapPos, 3 + (vertAlignDiff >> 1), this._bitmapDown.getWidth(), this._bitmapDown.getHeight(), this._bitmapDown, 0, 0);
            graphics.setColor(color);
         }
      } else {
         if (this._bitmapUp != null) {
            graphics.drawBitmap(bitmapPos, 3, this._bitmapUp.getWidth(), this._bitmapUp.getHeight(), this._bitmapUp, 0, 0);
         }

         if (this._bitmapDown != null) {
            graphics.drawBitmap(
               bitmapPos, height - 3 - this._bitmapDown.getHeight(), this._bitmapDown.getWidth(), this._bitmapDown.getHeight(), this._bitmapDown, 0, 0
            );
         }
      }

      int iconX0 = rtl ? bitmapPos + this._bitmapWidth : 3;
      int textX0 = iconX0 + this._iconWidth + 1;
      int textX1 = rtl ? width - 3 : verticalLine - 1;
      int y = 3;

      for (int index = start; index < end; index++) {
         byte flags = this._flags[index];
         boolean optgroup = (flags & 4) != 0;
         graphics.setFont(optgroup ? this._optgroupFont : this._optionFont);
         int iconX = iconX0;
         int textX = optgroup ? iconX : textX0;
         if (this._size > 1 && (flags & 8) != 0) {
            iconX += 10;
            textX += 10;
         }

         if (this._options[index] != null) {
            String value = this._options[index];
            if (this._multiple) {
               if (!optgroup) {
                  SystemIcon.COLLECTION.paint(graphics, iconX, y, this._iconWidth, this._iconHeight, this.isOptionSetInternal(index) ? 1 : 0);
               }

               RichText.drawTextWithEllipses(graphics, value, textX, y, textX1 - textX, paragraphOrdering, 0);
            } else {
               boolean isSet = this.isOptionSetInternal(index) && this._size > 1;
               if (isSet) {
                  graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 2));
                  graphics.setColor(ThemeAttributeSet.getColor(this, 3));
                  graphics.clear(iconX0, y, textX1 - iconX0, this._lineHeight);
               }

               RichText.drawTextWithEllipses(graphics, value, textX, y, textX1 - textX, paragraphOrdering, 0);
               if (isSet) {
                  graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 0));
                  graphics.setColor(ThemeAttributeSet.getColor(this, 1));
               }
            }
         }

         y += this._lineHeight;
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      int width = this.getContentWidth();
      int height = this.getContentHeight();
      graphics.setColor(on ? graphics.getColor() : graphics.getBackgroundColor());
      graphics.drawRect(0, 0, width, height);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (Ui.getMode() < 2 && this.isEditable() && !this.isEmpty()) {
         contextMenu.addItem(_changeMenuItem);
         contextMenu.setDefaultItem(_changeMenuItem);
      }
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0) {
         this.changeDialog(true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if ((status & 1073741824) != 0) {
         return false;
      }

      this.changeDialog(false);
      return true;
   }

   protected void changeDialog(boolean alted) {
      if (this.isEditable() && !this.isEmpty()) {
         EditScreen dialog = new EditScreen(this, new SelectField$SelectInPlaceField(this, this._index, alted));
         dialog.doModal();
         this.updateIndex();
         this.invalidate();
      }
   }

   public boolean isEmpty() {
      return this._options.length == 0;
   }

   private boolean hasOptgroups() {
      return this._optgroupFont != null;
   }

   public int getLength() {
      if (this.hasOptgroups()) {
         int numOptions = 0;

         for (int i = this._options.length - 1; i >= 0; i--) {
            if ((this._flags[i] & 4) == 0) {
               numOptions++;
            }
         }

         return numOptions;
      } else {
         return this._options.length;
      }
   }

   public void setLength(int newSize) {
      if (newSize < 0) {
         newSize = 0;
      }

      Array.resize(this._options, newSize);
      Array.resize(this._flags, newSize);
      boolean hasOptGroup = this.resetOptions();
      if (!hasOptGroup && this._optgroupFont != null) {
         this._optgroupFont = null;
         this._lineHeight = this._optionFont.getHeight();
      }

      this.invalidate();
   }

   public void setSize(int size) {
      if (size < 0) {
         size = 1;
      }

      this._size = size;
   }

   public int getSize() {
      return this._size;
   }

   public int getSelectedIndex() {
      int length = this._flags.length;
      int numOptions = 0;

      for (int i = 0; i < length; i++) {
         if (this.isOptionSetInternal(i)) {
            return numOptions;
         }

         if ((this._flags[i] & 4) == 0) {
            numOptions++;
         }
      }

      return -1;
   }

   private int getCombinedIndex(int index) {
      if (this.hasOptgroups()) {
         int length = this._options.length;
         int numOptions = 0;

         for (int i = 0; i < length; i++) {
            if ((this._flags[i] & 4) == 0 && numOptions++ == index) {
               return i;
            }
         }

         if (index > 0) {
            return length;
         }
      }

      return index;
   }

   public String getOption(int index) {
      return this._options[this.getCombinedIndex(index)];
   }

   public boolean getDefaultSelected(int index) {
      return (this._flags[this.getCombinedIndex(index)] & 2) != 0;
   }

   public void setDefaultSelected(int index, boolean value) {
      index = this.getCombinedIndex(index);
      if ((this._flags[index] & 2) != 0 != value) {
         if (!this._multiple) {
            int flags = 0;
            int size = this._options.length;

            for (int i = 0; i < size; i++) {
               int var7 = this._flags[i];
               if ((var7 & 2) != 0) {
                  var7 ^= 2;
               }

               this._flags[i] = (byte)var7;
            }
         }

         this._flags[index] = (byte)(this._flags[index] | 2);
      }
   }

   public boolean resetOptions() {
      int size = this._options.length;
      int flags = 0;
      this._oneOptionSet = false;
      this._widestWidth = -1;
      this._widestWidthWithIndent = -1;
      boolean hasOptgroup = false;

      for (int index = 0; index < size; index++) {
         int var5 = this._flags[index];
         if ((var5 & 2) == 0 || !this._multiple && this._oneOptionSet) {
            var5 &= -2;
         } else {
            var5 |= 1;
            this._oneOptionSet = true;
         }

         if ((var5 & 4) != 0) {
            hasOptgroup = true;
         }

         this._flags[index] = (byte)var5;
      }

      if (!this._oneOptionSet && !this._multiple) {
         for (int index = 0; index < size; index++) {
            if ((this._flags[index] & 4) == 0) {
               this._flags[index] = (byte)(this._flags[index] | 1);
               this._oneOptionSet = true;
               break;
            }
         }
      }

      this.updateIndex();
      this.setDirty(false);
      return hasOptgroup;
   }

   public void toggleOption(int optionIndex, boolean programmatic) {
      this.toggleOptionInternal(this.getCombinedIndex(optionIndex), programmatic);
   }

   private void toggleOptionInternal(int optionIndex, boolean programmatic) {
      if (optionIndex < this._flags.length) {
         int flags = this._flags[optionIndex];
         if (this._multiple) {
            if ((flags & 1) != 0) {
               flags &= -2;
            } else {
               flags |= 1;
            }

            this._flags[optionIndex] = (byte)flags;
         } else if ((flags & 1) == 0) {
            int size = this._options.length;

            for (int index = 0; index < size; index++) {
               this._flags[index] = (byte)(this._flags[index] & -2);
            }

            this._flags[optionIndex] = (byte)(this._flags[optionIndex] | 1);
         }

         this.fieldChangeNotify(programmatic ? Integer.MIN_VALUE : 0);
         this.setDirty(true);
         this.updateIndex();
         this.invalidate();
         if (!programmatic) {
            this.onChange();
         }
      }
   }

   protected void onChange() {
   }

   public boolean isOptionSet(int index) {
      return this.isOptionSetInternal(this.getCombinedIndex(index));
   }

   private boolean isOptionSetInternal(int index) {
      return (this._flags[index] & 1) != 0;
   }

   public int getWidthOfWidestOption(boolean addIndentForGroupedOption) {
      synchronized (this) {
         if (this._widestWidth != -1) {
            return addIndentForGroupedOption ? this._widestWidthWithIndent : this._widestWidth;
         }

         int width = 0;
         int widthWithIndent = 0;
         this._widestWidth = 0;
         this._widestWidthWithIndent = 0;
         int size = this._options.length;

         for (int index = 0; index < size; index++) {
            if (this._options[index] != null) {
               String label = this._options[index].toString();
               byte flags = this._flags[index];
               if ((flags & 4) != 0) {
                  width = this._optgroupFont.getBounds(label) + 1;
               } else {
                  width = this._optionFont.getBounds(label) + this._iconWidth + 1;
               }

               widthWithIndent = width;
               if ((flags & 8) != 0) {
                  widthWithIndent += 10;
               }

               if (width > this._widestWidth) {
                  this._widestWidth = width;
               }

               if (widthWithIndent > this._widestWidthWithIndent) {
                  this._widestWidthWithIndent = widthWithIndent;
               }
            }
         }
      }

      return addIndentForGroupedOption ? this._widestWidthWithIndent : this._widestWidth;
   }

   private void updateWidthOfWidestOption(int index) {
      synchronized (this) {
         if (this._widestWidth != -1) {
            if (this._options[index] != null) {
               String label = this._options[index].toString();
               int width = 0;
               int widthWithIndent = 0;
               byte flags = this._flags[index];
               if ((flags & 4) != 0) {
                  width = this._optgroupFont.getBounds(label) + 1;
               } else {
                  width = this._optionFont.getBounds(label) + this._iconWidth + 1;
               }

               widthWithIndent = width;
               if ((flags & 8) != 0) {
                  widthWithIndent += 10;
               }

               if (width > this._widestWidth) {
                  this._widestWidth = width;
               }

               if (widthWithIndent > this._widestWidthWithIndent) {
                  this._widestWidthWithIndent = widthWithIndent;
               }
            }
         }
      }
   }

   public int getHeightOfOption(Font font) {
      return this._iconHeight;
   }

   private boolean containsKey(char key, char[] keys) {
      int len = keys.length;

      for (int i = 0; i < len; i++) {
         if (Character.toLowerCase(keys[i]) == Character.toLowerCase(key)) {
            return true;
         }
      }

      return false;
   }

   private static long verifyStyle(long style) {
      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      return style;
   }

   public int addOption(String option, byte flag) {
      return this.addOption(this._options.length, option, flag);
   }

   public int addOption(int index, String option, byte flag) {
      if (index >= this._options.length) {
         Array.resize(this._options, index + 1);
         Array.resize(this._flags, index + 1);
      }

      this._options[index] = option;
      this._flags[index] = flag;
      if ((flag & 4) != 0 && this._optgroupFont == null) {
         this._optgroupFont = this._optionFont.derive(3);
         int optgroupFontHeight = this._optgroupFont.getHeight();
         if (optgroupFontHeight > this._lineHeight) {
            this._lineHeight = optgroupFontHeight;
         }
      }

      if (this.isDirty()) {
         this.resetOptions();
      } else {
         if ((flag & 2) == 0 || !this._multiple && this._oneOptionSet) {
            flag = (byte)(flag & -2);
         } else {
            flag = (byte)(flag | 1);
            this._oneOptionSet = true;
         }

         this._flags[index] = flag;
      }

      this.updateWidthOfWidestOption(index);
      this.updateLayout();
      return index;
   }

   public void setOptionText(int optionIndex, String option, boolean updateLayout) {
      this.setOptionTextInternal(this.getCombinedIndex(optionIndex), option, updateLayout);
   }

   private void setOptionTextInternal(int optionIndex, String option, boolean updateLayout) {
      this._options[optionIndex] = option;
      if (this._numeric && option != null && option.length() > 0 && !Character.isDigit(option.charAt(0))) {
         this._numeric = false;
      }

      this.updateWidthOfWidestOption(optionIndex);
      if (updateLayout) {
         this.updateLayout();
      } else {
         this.invalidate();
      }
   }
}
