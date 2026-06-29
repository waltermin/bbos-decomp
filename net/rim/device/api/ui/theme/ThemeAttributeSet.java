package net.rim.device.api.ui.theme;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontLogicHelper;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.Background;
import net.rim.device.internal.ui.Border;

public final class ThemeAttributeSet {
   private String _scrollbarName;
   private Bitmap[] _scrollbarImages;
   String _element;
   int _set;
   String[] _palettedColors = new String[8];
   int[] _colors = new int[8];
   int _focusStyle;
   String _fontFamily;
   String _altFontFamily;
   int _fontStyle;
   int _fontSize;
   int _fontSizeUnits;
   int _fontAntialiasMode;
   int _fontStrokeOpacity = 255;
   int _fontRelativeChange = 0;
   float _fontRelativePercent = (float)false;
   Font _font;
   int _textAlign;
   int _maxLineWrap;
   Background _background;
   ResourceFetcher _backgroundLocation;
   String _backgroundName;
   int _opacity = 255;
   String[] _scrollArrowName;
   Bitmap[] _scrollArrow;
   String _borderName;
   Border _border;
   XYEdges _padding;
   XYEdges _margin;
   String _layout;
   String[] _layoutParams;
   XYRect _position;
   public static final int EDGES_PADDING;
   public static final int EDGES_BORDER;
   public static final int EDGES_MARGIN;
   public static final int COLOR_BACKGROUND;
   public static final int COLOR_FOREGROUND;
   public static final int COLOR_CARET_BACKGROUND;
   public static final int COLOR_CARET_FOREGROUND;
   public static final int COLOR_SELECTION_BACKGROUND;
   public static final int COLOR_SELECTION_FOREGROUND;
   public static final int COLOR_FONT_STROKE;
   public static final int COLOR_FONT_FILL;
   public static final int COLOR_COUNT;
   public static final int SCROLL_ARROW_UP;
   public static final int SCROLL_ARROW_DOWN;
   public static final int SCROLL_ARROW_LEFT;
   public static final int SCROLL_ARROW_RIGHT;
   public static final int FONT_SIZE_XX_SMALL;
   public static final int FONT_SIZE_X_SMALL;
   public static final int FONT_SIZE_SMALL;
   public static final int FONT_SIZE_MEDIUM;
   public static final int FONT_SIZE_LARGE;
   public static final int FONT_SIZE_X_LARGE;
   public static final int FONT_SIZE_XX_LARGE;
   public static final int FONT_SIZE_LARGER;
   public static final int FONT_SIZE_SMALLER;
   static final int COLOR_SET_SHIFT;
   static final int FOCUS_STYLE_SET;
   static final int FONT_SET;
   static final int BACKGROUND_IMAGE_SET;
   static final int BORDER_SET;
   static final int EDGES_SET_SHIFT;
   static final int SCROLL_ARROW_SET_SHIFT;
   static final int BACKGROUND_SET;
   static final int BACKGROUND_OPACITY_SET;
   static final int TEXT_ALIGN_SET;
   static final int FONT_STYLE_SET;

   public ThemeAttributeSet() {
      this._border = this._border;
   }

   public ThemeAttributeSet(String element) {
      this._element = element;
   }

   public ThemeAttributeSet(ThemeAttributeSet themeAttributes) {
      if (themeAttributes == null) {
         throw new IllegalArgumentException();
      }

      this.clone(themeAttributes);
   }

   private final void clone(ThemeAttributeSet ta) {
      this._element = ta._element;
      this._set = ta._set;
      System.arraycopy(ta._colors, 0, this._colors, 0, this._colors.length);
      System.arraycopy(ta._palettedColors, 0, this._palettedColors, 0, this._palettedColors.length);
      this._focusStyle = ta._focusStyle;
      this._fontFamily = ta._fontFamily;
      this._altFontFamily = ta._altFontFamily;
      this._fontStyle = ta._fontStyle;
      this._fontSize = ta._fontSize;
      this._fontSizeUnits = ta._fontSizeUnits;
      this._fontStrokeOpacity = ta._fontStrokeOpacity;
      this._fontAntialiasMode = ta._fontAntialiasMode;
      this._font = ta._font;
      this._textAlign = ta._textAlign;
      if (ta._scrollArrowName != null) {
         this._scrollArrowName = new String[ta._scrollArrowName.length];
         System.arraycopy(ta._scrollArrowName, 0, this._scrollArrowName, 0, this._scrollArrowName.length);
      }

      if (ta._scrollArrow != null) {
         this._scrollArrow = new Bitmap[ta._scrollArrow.length];
         System.arraycopy(ta._scrollArrow, 0, this._scrollArrow, 0, this._scrollArrow.length);
      }

      this._borderName = ta._borderName;
      this._padding = ta._padding;
      this._border = ta._border;
      this._margin = ta._margin;
      this._scrollbarName = ta._scrollbarName;
      this._scrollbarImages = ta._scrollbarImages;
      this._position = ta._position;
      this._layout = ta._layout;
      this._layoutParams = ta._layoutParams;
   }

