package net.rim.device.internal.media;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.menu.MenuItemPrefab;
import net.rim.device.internal.util.OptionsRegistry$Listener;

final class MediaOptionsUtilities$VolumeBoostMoreInformationScreen extends MainScreen implements OptionsRegistry$Listener {
   private boolean _closeWhenVisible;

   MediaOptionsUtilities$VolumeBoostMoreInformationScreen() {
      MediaOptionsRegistry.getInstance().addOptionsRegistryChangeListener(this);
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1 && this.shouldInvoke()) {
         this.invokeDefaultMenuItem(0);
      }

      return true;
   }

   private final boolean shouldInvoke() {
      boolean invoke = false;
      MenuItem item = this.getDefaultMenuItem(0);
      if (!(item instanceof MenuItemPrefab)) {
         invoke = true;
      }

      return invoke;
   }

   @Override
   public final void onOptionsRegistryChange(long key) {
      if (key == 2886183832722201160L) {
         this._closeWhenVisible = true;
      }
   }

   @Override
   protected final void onUndisplay() {
      MediaOptionsRegistry.getInstance().removeOptionsRegistryChangeListener(this);
      super.onUndisplay();
   }

   @Override
   protected final void onExposed() {
      if (this._closeWhenVisible) {
         this._closeWhenVisible = false;
         this.close();
      }
   }
}
