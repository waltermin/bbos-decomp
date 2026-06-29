package net.rim.device.apps.api.ribbon;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontLogicHelper;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.ToIntHashtable;

public class TextRibbonComponent implements SimpleRibbonComponent, RibbonComponentInitializer, GlobalEventListener {
   private RibbonComponent$RibbonComponentChangeListener _listener;
   protected int _height;
   protected int _width;
   protected Font _mainFont;
   protected Font _altFont;
   protected Font _font;
   protected int _align;
   protected int _fgColor;
   protected boolean _fgColorSet;
   private static ToIntHashtable _colors = (ToIntHashtable)(new Object(10));

   protected void setAltFont(int locale) {
      if (this._altFont != null) {
         if (FontLogicHelper.fontLegible(this._altFont, locale)) {
            this._font = this._altFont;
         } else {
            this._font = FontLogicHelper.getSuggestedFont(this._altFont, locale, false);
         }
      }
   }

   protected void setMainFont() {
      this._font = this._mainFont;
   }

   protected void updateFont() {
      int locale = Locale.getDefaultForSystem().getCode();
      if (this._altFont != null && FontLogicHelper.useAltFont(locale)) {
         this.setAltFont(locale);
      } else {
         this.setMainFont();
      }
   }

   protected String getDefaultId() {
      return "banner";
   }

   protected String getDefaultTag() {
      return null;
   }

   public int getPreferredHeight() {
      int height = 0;
      if (this._font != null) {
         return this._font.getHeight();
      }

      Font font = null;
      if (font == null) {
         font = Font.getDefault();
      }

      height = font.getHeight();
      if (!this.isTextDescentIncluded()) {
         height -= font.getDescent();
      }

      return height;
   }

   public int getPreferredWidth() {
      return 0;
   }

   protected boolean isTextDescentIncluded() {
      return true;
   }

   public synchronized void ribbonComponentChanged(RibbonComponent component) {
      if (this._listener != null) {
         this._listener.ribbonComponentChanged(this);
      }
   }

   protected ThemeAttributeSet getDefaultThemeAttributes() {
      return null;
   }

   @Override
   public void initialize(Hashtable params, Object context) {
      this._align = parseAlignment(params);
      this._mainFont = this.parseMainFont(params);
      this._altFont = this.parseAltFont(params);
      if (!this._fgColorSet) {
         this._fgColor = parseColor(params, "foreground-color");
         this._fgColorSet = this._fgColor != -1;
      }

      if (this._fgColor == -1) {
         this._fgColor = 0;
      }

      this.updateFont();
   }

   @Override
   public int paintComponent(Graphics _1, int _2, int _3, int _4, int _5, Object _6) {
      throw null;
   }

   @Override
   public synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public void setDimensionsAvailable(int width, int height) {
      if (height == -1) {
         height = this.getPreferredHeight();
      }

      if (width == -1) {
         width = this.getPreferredWidth();
      }

      this._height = height;
      this._width = width;
   }

   @Override
   public void uninitialize() {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.updateFont();
      }
   }

   @Override
   public int getComponentHeight() {
      return this._height;
   }

   @Override
   public int getComponentWidth() {
      return this._width;
   }

   @Override
   public void applyTheme() {
   }

   private Font parseMainFont(Hashtable parms) {
      String fontName = (String)parms.get("font-family");
      return this.parseFont(fontName, parms);
   }

   private Font parseAltFont(Hashtable parms) {
      String altFontName = (String)parms.get("altFont");
      return this.parseFont(altFontName, parms);
   }

   private Font parseFont(String fontName, Hashtable parms) {
      int size = Font.getDefault().getHeight();
      int style = 0;
      String styleText = (String)parms.get("font-style");
      if (styleText != null) {
         if (styleText.toLowerCase().startsWith("bold")) {
            style = 1;
         } else if (styleText.toLowerCase().startsWith("italic")) {
            style = 2;
         }
      }

      String weightText = (String)parms.get("font-weight");
      if (weightText != null && weightText.toLowerCase().startsWith("bold")) {
         style |= 1;
      }

      String sizeString = (String)parms.get("font-size");
      if (sizeString != null) {
         size = Integer.parseInt(sizeString);
      }

      int fill = parseColor(parms, "fill");
      int stroke = parseColor(parms, "stroke");
      String strokeOpacity = (String)parms.get("stroke-opacity");
      if (strokeOpacity != null && stroke != -1) {
         double opacity = Double.parseDouble(strokeOpacity);
         int intOpacity = MathUtilities.clamp(0, (int)Math.floor(opacity * 4643176031446892544L), 255);
         stroke |= ~intOpacity << 24;
      }

      return this.createFont(fontName, style, size, fill, stroke);
   }

   private Font createFont(String fontName, int style, int size, int fill, int stroke) {
      if (fontName != null) {
         try {
            FontFamily family = FontFamily.forName(fontName);
            int[] heights = family.getHeights();
            if (heights != null && heights.length > 0) {
               boolean found = false;

               for (int lv = heights.length - 1; lv >= 0; lv--) {
                  if (heights[lv] == size) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  size = heights[0];
               }
            }

            int effects = 0;
            if (stroke != -1) {
               effects = 768;
            } else if (fill != -1) {
               this._fgColor = fill;
               this._fgColorSet = true;
            }

            stroke = stroke == -1 ? 0 : stroke;
            fill = fill == -1 ? 0 : fill;
            return family.getFont(style, size, 0, 1, effects, 65536, 0, 0, 65536, 0, 0, stroke, fill);
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   private static int parseColor(Hashtable params, String name) {
      String colorStr = (String)params.get(name);
      int color = -1;
      if (colorStr != null) {
         color = _colors.get(colorStr);
         if (color == -1) {
            try {
               return Integer.parseInt(colorStr, 16);
            } finally {
               ;
            }
         }
      }

      return color;
   }

   public static int parseAlignment(Hashtable parms) {
      int style = 0;
      String str = (String)parms.get("align");
      if (str != null) {
         if (str.equals("left")) {
            style |= 6;
         } else if (str.equals("center")) {
            style |= 4;
         } else if (str.equals("right")) {
            style |= 5;
         } else if (str.equals("full")) {
            style |= 7;
         }
      }

      str = (String)parms.get("valign");
      if (str != null) {
         if (str.equals("top")) {
            return style | 48;
         }

         if (str.equals("bottom")) {
            return style | 40;
         }

         if (str.equals("baseline")) {
            return style | 8;
         }

         if (str.equals("center")) {
            return style | 32;
         }

         if (str.equals("full")) {
            style |= 56;
         }
      }

      return style;
   }

   protected TextRibbonComponent() {
   }

   static {
      _colors.put("black", 0);
      _colors.put("blue", 255);
      _colors.put("brown", 10824234);
      _colors.put("gray", 8421504);
      _colors.put("green", 32768);
      _colors.put("grey", 8421504);
      _colors.put("red", 16711680);
      _colors.put("white", 16777215);
      _colors.put("yellow", 16776960);
   }
}