   public final void apply() {
      Theme theme = this.getTheme();
      this.calculateRelativeFontSize();
      if (this._fontFamily != null || (this._fontSize | this._fontStyle | this._fontAntialiasMode) != 0) {
         Font defaultFont = Font.getDefault();
         FontFamily family = null;
         int localeCode = Locale.getDefaultForSystem().getCode();
         if (FontLogicHelper.useAltFont(localeCode)) {
            family = FontLogicHelper.getAltFontFamily(localeCode);
         } else if (this._fontFamily != null) {
            try {
               family = FontFamily.forName(theme.getRegisteredFontNameHack(this._fontFamily));
            } catch (ClassNotFoundException var13) {
            }
         } else {
            family = defaultFont.getFontFamily();
         }

         int fontStyle = this._fontStyle != 0 ? this._fontStyle & -1073741825 : defaultFont.getStyle();
         int fontSize = this._fontSize != 0 ? this._fontSize : defaultFont.getHeight();
         int fontSizeUnits = this._fontSizeUnits != 0 ? this._fontSizeUnits : 0;
         int fontSizePx = Ui.convertSize(fontSize, this._fontSizeUnits, 0);
         int fontAntialiasMode = this._fontAntialiasMode != 0 ? this._fontAntialiasMode : defaultFont.getAntialiasMode();
         if (!family.isHeightSupported(fontSizePx)) {
            int[] heights = family.getHeights();
            int heightIndex = MathUtilities.clamp(0, -Arrays.binarySearch(heights, fontSizePx) - 2, heights.length - 1);
            fontSize = heights[heightIndex];
            fontSizeUnits = 0;
         }

         int effects = 0;
         int strokeColor = this.getColor(6);
         int fillColor = this.getColor(7);
         if ((this._set & 64) != 0) {
            effects = 768;
            strokeColor |= ~this._fontStrokeOpacity << 24;
         }

         this._font = family.getFont(fontStyle, fontSize, fontSizeUnits, fontAntialiasMode, effects, 65536, 0, 0, 65536, 0, 0, strokeColor, fillColor);
         this._set |= 512;
      }

      if (this._borderName != null) {
         this._border = theme.getBorder(this._borderName);
         if (this._background == null && this._border != null) {
            this._background = this._border.getBackground();
            this._set |= 524288;
         }
      }

      for (int lv = this._palettedColors.length - 1; lv >= 0; lv--) {
         if (this._palettedColors[lv] != null) {
            this._colors[lv] = theme.getColor(this._palettedColors[lv]);
         }
      }
   }

   private final void calculateRelativeFontSize() {
      Font defaultFont = Font.getDefault();
      if (this._fontRelativeChange != 0) {
         this._fontSize = defaultFont.getHeight(this._fontSizeUnits) + this._fontRelativeChange;
      } else {
         if (this._fontRelativePercent > 0) {
            this._fontSize = (int)(defaultFont.getHeight() * this._fontRelativePercent);
            this._fontSizeUnits = 0;
         }
      }
   }

   public final void applyToGraphics(Graphics graphics) {
      if ((this._set & 2) != 0) {
         graphics.setColor(this._colors[1]);
      }

      if ((this._set & 512) != 0) {
         if (this._font == null) {
            graphics.setFont(Font.getDefault());
         } else {
            graphics.setFont(this._font);
         }
      }

      if ((this._set & 1) != 0) {
         graphics.setBackgroundImage(null, 0, 0);
         graphics.setBackgroundColor(this._colors[0]);
      }
   }

   final boolean freeStaleObject(int priority) {
      return false;
   }

   public final Background getBackground() {
      return this._background;
   }

   public static final Background getBackground(Field field) {
      ThemeAttributeSet ta = getInheritedAttribute(field, 524288);
      return ta == null ? null : ta._background;
   }

   public final int getBackgroundOpacity() {
      return this._opacity;
   }

   public final int getFontStrokeOpacity() {
      return this._fontStrokeOpacity;
   }

   public final String getAltFontFamily() {
      return this._altFontFamily;
   }

   public final int getColor(int colorType) {
      return this._colors[colorType];
   }

   public final String getElement() {
      return this._element;
   }

