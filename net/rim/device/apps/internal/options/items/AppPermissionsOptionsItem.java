package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class AppPermissionsOptionsItem extends MainScreenOptionsListItem {
   public AppPermissionsOptionsItem() {
      super(OptionsResources.getResourceBundle(), 1964, null, 5294015899860238835L);
   }

   @Override
   protected final MainScreen createMainScreen() {
      return new ApplicationOptionsScreen(this.getDisplayName(), false);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }
}
