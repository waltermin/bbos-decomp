package net.rim.device.theme.bbdimension_icon_320x240_b;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.theme.Theme$Factory;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;

public final class ThemeFactory extends Theme$Factory {
   public ThemeFactory() {
      super("Bbdimension_icon_320x240_b", "Bbdimension_zen_320x240_b");
      this.setTargetDisplay(320, 240, 16);
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
      return ResourceBundle.getBundle(1831297904320042258L, "net.rim.device.theme.bbdimension_icon_320x240_b.resource.Theme").getString(0);
   }

   @Override
   public final int getPriority() {
      return 50;
   }

   @Override
   public final void populate(Theme$Writer theme) {
      theme.addResources();
      theme.setApplicationIconSize(53, 48);
      theme.addAlias("net_rim_bb_file_explorer.Pictures", "net_rim_bb_explorer_picture.Pictures");
      theme.addAlias("net_rim_bb_file_explorer.Pictures~focus", "net_rim_bb_explorer_picture.Pictures~focus");
      theme.setThumbnailName("MyThumbnail");
      theme.createBorderBitmap("popup", 8, 8, 8, 8);
      ThemeAttributeSet$Writer attr = theme.createThemeAttributeSetWriter("application-icon");
      attr.setColor(3, 23776);
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("banner");
      attr.setLayout("banner.pme");
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("homescreen");
      attr.setColor(0, 16777215);
      attr.setBackgroundImage("background_homescreen.png");
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("profiles-menu");
      attr.setFontStyle(1);
      attr.setColor(1, 4473924);
      attr.setColor(0, 16777215);
      attr.setBackgroundImage("background_popup.png");
      attr.setFocusStyle(3);
      attr.setColor(3, 16777215);
      attr.setColor(2, 23776);
      attr.setEdges(2, 0, 0, 0, 0);
      attr.setEdges(0, 0, 0, 0, 0);
      attr.setBorder("popup");
      attr.setPosition(46, 14, 212, 192);
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("profiles-menu-selected");
      attr.setFontStyle(0);
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("status#homescreen");
      attr.setBackgroundImage("background_status.png");
      attr.setFontFamily("Homescreen");
      attr.setAltFontFamily("BBMillbank");
      attr.setFontSize(20, 0, false);
      attr.setFontStyle(1);
      attr.setColor(6, 16250871);
      attr.setColor(7, 4473924);
      attr.setFontStrokeOpacity(153);
      attr.setPosition(0, 0, 320, 26);
      theme.put(attr);
   }
}
