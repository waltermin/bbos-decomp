package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.internal.camera.Camera;

final class VideoViewfinderField extends Field {
   private int _vfWidth = 240;
   private int _vfHeight = 180;
   private int _videoCodec = 2;
   private Bitmap _vfPauseBitmap;
   private int _attemptCounter = 0;
   private int _flashMode = 0;
   private int _colourEffect = 0;
   private int _vfState = 0;
   private int _vfHandle;
   private static final int MAX_START_ATTEMPTS;
   private static final long START_ATTEMPTS_DELAY;
   private static final int VF_STATE_STOPPED;
   private static final int VF_STATE_STARTED;
   private static final int VF_STATE_PAUSED;
   private static final int VF_STATE_STARTING;
   private static final int VF_STATE_ERRORED;

   public VideoViewfinderField() {
      super(36028861443473408L);
      this.setVfState(0);
   }

   public VideoViewfinderField(int width, int height, int vcodec, int colorEffect) {
      this();
      this._vfWidth = width;
      this._vfHeight = height;
      this._videoCodec = vcodec;
      this.setColourEffect(colorEffect);
   }

   public final boolean isStarted() {
      return this._vfState == 1;
   }

   @Override
   protected final void onDisplay() {
      this.vfStart(0);
      super.onDisplay();
   }

   @Override
   protected final void onExposed() {
      this.vfResume();
      super.onExposed();
   }

   @Override
   protected final void onObscured() {
      this.vfPause();
      super.onObscured();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.vfStart(0);
      } else {
         this.vfStop(false);
      }

      super.onVisibilityChange(visible);
   }

   @Override
   protected final void onUndisplay() {
      this.vfStop(false);
      super.onUndisplay();
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this._vfWidth, this._vfHeight);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return false;
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._vfState == 2 && this._vfPauseBitmap != null) {
         graphics.drawBitmap(0, 0, this._vfPauseBitmap.getWidth(), this._vfPauseBitmap.getHeight(), this._vfPauseBitmap, 0, 0);
      }
   }

   public final void setFormat(int width, int height, int codec) {
      this._vfWidth = width;
      this._vfHeight = height;
      this._videoCodec = codec;
      this.updateLayout();
      this.vfRestart();
   }

   public final void setFlashMode(int flashMode) {
      if (this._flashMode != flashMode) {
         this._flashMode = flashMode;
      }

      Camera.setOption(1, VideoRecorderOptions.FLASH_MODE_TABLE[flashMode]);
   }

   final void setColourEffect(int effect) {
      if ((Camera.getFeatures(0) & 32) != 0) {
         if (this._colourEffect != effect) {
            this._colourEffect = effect;
         }

         Camera.setOption(4, effect);
      }
   }

   private final void updateVfOptions() {
      Camera.setOption(2, 0);
      this.setFlashMode(this._flashMode);
      this.setColourEffect(this._colourEffect);
   }

   final void vfPause() {
      if (this._vfState != 1) {
         if (this._vfState == 3) {
            this.setVfState(0);
         }
      } else {
         if (this._vfPauseBitmap == null || this._vfPauseBitmap.getHeight() != this._vfHeight || this._vfPauseBitmap.getWidth() != this._vfWidth) {
            this._vfPauseBitmap = (Bitmap)(new Object(197, this._vfWidth, this._vfHeight));
         }

         Screen screen = this.getScreen();
         if (screen instanceof VideoRecorderScreen && ((VideoRecorderScreen)screen).isRecording()) {
            ((VideoRecorderScreen)screen).activatePause();
         }

         Camera.pauseViewfinder(this._vfPauseBitmap);
         this.setVfState(2);
         this.invalidate();
      }
   }

   final void vfResume() {
      if (this._vfState == 2) {
         this.getScreen().doPaint();
         Camera.resumeViewfinder();
         this.setVfState(1);
      } else {
         if (this._vfState != 4) {
            this.vfStart(0);
         }
      }
   }

   final void vfRestart() {
      if (this._vfState == 1) {
         Camera.closeViewfinder(this._vfHandle);
         this.setVfState(3);
         VideoViewfinderField$ViewfinderStartRunnable start = new VideoViewfinderField$ViewfinderStartRunnable(this, null);
         start.run();
      }
   }

   protected final void vfStart(int counter) {
      if (this._vfState == 0) {
         this._attemptCounter = counter;
         Application app = Application.getApplication();
         VideoViewfinderField$ViewfinderStartRunnable start = new VideoViewfinderField$ViewfinderStartRunnable(this, null);
         if (counter == 0) {
            app.invokeLater(start);
            this.updateVfOptions();
         } else if (app.invokeLater(start, 500, false) < 0) {
            app.invokeLater(start);
         }

         this.setVfState(3);
      }
   }

   final void vfStop(boolean forced) {
      if (this._vfState != 4) {
         Screen screen = this.getScreen();
         if (screen instanceof VideoRecorderScreen && ((VideoRecorderScreen)screen).isRecording()) {
            ((VideoRecorderScreen)screen).activateStop();
         }

         if (this._vfState != 0) {
            if (this._vfState != 3) {
               this.setVfState(0);
               if (Camera.isViewfinderActive()) {
                  Camera.closeViewfinder(this._vfHandle);
                  return;
               }
            }
         } else if (forced) {
            try {
               this.setVfState(0);
               Camera.closeViewfinder(this._vfHandle);
               return;
            } finally {
               return;
            }
         }
      }
   }

   private final void setVfState(int newState) {
      if (this._vfState != newState) {
         this._vfState = newState;
      }
   }
}
