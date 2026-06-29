package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.BorderRounded;
import net.rim.device.internal.ui.BorderTransparent;
import net.rim.device.internal.ui.Image;

public class ButtonField extends Field implements DrawStyle, FieldLabelProvider {
   private String _label;
   private Image _image;
   private int _imageWidth = -1;
   private int _imageHeight = -1;
   private boolean _active;
   private boolean _acceptsKeyUp;
   private boolean _acceptsKeyUpSet;
   private boolean _borderAllSet;
   private Border _borderAll;
   private boolean _borderFocusSet;
   private Border _borderFocus;
   private boolean _borderActiveSet;
   private Border _borderActive;
   private boolean _borderDisabledSet;
   private Border _borderDisabled;
   private boolean _borderDisabledFocusSet;
   private Border _borderDisabledFocus;
   private int _topExtra;
   private int _bottomExtra;
   private int _rightExtra;
   private int _leftExtra;
   private ThemeAttributeSet _attrNormal;
   private ThemeAttributeSet _attrFocus;
   private ThemeAttributeSet _attrActive;
   private ThemeAttributeSet _attrDisabled;
   private ThemeAttributeSet _attrDisabledFocus;
   private TextRect _text;
   private static Tag TAG = Tag.create("button");
   public static final int BARE = 1024;
   public static final long CONSUME_CLICK = 65536L;
   public static final long NEVER_DIRTY = 32768L;
   private static final int H_INSIDE_SPACE = 4;
   private static final int V_INSIDE_SPACE = 2;
   private static final int H_OUTSIDE_SPACE = 4;
   private static final int V_OUTSIDE_SPACE = 2;

   TextRect getTextRect() {
      return this._text;
   }

   public void setImage(Image image) {
      this._image = image;
   }

   public void setImageSize(int width, int height) {
      this._imageWidth = width;
      this._imageHeight = height;
   }

