package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

final class InputController$ForwardScreenRunnable implements Runnable {
   private Screen _screen;

   public InputController$ForwardScreenRunnable(Screen screen) {
      this._screen = screen;
   }

   @Override
   public final void run() {
      UiApplication app = UiApplication.getUiApplication();
      app.pushScreen(this._screen);
   }
}
