package net.rim.blackberry.api.options;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;

final class OptionsManager$OptionsItemWrapper extends SaveableMainScreenOptionsListItem {
   private OptionsProvider _optionsProvider;

   public OptionsManager$OptionsItemWrapper(OptionsProvider optionsProvider) {
      super(optionsProvider.getTitle());
      this._optionsProvider = optionsProvider;
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      this._optionsProvider.populateMainScreen(screen);
   }

   @Override
   protected final boolean save() {
      this._optionsProvider.save();
      return super.save();
   }
}
