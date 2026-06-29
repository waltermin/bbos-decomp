package net.rim.device.internal.system;

import net.rim.device.api.ui.UiApplication;

final class RIMProcessLauncherMain$UiApplicationImpl extends UiApplication {
   RIMProcessLauncherMain$UiApplicationImpl(Runnable runnable) {
      this.invokeLater(runnable);
   }
}
