package net.rim.device.apps.internal.vad;

import net.rim.device.api.lowmemory.LowMemoryFailedListener;

final class VADEngineManager$VADLowMemoryListener implements LowMemoryFailedListener {
   boolean _lmm_failed;

   @Override
   public final void lowMemoryManagerFailed() {
      this._lmm_failed = true;
   }
}
