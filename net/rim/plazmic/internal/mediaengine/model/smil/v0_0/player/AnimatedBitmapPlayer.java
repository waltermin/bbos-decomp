package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player;

import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.system.Application;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

public class AnimatedBitmapPlayer implements Player, Runnable {
   private Application _app;
   private int _timerId;
   private int _pollingInterval;
   private AnimatedBitmapField _field;
   private boolean _playing;
   private PlayerListener _listener;
   private long _lastUpdateTime;
   private long _totalMediaTime;

   public AnimatedBitmapPlayer(AnimatedBitmapField field, int pollingInterval) {
      this._field = field;
      this._pollingInterval = pollingInterval;
   }

   @Override
   public int getType() {
      return 2;
   }

   @Override
   public VolumeControl getVolumeControl() {
      return null;
   }

   @Override
   public void start() {
      if (!this._playing) {
         this._playing = true;
         if (this._listener != null) {
            this._app = Application.getApplication();
            this._lastUpdateTime = System.currentTimeMillis();
            this._timerId = this._app.invokeLater(this, this._pollingInterval, true);
         }

         this._field.startAnimation();
      }
   }

   @Override
   public void stop() {
      if (this._playing) {
         this.updateMediaTime();
         this._field.stopAnimation();
         if (this._listener != null) {
            this._app.cancelInvokeLater(this._timerId);
         }

         this._playing = false;
      }
   }

   @Override
   public void close() {
      this.stop();
   }

   @Override
   public void setTime(long time) {
      this._totalMediaTime = time;
      this._lastUpdateTime = System.currentTimeMillis();
   }

   @Override
   public long getTime() {
      return this._totalMediaTime;
   }

   private void updateMediaTime() {
      long currentTime = System.currentTimeMillis();
      this._totalMediaTime = this._totalMediaTime + (currentTime - this._lastUpdateTime);
      this._lastUpdateTime = currentTime;
   }

   @Override
   public void setPlayerListener(PlayerListener listener) {
      this._listener = listener;
   }

   @Override
   public void run() {
      this.updateMediaTime();
      if (!this._field.isAnimationRunning()) {
         this.stop();
         this.onCompletion();
      }
   }

   private void onCompletion() {
      this._listener.notifyComplete(this._totalMediaTime);
   }
}
