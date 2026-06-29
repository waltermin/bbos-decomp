package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;

public final class BackdoorHelpScreen extends MainScreen {
   public BackdoorHelpScreen(String[] helpStrings) {
      String[] strings = helpStrings;
      if (helpStrings != null && helpStrings.length > 0) {
         this.add(new BackdoorHelpScreen$1(this, strings.length, 36028797018963968L, strings));
      } else {
         this.add((Field)(new Object("no help strings provided")));
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return super.keyChar(c, status, time);
   }

   public static final void showHelpScreen(UiApplication app, String[] helpStrings) {
      UiApplication uiApp = app != null ? app : UiApplication.getUiApplication();
      if (helpStrings != null && helpStrings.length > 0) {
         uiApp.pushModalScreen(new BackdoorHelpScreen(helpStrings));
      }
   }
}
