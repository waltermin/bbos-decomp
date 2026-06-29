package net.rim.wica.runtime.util;

import net.rim.device.api.lowmemory.LowMemoryFailedListener;

class Util$ProvisioningLowMemoryListener implements LowMemoryFailedListener {
   boolean _lmm_failed = false;

   private Util$ProvisioningLowMemoryListener() {
   }

   @Override
   public void lowMemoryManagerFailed() {
      this._lmm_failed = true;
   }

   Util$ProvisioningLowMemoryListener(Util$1 x0) {
      this();
   }
}
