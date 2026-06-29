package net.rim.device.apps.internal.options;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;

final class OptionsApp$DisplayOptionRunnable implements Runnable {
   private ApplicationDescriptor _descrip;

   public OptionsApp$DisplayOptionRunnable(ApplicationDescriptor d, String id) {
      this._descrip = new ApplicationDescriptor(d, new String[]{id});
   }

   @Override
   public final void run() {
      ApplicationManager am = ApplicationManager.getApplicationManager();

      try {
         am.runApplication(this._descrip, true);
      } finally {
         return;
      }
   }
}
