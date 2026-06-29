package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.api.lowmemory.LowMemoryFailedListener;

class ModuleInfo$ApplicationDeliveryLowMemoryListener implements LowMemoryFailedListener {
   boolean _lmmFailed;

   private ModuleInfo$ApplicationDeliveryLowMemoryListener() {
   }

   @Override
   public void lowMemoryManagerFailed() {
      this._lmmFailed = true;
   }

   ModuleInfo$ApplicationDeliveryLowMemoryListener(ModuleInfo$1 x0) {
      this();
   }
}
