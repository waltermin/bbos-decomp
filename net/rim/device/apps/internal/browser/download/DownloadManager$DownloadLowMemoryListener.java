package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.lowmemory.LowMemoryFailedListener;

class DownloadManager$DownloadLowMemoryListener implements LowMemoryFailedListener {
   boolean _lmm_failed;

   @Override
   public void lowMemoryManagerFailed() {
      this._lmm_failed = true;
   }
}
