package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.bis.BISLaunch;
import net.rim.device.apps.internal.bis.launch.resource.ApplicationResources;

public final class InitializationScreen extends BaseScreen {
   public InitializationScreen() {
      super(ApplicationResources.getString(14));
   }

   @Override
   public final void onDisplay() {
      BISLaunch app = (BISLaunch)UiApplication.getUiApplication();
      app.checkUpdates();
   }
}
