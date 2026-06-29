package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;

final class ApplicationMenu$2 implements Runnable {
   @Override
   public final void run() {
      long idle = 1000 * DeviceInfo.getIdleTime();
      if (idle < 30000) {
         Application.getApplication().invokeLater(this, 30000 - idle, false);
      } else if (!ApplicationMenu._isTimerActive) {
         ApplicationMenu.access$402(null);
      } else {
         boolean menuIsUp = ApplicationMenu._currentSystemMenu != null && ApplicationMenu._currentSystemMenu.isDisplayed();
         if (ApplicationMenu._instance.isObscured() && !menuIsUp) {
            Application.getApplication().invokeLater(this, 30000, false);
         } else {
            ApplicationMenu.dismiss();
            ApplicationMenu.access$402(null);
         }
      }
   }
}
