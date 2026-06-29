package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.plazmic.internal.mediaengine.service.MediaViewport;

final class MediaLayout$ThreadSwitchingRunnable implements Runnable {
   private MediaLayout _ml;
   private boolean _invalidatePending;

   MediaLayout$ThreadSwitchingRunnable(MediaLayout ml) {
      this._ml = ml;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._invalidatePending = false;
      }

      MediaViewport vp = this._ml.getMediaViewport();
      vp.invalidate();
      MediaLayout.access$800(this._ml, vp.getDirtyX(), vp.getDirtyY(), vp.getDirtyWidth(), vp.getDirtyHeight());
   }
}
