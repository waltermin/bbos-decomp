package net.rim.device.api.ui.component;

import java.io.IOException;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontRegistry;

class PlaceholderFont {
   private static final byte[] _template = new byte[]{
      115,
      102,
      102,
      52,
      0,
      10,
      0,
      0,
      0,
      0,
      0,
      6,
      0,
      0,
      0,
      0,
      0,
      48,
      0,
      1,
      0,
      0,
      0,
      60,
      0,
      2,
      0,
      0,
      0,
      87,
      0,
      3,
      0,
      0,
      0,
      107,
      0,
      4,
      0,
      0,
      0,
      113,
      0,
      5,
      0,
      0,
      0,
      118,
      0,
      1,
      0,
      0,
      0,
      2,
      0,
      8,
      0,
      112,
      0,
      104,
      0,
      106,
      0,
      100,
      0,
      82,
      0,
      24,
      0,
      102,
      0,
      26,
      0,
      0,
      26,
      0,
      86,
      0,
      118,
      0,
      6,
      0,
      12,
      0,
      124,
      0,
      56,
      2,
      0,
      0,
      -111,
      0,
      -110,
      0,
      1,
      -128,
      -1,
      -4,
      -1,
      -3,
      0,
      1,
      -128,
      0,
      0,
      0,
      3,
      32,
      0,
      0,
      32,
      0,
      0,
      1,
      127,
      64,
      -1,
      64,
      0,
      0
   };

   public static Font getFont(int height) {
      if (height >= 0 && height <= 100) {
         int registered = FontFamily.UNKNOWN_FONT;

         try {
            registered = FontRegistry.getInstance().getTypefaceType("ph");
         } catch (IOException var3) {
         }

         if (registered == FontFamily.UNKNOWN_FONT) {
            FontRegistry.loadFont(_template, "ph", false);
         }

         return FontRegistry.get("ph").getFont(0, height);
      } else {
         throw new IllegalArgumentException("");
      }
   }
}
