package net.rim.device.theme.bbdimension_today_320x240_b;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.theme.Theme$Factory;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;

public final class ThemeFactory extends Theme$Factory {
   public ThemeFactory() {
      super("Bbdimension_today_320x240_b", "Bbdimension_zen_320x240_b");
      this.setTargetDisplay(240, 260, 16);
      this.setRemovable(false);
   }

   public static final void main(String[] args) {
      boolean addTheme = true;
      if (addTheme) {
         ThemeManager.add(new ThemeFactory());
      }
   }

   @Override
   public final String getName(Locale locale) {
      return ResourceBundle.getBundle(2069654640674994680L, "net.rim.device.theme.bbdimension_today_320x240_b.resource.Theme").getString(0);
   }

   @Override
   public final int getPriority() {
      return 50;
   }

   @Override
   public final void populate(Theme$Writer theme) {
      theme.setResourceBundle(ResourceBundle.getBundle(2069654640674994680L, "net.rim.device.theme.bbdimension_today_320x240_b.resource.Theme"));
      theme.addResources();
      theme.setOption("DateFormatResourceId", "3");
      theme.setOption("TimeFormatResourceId", "4");
      theme.setApplicationIconSize(48, 36);
      theme.addAlias("net_rim_bb_file_explorer.Pictures", "net_rim_bb_explorer_picture.Pictures");
      theme.addAlias("net_rim_bb_file_explorer.Pictures~focus", "net_rim_bb_explorer_picture.Pictures~focus");
      theme.setThumbnailName("MyThumbnail");
      ThemeAttributeSet$Writer attr = theme.createThemeAttributeSetWriter("homescreen");
      attr.setLayout("homescreen.pme");
      attr.setColor(0, 16777215);
      attr.setBackgroundImage("background_homescreen.png");
      theme.put(attr);
   }
}