   public final int getMaximumLineWrapping() {
      return this._maxLineWrap;
   }

   private static final ThemeAttributeSet getInheritedAttribute(Field field, int flags) {
      if (field != null) {
         ThemeAttributeSet ta = field.getThemeAttributeSetSpecial();
         if (ta != null && (ta._set & flags) != 0) {
            return ta;
         }
      }

      while (field != null) {
         ThemeAttributeSet ta = field.getThemeAttributeSet();
         if (ta != null && (ta._set & flags) != 0) {
            return ta;
         }

         field = field.getManager();
      }

      return null;
   }

   private static final ThemeAttributeSet getNonInheritedAttribute(Field field, int flags) {
      if (field != null) {
         ThemeAttributeSet ta = field.getThemeAttributeSetSpecial();
         if (ta != null && (ta._set & flags) != 0) {
            return ta;
         }

         ta = field.getThemeAttributeSet();
         if (ta != null && (ta._set & flags) != 0) {
            return ta;
         }
      }

      return null;
   }

   public static final Border getBorder(Field field) {
      ThemeAttributeSet ta = getNonInheritedAttribute(field, 2048);
      return ta == null ? null : ta._border;
   }

   public static final int getColor(Field field, int colorType) {
      ThemeAttributeSet ta = getInheritedAttribute(field, 1 << 0 + colorType);
      if (ta == null) {
         switch (colorType) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            default:
               return 16777215;
            case 1:
               return 0;
            case 2:
               return 0;
            case 3:
               return 16777215;
            case 4:
               return 0;
            case 5:
               return 16777215;
            case 6:
               return 0;
            case 7:
               return 0;
         }
      } else {
         return ta._colors[colorType];
      }
   }

   public static final XYEdges getEdges(Field field, int edgeType) {
      ThemeAttributeSet ta = getNonInheritedAttribute(field, 1 << 12 + edgeType);
      if (ta != null) {
         switch (edgeType) {
            case -1:
               break;
            case 0:
            default:
               return ta._padding;
            case 1:
               if (ta._border != null) {
                  return ta._border.getEdges();
               }

               return null;
            case 2:
               return ta._margin;
         }
      }

      return null;
   }

   public final XYRect getPosition() {
      return this._position;
   }

   public static final Bitmap getScrollArrow(Field field, int arrowType) {
      ThemeAttributeSet ta = getInheritedAttribute(field, 1 << 15 + arrowType);
      if (ta != null) {
         if (ta._scrollArrow[arrowType] == null) {
            ta._scrollArrow[arrowType] = ThemeManager.getActiveTheme().getBitmap(ta._scrollArrowName[arrowType]);
            ta._scrollArrowName[arrowType] = null;
         }

         return ta._scrollArrow[arrowType];
      } else {
         switch (arrowType) {
            case -1:
               return null;
            case 0:
            default:
               return Theme.getThemeBitmap(0);
            case 1:
               return Theme.getThemeBitmap(1);
         }
      }
   }

   public static final int getFocusStyle(Field field) {
      ThemeAttributeSet ta = getInheritedAttribute(field, 256);
      if (ta == null) {
         return Graphics.isColor() ? 3 : 0;
      } else {
         return ta._focusStyle;
      }
   }

   public final Font getFont() {
      return this._font;
   }

   public final Manager getLayout(Object context) {
      return this._layout != null ? this.getTheme().getLayout(this._layout, context) : null;
   }

   public final String getLayoutName() {
      return this._layout;
   }

   public final String[] getLayoutParameters() {
      return this._layoutParams;
   }

   public static final int getTextAlignAsDrawStyle(Field field) {
      ThemeAttributeSet ta = getInheritedAttribute(field, 2097152);
      if (ta == null) {
         return 0;
      }

      switch (ta._textAlign) {
         case 0:
            return 0;
         case 1:
         default:
            return 6;
         case 2:
            return 5;
         case 3:
            return 4;
         case 4:
            return 7;
         case 5:
            return 2;
         case 6:
            return 1;
      }
   }

   final Theme getTheme() {
      return ThemeManager.getActiveTheme();
   }

   public final ThemeAttributeSet$Writer getWriterInternal() {
      return this.getWriterInternal(new DefaultResourceFetcher());
   }

   public final ThemeAttributeSet$Writer getWriterInternal(ResourceFetcher resourceFetcher) {
      return new ThemeAttributeSet$Writer(this, resourceFetcher);
   }

   public final boolean isBackgroundDefined() {
      return (this._set & 524288) != 0;
   }

   public final String getScrollbarName() {
      return this._scrollbarName;
   }
}
