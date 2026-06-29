package net.rim.device.api.ui;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

class FontPersist implements Persistable {
   public String _fontFamily;
   public int _fontHeight;
   public int _fontStyle;
   public int _fontEffects;
   public int _fontAntialiasing;
   public int[] _fontTransform;
   private static final long DEFAULT_FONT_KEY;

   public FontPersist(Font aFont) {
      this._fontFamily = aFont.getFontFamily().getName();
      this._fontHeight = aFont.getHeight(4194306);
      this._fontStyle = aFont.getStyle();
      this._fontEffects = aFont.getEffects();
      this._fontAntialiasing = aFont.getAntialiasMode();
      this._fontTransform = new int[6];
      System.arraycopy(aFont.getTransform(), 0, this._fontTransform, 0, 6);
   }

   static void setDefaultFont(Font aFont) {
      FontPersist fp = new FontPersist(aFont);
      PersistentObject po = RIMPersistentStore.getPersistentObject(3057605627993471691L);
      po.setContents(fp, 51);
      po.commit();
   }

   static Font getDefaultFont() {
      PersistentObject po = RIMPersistentStore.getPersistentObject(3057605627993471691L);
      FontPersist fp = (FontPersist)po.getContents();
      if (fp != null) {
         String[] fams = FontRegistry.getFontFamilies();
         if (fams != null && fams.length > 0 && fp._fontFamily != null) {
            for (int i = 0; i < fams.length; i++) {
               if (fp._fontFamily.equals(fams[i])) {
                  FontFamily ff = FontRegistry.get(fams[i]);
                  return ff.getFont(
                     fp._fontStyle,
                     fp._fontHeight,
                     4194306,
                     fp._fontAntialiasing,
                     fp._fontEffects,
                     fp._fontTransform[0],
                     fp._fontTransform[1],
                     fp._fontTransform[2],
                     fp._fontTransform[3],
                     fp._fontTransform[4],
                     fp._fontTransform[5]
                  );
               }
            }
         }
      }

      return null;
   }
}
