package net.rim.device.api.ui.theme;

import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.RegistryListener;
import net.rim.device.internal.ui.UiOptionsRegistry;

class ThemeManager$Listeners implements GlobalEventListener, LowMemoryListener, RegistryListener {
   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4232371946002803201L) {
         Theme theme = ThemeManager._instance._activeTheme;
         if (theme != null) {
            theme.clearAppIconCache();
         }
      }
   }

   @Override
   public boolean freeStaleObject(int priority) {
      boolean ret = false;
      if (priority == 0) {
         Theme theme = ThemeManager._instance._activeTheme;
         if (theme != null) {
            ret = theme.freeStaleObject(priority);
         }
      }

      return ret;
   }

   @Override
   public void registryChanged() {
      this.updateThemeFromRegistry(true);
   }

   private void updateThemeFromRegistry(boolean restore) {
      String newTheme = UiOptionsRegistry.getInstance().getString(-7276267599751932452L);
      if (newTheme != null && !newTheme.equals(ThemeManager._instance._activeThemeName)) {
         int index = ThemeManager.getIndex(newTheme);
         if (index == -1 || index == 0 && Graphics.isColor()) {
            newTheme = null;
         }

         ThemeManager.setActiveTheme(newTheme, restore);
      }
   }

   @Override
   public void registryChanged(long key) {
      if (key == -7276267599751932452L) {
         this.updateThemeFromRegistry(false);
      }
   }
}
