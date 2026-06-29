package net.rim.device.api.ui.theme;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.BorderRounded;

class ThemeManager$DefaultThemeFactory extends Theme$Factory {
   ThemeManager$DefaultThemeFactory() {
      int displayDepth = ThemeManager.log2(Graphics.getNumColors());
      this.setTargetDisplay(Display.getWidth(), Display.getHeight(), displayDepth);
      this.setRemovable(false);
   }

   @Override
   public String getName() {
      return "";
   }

   @Override
   public void populate(Theme$Writer theme) {
      if (Display.getWidth() == 160 && Display.getHeight() == 100) {
         theme.setApplicationIconSize(32, 28);
      } else {
         theme.setApplicationIconSize(32, 32);
      }

      theme.putBorder("popup", new BorderRounded(3, 3, 3, 3, 0));
      ThemeAttributeSet$Writer attr = theme.createThemeAttributeSetWriter("");
      attr.setColor(0, 16777215);
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("inplace");
      attr.setColor(0, 16777215);
      attr.setBorder("popup");
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("list-move");
      attr.setFocusStyle(3);
      attr.setColor(3, 0);
      attr.setColor(2, 16777215);
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("menu");
      attr.setColor(0, 16777215);
      attr.setBorder("popup");
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("popup");
      attr.setColor(0, 16777215);
      attr.setBorder("popup");
      theme.put(attr);
      attr = theme.createThemeAttributeSetWriter("global-popup");
      attr.setColor(0, 16777215);
      attr.setBorder("popup");
      theme.put(attr);
   }
}