   @Override
   public void setLabel(String label) {
      this._label = label;
      this._text.setText(this._label);
      this.updateLayout();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   private void applyFocusDifference() {
      int marginActiveTop = this._topExtra;
      int marginActiveBottom = this._bottomExtra;
      int marginActiveLeft = this._leftExtra;
      int marginActiveRight = this._rightExtra;
      XYEdges edges = ThemeAttributeSet.getEdges(this, 2);
      if (edges != null) {
         marginActiveTop += edges.top;
         marginActiveRight += edges.right;
         marginActiveBottom += edges.bottom;
         marginActiveLeft += edges.left;
      }

      this.setMargin(marginActiveTop, marginActiveRight, marginActiveBottom, marginActiveLeft);
   }

   @Override
   protected void applyThemeOnStateChange() {
      boolean on = this.getState() == 6;
      ThemeAttributeSet attrNormal = null;
      ThemeAttributeSet attrFocus = null;
      Border border;
      if (this.isEditable()) {
         if (on && this._active) {
            if (this._borderActive != null) {
               border = this._borderActive;
            } else {
               border = on ? this._borderFocus : this._borderAll;
            }

            if (this._attrActive != null) {
               attrNormal = this._attrActive;
               attrFocus = this._attrActive;
            }
         } else {
            border = on ? this._borderFocus : this._borderAll;
         }
      } else {
         border = on ? this._borderDisabledFocus : this._borderDisabled;
         if (this._attrDisabled != null) {
            attrNormal = this._attrDisabled;
            attrFocus = this._attrDisabledFocus;
         }
      }

      if (attrNormal == null) {
         attrNormal = this._attrNormal;
         if (attrFocus == null) {
            attrFocus = this._attrFocus;
         }
      }

      this.setBorderWithoutLayout(border);
      this.calculateFocusDifference();
      this.applyFocusDifference();
      this.setThemeAttributesAll(attrNormal, attrFocus);
      super.applyThemeOnStateChange();
      int horizontalSpacing = this.getBorderLeft() + this.getBorderRight() + this.getPaddingLeft() + this.getPaddingRight();
      this._text.layout(Math.max(0, this.getExtent().width - horizontalSpacing), this.getExtent().height);
      this.invalidate();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      graphics.setDrawingStyle(8, false);
      if (on && this._active && this._attrActive != null) {
         this.setThemeAttributesSpecial(this._attrActive, graphics);
      } else {
         this.setThemeAttributesSpecial(on ? this._attrFocus : this._attrNormal, graphics);
      }

      Font font = this.getFontIfSet();
      if (font != null) {
         graphics.setFont(font);
      }

      this.paint(graphics);
      this.setThemeAttributesSpecial(null, null);
   }

   private void calculateFocusDifference() {
      if (this.isEditable() && this._borderFocus != null && this._borderAll != null) {
         if (this.getState() != 6) {
            this._topExtra = Math.max(0, this._borderFocus.getTop() - this._borderAll.getTop());
            this._rightExtra = Math.max(0, this._borderFocus.getRight() - this._borderAll.getRight());
            this._bottomExtra = Math.max(0, this._borderFocus.getBottom() - this._borderAll.getBottom());
            this._leftExtra = Math.max(0, this._borderFocus.getLeft() - this._borderAll.getLeft());
         } else {
            this._topExtra = Math.max(0, this._borderAll.getTop() - this._borderFocus.getTop());
            this._rightExtra = Math.max(0, this._borderAll.getRight() - this._borderFocus.getRight());
            this._bottomExtra = Math.max(0, this._borderAll.getBottom() - this._borderFocus.getBottom());
            this._leftExtra = Math.max(0, this._borderAll.getLeft() - this._borderFocus.getLeft());
         }
      } else {
         if (!this.isEditable() && this._borderDisabledFocus != null && this._borderDisabled != null) {
            this._topExtra = Math.abs(this._borderDisabledFocus.getTop() - this._borderDisabled.getTop());
            this._rightExtra = Math.abs(this._borderDisabledFocus.getRight() - this._borderDisabled.getRight());
            this._bottomExtra = Math.abs(this._borderDisabledFocus.getBottom() - this._borderDisabled.getBottom());
            this._leftExtra = Math.abs(this._borderDisabledFocus.getLeft() - this._borderDisabled.getLeft());
         }
      }
   }

   @Override
   public int getPreferredWidth() {
      Font font = this.getFontIfSet();
      return this.getPreferredWidth(font);
   }

   private int getPreferredWidth(Font font) {
      int width = 0;
      int fontHeight = 0;
      if (font != null) {
         width = font.getBounds(this._label);
         fontHeight = font.getHeight();
      } else if (this._attrNormal != null) {
         font = this._attrNormal.getFont();
         if (font != null) {
            int focusWidth = font.getBounds(this._label);
            if (focusWidth > width) {
               width = focusWidth;
            }

            int focusHeight = font.getHeight();
            if (focusHeight > fontHeight) {
               fontHeight = focusHeight;
            }
         }

         if (this._attrFocus != this._attrNormal && this._attrFocus != null) {
            font = this._attrFocus.getFont();
            if (font != null) {
               int focusWidth = font.getBounds(this._label);
               if (focusWidth > width) {
                  width = focusWidth;
               }

               int focusHeight = font.getHeight();
               if (focusHeight > fontHeight) {
                  fontHeight = focusHeight;
               }
            }
         }

         if (this._attrDisabled != this._attrNormal && this._attrDisabled != null) {
            font = this._attrDisabled.getFont();
            if (font != null) {
               int disabledWidth = font.getBounds(this._label);
               if (disabledWidth > width) {
                  width = disabledWidth;
               }

               int disabledHeight = font.getHeight();
               if (disabledHeight > fontHeight) {
                  fontHeight = disabledHeight;
               }
            }
         }

         if (this._attrDisabledFocus != this._attrNormal && this._attrDisabledFocus != null) {
            font = this._attrDisabledFocus.getFont();
            if (font != null) {
               int disabledFocusWidth = font.getBounds(this._label);
               if (disabledFocusWidth > width) {
                  width = disabledFocusWidth;
               }

               int disabledFocusHeight = font.getHeight();
               if (disabledFocusHeight > fontHeight) {
                  fontHeight = disabledFocusHeight;
               }
            }
         }

         if (this._attrActive != this._attrNormal && this._attrActive != null) {
            font = this._attrActive.getFont();
            if (font != null) {
               int activeWidth = font.getBounds(this._label);
               if (activeWidth > width) {
                  width = activeWidth;
               }

               int activeHeight = font.getHeight();
               if (activeHeight > fontHeight) {
                  fontHeight = activeHeight;
               }
            }
         }
      }

      if (width == 0 || fontHeight == 0) {
         font = this.getFont();
         width = font.getBounds(this._label);
         fontHeight = font.getHeight();
      }

      if (this._image != null) {
         width = width + this.getImageXYRect(fontHeight).width + 2;
      }

      return width + this._rightExtra + this._leftExtra;
   }

   private XYRect getImageXYRect(int fontHeight) {
      int imgWidth = this._imageWidth;
      if (imgWidth < 0) {
         imgWidth *= -fontHeight;
      }

      int imgHeight = this._imageHeight;
      if (imgHeight < 0) {
         imgHeight *= -fontHeight;
      }

      return new XYRect(0, 0, this._image.getWidth(imgWidth, imgHeight), this._image.getHeight(imgWidth, imgHeight));
   }

   @Override
   public int getPreferredHeight() {
      Font font = this.getFontIfSet();
      return this.getPreferredHeight(font);
   }

   private int getPreferredHeight(Font font) {
      int height = 0;
      if (font != null) {
         height = font.getHeight();
      } else if (this._attrNormal != null) {
         font = this._attrNormal.getFont();
         if (font != null) {
            int focusHeight = font.getHeight();
            if (focusHeight > height) {
               height = focusHeight;
            }
         }

         if (this._attrFocus != this._attrNormal && this._attrFocus != null) {
            font = this._attrFocus.getFont();
            if (font != null) {
               int focusHeight = font.getHeight();
               if (focusHeight > height) {
                  height = focusHeight;
               }
            }
         }

         if (this._attrDisabled != this._attrNormal && this._attrDisabled != null) {
            font = this._attrDisabled.getFont();
            if (font != null) {
               int disabledHeight = font.getHeight();
               if (disabledHeight > height) {
                  height = disabledHeight;
               }
            }
         }

         if (this._attrDisabledFocus != this._attrNormal && this._attrDisabledFocus != null) {
            font = this._attrDisabledFocus.getFont();
            if (font != null) {
               int disabledFocusHeight = font.getHeight();
               if (disabledFocusHeight > height) {
                  height = disabledFocusHeight;
               }
            }
         }

         if (this._attrActive != this._attrNormal && this._attrActive != null) {
            font = this._attrActive.getFont();
            if (font != null) {
               int activeHeight = font.getHeight();
               if (activeHeight > height) {
                  height = activeHeight;
               }
            }
         }
      }

      if (height == 0) {
         font = this.getFont();
         height = font.getHeight();
      }

      if (this._image != null) {
         int imgWidth = this._imageWidth;
         if (imgWidth < 0) {
            imgWidth *= -height;
         }

         int imgHeight = this._imageHeight;
         if (imgHeight < 0) {
            imgHeight *= -height;
         }

         height = Math.max(height, this._image.getHeight(imgWidth, imgHeight));
      }

      height = Math.max(height, this._text.getHeight());
      return height + this._topExtra + this._bottomExtra;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.isEditable()) {
               this._active = true;
               this.fieldChangeNotify(0);
               if (this._attrActive != null) {
                  this.applyThemeOnStateChange();
               }

               return this.isStyle(65536);
            }

            return false;
         default:
            return false;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (!this._acceptsKeyUpSet) {
         this._acceptsKeyUp = Application.getApplication().acceptsKeyUpEvents();
         this._acceptsKeyUpSet = true;
      }

      if (this._acceptsKeyUp && Keypad.map(Keypad.key(keycode), Keypad.status(keycode)) == '\n') {
         this._active = true;
         if (this._attrActive != null) {
            this.applyThemeOnStateChange();
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      this._active = false;
      if (this._attrActive != null) {
         this.applyThemeOnStateChange();
      }

      return false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == '\n') {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void layout(int width, int height) {
      ThemeAttributeSet tas = ThemeManager.getActiveTheme().getAttributeSet(this, 6);
      Font focusFont = null;
      if (tas != null) {
         focusFont = tas.getFont();
      }

      int maxWidth = width;
      width = Math.min(width, this.getPreferredWidth());
      width = focusFont != null ? Math.max(width, this.getPreferredWidth(focusFont)) : width;
      width = Math.min(maxWidth, width);
      int imageWidth = 0;
      if (this._image != null) {
         imageWidth = this.getImageXYRect(this.getFont().getHeight()).width + 2;
         this._text.setPosition(imageWidth, 0);
      }

      this._text.layout(width - imageWidth, height);
      height = Math.min(height, this.getPreferredHeight());
      height = focusFont != null ? Math.max(height, this.getPreferredHeight(focusFont)) : height;
      height = Math.max(this._text.getLineHeight(0), height);
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      int foregroundColor = graphics.getColor();
      boolean on = this.getState() == 6;
      if (on && !this.isStyle(1024)) {
         if (this._attrFocus != null) {
            graphics.setColor(this._attrFocus.getColor(1));
         } else {
            graphics.setColor(graphics.getBackgroundColor());
         }
      }

      if (!this.isEditable()) {
         if (on && this._attrDisabledFocus != null) {
            graphics.setColor(this._attrDisabledFocus.getColor(1));
         } else if (!on && this._attrDisabled != null) {
            graphics.setColor(this._attrDisabled.getColor(1));
         }
      }

      Font font = graphics.getFont();
      int fontHeight = font.getHeight();
      int contentHeight = this.getContentHeight();
      if (this._image != null) {
         XYRect dimensions = this.getImageXYRect(fontHeight);
         int imgWidth = dimensions.width;
         int imgHeight = dimensions.height;
         this._image.paint(graphics, 0, contentHeight - imgHeight >>> 1, imgWidth, imgHeight);
      }

      this._text.paintSelf(graphics);
      graphics.setColor(foregroundColor);
   }

   @Override
   protected void onUnfocus() {
      this._active = false;
      super.onUnfocus();
   }

   @Override
   public void setBorder(int state, Border border) {
      super.setBorder(state, null);
      switch (state) {
         case 0:
            this._borderAll = border;
            this._borderAllSet = border != null;
         default:
            return;
         case 4:
            this._borderActive = border;
            this._borderActiveSet = border != null;
            return;
         case 6:
            this._borderFocus = border;
            this._borderFocusSet = border != null;
            return;
         case 7:
            this._borderDisabled = border;
            this._borderDisabledSet = border != null;
            return;
         case 8:
            this._borderDisabledFocus = border;
            this._borderDisabledFocusSet = border != null;
      }
   }

   @Override
   public void setDirty(boolean dirty) {
      if (!this.isStyle(32768)) {
         super.setDirty(dirty);
      }
   }

   public ButtonField(long style) {
      this(null, style);
   }

   public ButtonField(String label) {
      this(label, 0);
   }

   public ButtonField(String label, long style) {
      super(verifyStyle(style));
      this.setTag(TAG);
      this._label = label;
      this._text = new TextRect(this);
      this._text.setText(this._label);
      this._text.setStyle(64);
   }

   @Override
   protected void applyTheme() {
      Theme theme = ThemeManager.getActiveTheme();
      this._attrNormal = theme.getAttributeSet(this.getTag(), this.getId(), 0);
      this._attrFocus = theme.getAttributeSet(this.getTag(), this.getId(), 6);
      this._attrActive = theme.getAttributeSet(this.getTag(), this.getId(), 4, true);
      this._attrDisabled = theme.getAttributeSet(this.getTag(), this.getId(), 7);
      this._attrDisabledFocus = theme.getAttributeSet(this.getTag(), this.getId(), 8);
      if (this._attrFocus == null) {
         this._attrFocus = this._attrNormal;
      }

      if (this._attrDisabled == null) {
         this._attrDisabled = this._attrNormal;
      }

      if (this._attrDisabledFocus == null) {
         this._attrDisabledFocus = this._attrFocus;
      }

      if (!this._borderAllSet) {
         this.setThemeAttributeSet(this._attrNormal);
         this._borderAll = ThemeAttributeSet.getBorder(this);
      }

      if (!this._borderFocusSet) {
         this.setThemeAttributeSet(this._attrFocus);
         this._borderFocus = ThemeAttributeSet.getBorder(this);
      }

      if (!this._borderDisabledSet) {
         this.setThemeAttributeSet(this._attrDisabled);
         this._borderDisabled = ThemeAttributeSet.getBorder(this);
      }

      if (!this._borderDisabledFocusSet) {
         this.setThemeAttributeSet(this._attrDisabledFocus);
         this._borderDisabledFocus = ThemeAttributeSet.getBorder(this);
      }

      if (this._attrActive != null && !this._borderActiveSet) {
         this.setThemeAttributeSet(this._attrActive);
         this._borderActive = ThemeAttributeSet.getBorder(this);
      }

      if (this._borderAll == null) {
         boolean bare = this.isStyle(1024);
         if (bare) {
            this._borderAll = new BorderTransparent(2, 4, 2, 4);
            this._borderDisabled = this._borderAll;
         } else {
            this._borderAll = new BorderRounded(2, 4, 2, 4, 4);
            this._borderDisabled = new BorderRounded(2, 4, 2, 4, 6);
         }

         int style = 4 | (bare ? 0 : 1);
         this._borderFocus = new BorderRounded(2, 4, 2, 4, style);
         this._borderDisabledFocus = new BorderRounded(2, 4, 2, 4, style | 1);
      }

      this.calculateFocusDifference();
      this.applyFocusDifference();
      this.setThemeAttributeSet(null);
      this._text.applyTheme();
      super.applyTheme();
   }

   @Override
   public void setMuddy(boolean muddy) {
      if (!this.isStyle(32768)) {
         super.setMuddy(muddy);
      }
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      this._active = true;
      if (this.isStyle(65536) || (status & 1073741824) != 0) {
         this.fieldChangeNotify(0);
      }

      if (this._attrActive != null) {
         this.applyThemeOnStateChange();
      }

      return this.isStyle(65536);
   }

   @Override
   protected boolean trackwheelUnclick(int status, int time) {
      this._active = false;
      if (this._attrActive != null) {
         this.applyThemeOnStateChange();
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

   @Override
   public int getAccessibleRole() {
      return 6;
   }

   @Override
   public String getAccessibleName() {
      return StringUtilities.removeChars(this.getLabel(), "̲");
   }

   public ButtonField() {
      this(0);
   }
}
