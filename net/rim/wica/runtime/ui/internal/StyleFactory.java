package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;
import net.rim.wica.runtime.persistence.Resource;

public final class StyleFactory {
   private IntHashtable _styleCache = new IntHashtable(10);
   private StyleCollection _styles;
   private ResourceProvider _resourceProvider;

   StyleFactory(StyleCollection styles, ResourceProvider resourceProvider) {
      this._styles = styles;
      this._resourceProvider = resourceProvider;
   }

   public final ThemeAttributeSet getStyle(int styleId) {
      ThemeAttributeSet style = (ThemeAttributeSet)this._styleCache.get(styleId);
      if (style == null) {
         if (this._styles == null) {
            return null;
         }

         style = this.createStyle(styleId);
      }

      return style;
   }

   private final ThemeAttributeSet createStyle(int styleId) {
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer writer = themeWriter.createThemeAttributeSetWriter(null);
      if (Graphics.isColor()) {
         if (this._styles.hasProperty(styleId, 0)) {
            writer.setColor(0, this._styles.getIntProperty(styleId, 0));
         }

         if (this._styles.hasProperty(styleId, 1)) {
            writer.setColor(1, this._styles.getIntProperty(styleId, 1));
         }

         if (this._styles.hasProperty(styleId, 8)) {
            Resource resource = this._resourceProvider.getApplicationResource(this._styles.getIntProperty(styleId, 8));
            if (resource != null) {
               writer.setBackgroundImage(EncodedImage.createEncodedImage(resource.getData(), 0, resource.size()).getBitmap());
            }
         }
      }

      if (this._styles.hasProperty(styleId, 2)) {
         writer.setFontFamily(this._styles.getStringProperty(styleId, 2));
      }

      if (this._styles.hasProperty(styleId, 3)) {
         writer.setFontSize(this._styles.getIntProperty(styleId, 3), 3);
      }

      int style = 0;
      if (this._styles.hasProperty(styleId, 4)) {
         style |= 1;
      }

      if (this._styles.hasProperty(styleId, 5)) {
         style |= 2;
      }

      if (this._styles.hasProperty(styleId, 7)) {
         style |= 4;
      }

      if (style != 0) {
         writer.setFontStyle(style);
      }

      ThemeAttributeSet attributes = writer.getThemeAttributeSet();
      attributes.apply();
      return attributes;
   }
}
