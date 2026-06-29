package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class ApplicationOptionsItem extends MainScreenOptionsListItem {
   public ApplicationOptionsItem() {
      super(OptionsResources.getResourceBundle(), 1427, null, -1514481539159318190L);
   }

   @Override
   protected final MainScreen createMainScreen() {
      return new ApplicationOptionsScreen(this.getDisplayName());
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }
}
