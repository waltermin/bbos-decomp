package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class ThemeOptionsItem extends MainScreenOptionsListItem {
   public ThemeOptionsItem() {
      super(OptionsResources.getString(1439), null);
   }

   @Override
   protected final MainScreen createMainScreen() {
      return new ThemeOptionsScreen();
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }
}
