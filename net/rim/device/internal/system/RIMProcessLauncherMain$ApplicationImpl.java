package net.rim.device.internal.system;

import net.rim.device.api.system.Application;

final class RIMProcessLauncherMain$ApplicationImpl extends Application {
   RIMProcessLauncherMain$ApplicationImpl(Runnable runnable) {
      this.invokeLater(runnable);
   }
}
