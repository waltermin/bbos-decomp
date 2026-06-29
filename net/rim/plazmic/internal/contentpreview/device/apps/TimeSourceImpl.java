package net.rim.plazmic.internal.contentpreview.device.apps;

import net.rim.plazmic.internal.mediaengine.event.TimeSource;

final class TimeSourceImpl implements TimeSource {
   private float _timeFactor = (float)1065353216;
   private long _previousTime = System.currentTimeMillis();
   private long _sceneTime;
   private final float MIN_VALUE = (float)1015580809;
   private final float MAX_VALUE = (float)1114636288;

   final synchronized float getTimeFactor() {
      return this._timeFactor;
   }

   final synchronized void offsetTime(long offset) {
      this._sceneTime += offset;
   }

   final synchronized void setTimeFactor(float factor) {
      if (factor < 1015580809) {
         this._timeFactor = (float)1015580809;
      } else if (factor > 1114636288) {
         this._timeFactor = (float)1114636288;
      } else {
         this._timeFactor = factor;
      }
   }

   @Override
   public final synchronized long getTime() {
      long currentTime = System.currentTimeMillis();
      this._sceneTime = (long)((float)this._sceneTime + (float)(currentTime - this._previousTime) * this._timeFactor);
      this._previousTime = currentTime;
      return this._sceneTime;
   }
}
