package net.rim.device.api.ui;

import com.sun.cldc.i18n.Helper;

public class FontLogicHelper {
   static final int MIN_HEIGHT_ARABIC_STROKE_BOLD;
   static final int MIN_HEIGHT_ARABIC_NO_STROKE_BOLD;
   static final int MIN_HEIGHT_ARABIC_STROKE_NORMAL;
   static final int MIN_HEIGHT_ARABIC_NO_STROKE_NORMAL;
   static final int MIN_HEIGHT_ASIAN_STROKE_BOLD;
   static final int MIN_HEIGHT_ASIAN_NO_STROKE_BOLD;
   static final int MIN_HEIGHT_ASIAN_STROKE_NORMAL;
   static final int MIN_HEIGHT_ASIAN_NO_STROKE_NORMAL;
   private static TextGraphics _textGraphics = new TextGraphics("BBMillbank", 10);

   public static boolean fontLegible(TextGraphics textGraphics, int locale) {
      switch (locale & -65536) {
         case 1634861056:
         case 1751449600:
            return fontLegible(textGraphics, 15, 13, 12, 10);
         case 1784741888:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int hanMask = textGraphics.getStyle() & 7168;
               if (hanMask != 3072) {
                  return false;
               }
            }

            return fontLegible(textGraphics, 19, 19, 15, 15);
         case 1802436608:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int hanMask = textGraphics.getStyle() & 7168;
               if (hanMask != 4096) {
                  return false;
               }
            }

            return fontLegible(textGraphics, 19, 19, 15, 15);
         case 2053636096:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int hanMask = textGraphics.getStyle() & 7168;
               if (locale == 2053653326) {
                  if (hanMask != 2048) {
                     return false;
                  }
               } else if (hanMask != 1024) {
                  return false;
               }
            }

            return fontLegible(textGraphics, 19, 19, 15, 15);
         default:
            return true;
      }
   }

   private static boolean fontLegible(
      TextGraphics textGraphics, int minHeightStrokeBold, int minHeightNoStrokeBold, int minHeightStrokeNormal, int minHeightNoStrokeNormal
   ) {
      int height = textGraphics.getHeight();
      if (height >= minHeightStrokeBold) {
         return true;
      } else if (height >= minHeightNoStrokeBold) {
         return (textGraphics.getStyle() & 1) == 0 || textGraphics.getEffects() != 768;
      } else if (height >= minHeightStrokeNormal) {
         return (textGraphics.getStyle() & 1) == 0;
      } else {
         return height >= minHeightNoStrokeNormal ? (textGraphics.getStyle() & 1) == 0 && textGraphics.getEffects() != 768 : false;
      }
   }

   public static boolean fontLegible(Font font, int locale) {
      _textGraphics.setFontSpec(font);
      return fontLegible(_textGraphics, locale);
   }

   public static boolean getSuggestedFont(TextGraphics textGraphics, int locale, boolean allowSizeChange) {
      switch (locale & -65536) {
         case 1634861056:
         case 1751449600:
            return getSuggestedFont(textGraphics, 15, 13, 12, 10, allowSizeChange);
         case 1784741888:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int newStyle = textGraphics.getStyle() & -7169 | 3072;
               textGraphics.setStyle(newStyle);
            }

            return getSuggestedFont(textGraphics, 19, 19, 15, 15, allowSizeChange);
         case 1802436608:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int newStyle = textGraphics.getStyle() & -7169 | 4096;
               textGraphics.setStyle(newStyle);
            }

            return getSuggestedFont(textGraphics, 19, 19, 15, 15, allowSizeChange);
         case 2053636096:
            if (!textGraphics.getTypefaceName().equals(getAltFontFamily(locale).getName())) {
               int newStyle;
               if (locale == 2053653326) {
                  newStyle = textGraphics.getStyle() & -7169 | 2048;
               } else {
                  newStyle = textGraphics.getStyle() & -7169 | 1024;
               }

               textGraphics.setStyle(newStyle);
            }

            return getSuggestedFont(textGraphics, 19, 19, 15, 15, allowSizeChange);
         default:
            return true;
      }
   }

   private static boolean getSuggestedFont(
      TextGraphics textGraphics,
      int minHeightStrokeBold,
      int minHeightNoStrokeBold,
      int minHeightStrokeNormal,
      int minHeightNoStrokeNormal,
      boolean allowSizeChange
   ) {
      int height = textGraphics.getHeight();
      int style = textGraphics.getStyle();
      if (height >= minHeightStrokeBold) {
         return true;
      }

      if (height >= minHeightNoStrokeBold) {
         if ((style & 1) != 0 && textGraphics.getEffects() == 768) {
            textGraphics.setStyle(style & -2);
            return true;
         } else {
            return true;
         }
      } else if (height >= minHeightStrokeNormal) {
         if ((style & 1) != 0) {
            textGraphics.setStyle(style & -2);
            return true;
         } else {
            return true;
         }
      } else if (height >= minHeightNoStrokeNormal) {
         if ((style & 1) != 0) {
            textGraphics.setStyle(style & -2);
         }

         if (textGraphics.getEffects() != 768) {
            return true;
         } else if (allowSizeChange) {
            textGraphics.setHeight(minHeightStrokeNormal);
            return true;
         } else {
            return false;
         }
      } else {
         if ((style & 1) != 0) {
            textGraphics.setStyle(style & -2);
         }

         if (!allowSizeChange) {
            return false;
         } else if (textGraphics.getEffects() == 768) {
            textGraphics.setHeight(minHeightStrokeNormal);
            return true;
         } else {
            textGraphics.setHeight(minHeightNoStrokeNormal);
            return true;
         }
      }
   }

   public static Font getSuggestedFont(Font font, int locale, boolean allowSizeChange) {
      FontFamily family = getAltFontFamily(locale);
      _textGraphics.setFontSpec(font);
      getSuggestedFont(_textGraphics, locale, allowSizeChange);
      return family.getFont(
         _textGraphics.getStyle(),
         _textGraphics.getHeightWithLeading(),
         0,
         1,
         _textGraphics.getEffects(),
         65536,
         0,
         0,
         65536,
         0,
         0,
         _textGraphics.getEffectsStrokeColor(),
         _textGraphics.getEffectsFillColor()
      );
   }

   public static FontFamily getAltFontFamily(int locale) {
      FontFamily result = null;
      String fName = Helper.getSuggestedTypeface(locale);

      try {
         return FontFamily.forName(fName);
      } catch (ClassNotFoundException var4) {
         return result;
      }
   }

   public static boolean useAltFont(int locale) {
      switch (locale & -65536) {
         case 1667301376:
         case 1668481024:
         case 1684340736:
         case 1701576704:
         case 1701707776:
         case 1702035456:
         case 1718747136:
         case 1752498176:
         case 1769209856:
         case 1852571648:
         case 1886126080:
         case 1886650368:
         case 1953628160:
            return false;
         default:
            return true;
      }
   }
}
