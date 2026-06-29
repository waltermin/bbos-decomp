package net.rim.device.apps.internal.security;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

final class SecurityApp$OpenScreenCloser implements Runnable {
   private SecurityApp$OpenScreenCloser() {
   }

   @Override
   public final void run() {
      UiApplication app = UiApplication.getUiApplication();
      int originalScreenCount = app.getScreenCount();
      if (originalScreenCount != 0) {
         char ch = 27;
         int keycode = Keypad.keycode(ch, 0);
         Screen screen = app.getActiveScreen();
         synchronized (app.getAppEventLock()) {
            screen.processKeyEvent(513, ch, keycode, 0);
            screen.processKeyEvent(515, ch, keycode, 0);
         }

         int newScreenCount = app.getScreenCount();
         if (newScreenCount > 0 && newScreenCount < originalScreenCount) {
            app.invokeLater(this);
         }
      }
   }

   SecurityApp$OpenScreenCloser(SecurityApp$1 x0) {
      this();
   }
}
