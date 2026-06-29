package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.container.FullScreen;

final class OptionsApp$ExitOnExposedScreen extends FullScreen {
   @Override
   protected final void onExposed() {
      System.exit(0);
   }
}
