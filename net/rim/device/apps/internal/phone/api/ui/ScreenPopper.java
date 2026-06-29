package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.Recognizer;

public final class ScreenPopper {
   private UiApplication _app;
   private Recognizer _recognizer;

   public ScreenPopper(UiApplication app, Recognizer recognizer) {
      this._app = app;
      this._recognizer = recognizer;
   }

   public final void popScreens() {
      while (true) {
         Screen activeScreen = this._app.getActiveScreen();
         if (activeScreen == null) {
            return;
         }

         if (this._recognizer.recognize(activeScreen)) {
            return;
         }

         this._app.popScreen(activeScreen);
      }
   }
}
