package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.internal.system.Security;

class WipeHandheldVerb$WipeHandheldRunnable implements Runnable {
   private boolean _wipeThirdPartyApps;

   WipeHandheldVerb$WipeHandheldRunnable(boolean wipeThirdPartyApps) {
      this._wipeThirdPartyApps = wipeThirdPartyApps;
   }

   @Override
   public void run() {
      if (this._wipeThirdPartyApps) {
         CodeModuleManager.deleteThirdPartyApplications();
      }

      Security.getInstance().deviceUnderAttack();
   }
}
