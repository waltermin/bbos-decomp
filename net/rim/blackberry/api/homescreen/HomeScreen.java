package net.rim.blackberry.api.homescreen;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.vm.Process;

public final class HomeScreen {
   private HomeScreen() {
   }

   public static final String getActiveThemeName() {
      return ThemeManager.getActiveName();
   }

   private static final int getModuleHandle() {
      int moduleHandle = Process.currentProcess().getModuleHandle();
      if (moduleHandle == 0) {
         throw new Object("Module handle for the current process is 0");
      } else {
         return moduleHandle;
      }
   }

   public static final int getPreferredIconHeight() {
      Theme theme = ThemeManager.getActiveTheme();
      return theme != null ? theme.getRibbonIconHeight() : 0;
   }

   public static final int getPreferredIconWidth() {
      Theme theme = ThemeManager.getActiveTheme();
      return theme != null ? theme.getRibbonIconWidth() : 0;
   }

   public static final void setName(String name) {
      setName(name, 0);
   }

   public static final void setName(String name, int index) {
      RibbonApi ra = RibbonApi.getInstance();
      if (ra == null) {
         throw new Object();
      }

      ra.setName(getModuleHandle(), index, name);
   }

   public static final void setRolloverIcon(Bitmap rollovericon) {
      setRolloverIcon(rollovericon, ApplicationDescriptor.currentApplicationDescriptor().getIndex());
   }

   public static final void setRolloverIcon(Bitmap rollovericon, int index) {
      RibbonApi ra = RibbonApi.getInstance();
      if (ra == null) {
         throw new Object();
      }

      ra.setRolloverIcon(getModuleHandle(), index, rollovericon);
   }

   public static final boolean supportsIcons() {
      return !isList();
   }

   private static final boolean isList() {
      boolean isList = false;
      Theme currTheme = ThemeManager.getActiveTheme();
      if (currTheme != null) {
         ThemeAttributeSet attrSet = currTheme.getAttributeSet(Tag.create("homescreen"));
         if (attrSet != null) {
            String[] layoutName = attrSet.getLayoutParameters();
            if (layoutName != null && layoutName[1].indexOf("VerticalAppChooser") != -1) {
               isList = true;
            }
         }
      }

      return isList;
   }

   public static final void updateIcon(Bitmap newIcon) {
      updateIcon(newIcon, ApplicationDescriptor.currentApplicationDescriptor().getIndex());
   }

   public static final void updateIcon(Bitmap newIcon, int index) {
      RibbonApi ra = RibbonApi.getInstance();
      if (ra == null) {
         throw new Object();
      }

      ra.setIcon(getModuleHandle(), index, newIcon);
   }
}
