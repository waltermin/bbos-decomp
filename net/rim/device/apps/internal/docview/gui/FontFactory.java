package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.util.IntHashtable;

public final class FontFactory {
   private String _fontFamilyName;
   private Font _baseFont;
   private IntHashtable _fontHash = (IntHashtable)(new Object());
   private static final int PREF_HEIGHT;

   public final void setFontFamilyName(String fontFamily) {
      if (this._fontFamilyName == null || this._baseFont == null || fontFamily.compareTo(this._baseFont.getFontFamily().toString()) != 0) {
         this._baseFont = null;
         FontFamily ff = FontFamily.forName(fontFamily);
         this._fontFamilyName = fontFamily;
         if (ff.isHeightSupported(10)) {
            this._baseFont = ff.getFont(0, 10);
         } else {
            int[] heights = ff.getHeights();
            if (heights.length > 1) {
               this._baseFont = ff.getFont(0, heights[1]);
            } else {
               this._baseFont = ff.getFont(0, heights[0]);
            }
         }

         this._fontHash.clear();
      }
   }

   public final Font getFont(int fontStyle, int cpdSize) {
      IntHashtable fontStyleHash = null;
      if (this._fontHash.containsKey(cpdSize)) {
         fontStyleHash = (IntHashtable)this._fontHash.get(cpdSize);
      }

      if (fontStyleHash != null) {
         if (fontStyleHash.containsKey(fontStyle)) {
            return (Font)fontStyleHash.get(fontStyle);
         }
      } else {
         fontStyleHash = (IntHashtable)(new Object());
         this._fontHash.put(cpdSize, fontStyleHash);
      }

      if (this._baseFont == null) {
         try {
            this.setFontFamilyName(Font.getDefault().getFontFamily().toString());
         } finally {
            ;
         }
      }

      Font font = this._baseFont.derive(fontStyle, cpdSize, 4194307);
      fontStyleHash.put(fontStyle, font);
      return font;
   }
}
