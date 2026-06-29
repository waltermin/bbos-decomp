package net.rim.blackberry.api.options;

import net.rim.device.api.ui.container.MainScreen;

public interface OptionsProvider {
   String getTitle();

   void populateMainScreen(MainScreen var1);

   void save();
}
