package net.rim.device.apps.internal.camera;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;

final class ViewfinderField extends Field implements ActiveMedia {
   private int _vfWidth = 240;
   private int _vfHeight = 180;
   private Bitmap _vfPauseBitmap;
   private int _zoomIndex;
   private int[] _zoomLevels = Camera.getZoomLevels();
   private int _flashMode = 0;
   private int _whiteBalance = 0;
   private int _colourEffect = 0;
   private int _attemptCounter = 0;
   private boolean _forceMedia;
   private int _vfState = 0;
   private int _vfHandle;
   private static ViewfinderField instance;
   private static final int MAX_START_ATTEMPTS = 5;
   private static final long START_ATTEMPTS_DELAY = 500L;
   private static final int VF_STATE_STOPPED = 0;
   private static final int VF_STATE_STARTED = 1;
   private static final int VF_STATE_PAUSED = 2;
   private static final int VF_STATE_STARTING = 3;
   private static final int VF_STATE_ERRORED = 4;

   final boolean adjustZoomLevel(int offset) {
      offset = Math.min(this._zoomLevels.length - 1, Math.max(offset + this._zoomIndex, 0));
      return this.setZoomLevel(offset);
   }

   final boolean setZoomLevel(int index) {
      if (index >= 0 && index < this._zoomLevels.length && index != this._zoomIndex && this._vfState == 1) {
         this._zoomIndex = index;
         Camera.setZoomLevel(this._zoomLevels[index]);
         return true;
      } else {
         return false;
      }
   }

   public final int[] getZoomLevels() {
      return this._zoomLevels;
   }

   public final int getZoomIndex() {
      return this._zoomIndex;
   }

   public final boolean isStarted() {
      return this._vfState == 1;
   }

   final boolean vfTakePicture() {
      boolean isJpeg = false;
      if (this._vfState == 1) {
         isJpeg = Camera.takePicture();
         this.setVfState(0);
         ActiveMediaObservable.setInactive(this);
         return isJpeg;
      } else {
         throw new IllegalStateException("Viewfinder not started");
      }
   }

   final void vfStop(boolean forced) {
      if (this._vfState != 4) {
         if (this._vfState != 0) {
            if (this._vfState != 3) {
               this.setVfState(0);
               if ((Camera.queryStatus() & 3) != 0) {
                  Camera.closeViewfinder(this._vfHandle);
               }
            }
         } else if (forced) {
            label35:
            try {
               this.setVfState(0);
               Camera.closeViewfinder(this._vfHandle);
            } finally {
               break label35;
            }
         }

         ActiveMediaObservable.setInactive(this);
      }
   }

   protected final void vfStart(int counter) {
      if (this._vfState == 0) {
         ActiveMediaObservable.setActive(this);
         this._attemptCounter = counter;
         Application app = Application.getApplication();
         ViewfinderField$ViewfinderStartRunnable start = new ViewfinderField$ViewfinderStartRunnable(this, null);
         if (counter == 0) {
            app.invokeLater(start);
            this.updateVfOptions();
         } else if (app.invokeLater(start, 500, false) < 0) {
            app.invokeLater(start);
         }

         this.setVfState(3);
      }
   }

   final void vfRestart() {
      if (this._vfState == 1) {
         Camera.closeViewfinder(this._vfHandle);
         this.setVfState(3);
         ViewfinderField$ViewfinderStartRunnable start = new ViewfinderField$ViewfinderStartRunnable(this, null);
         start.run();
      } else if (this._vfState == 2 || this._vfState == 0) {
         try {
            this.setVfState(0);
            Camera.closeViewfinder(this._vfHandle);
         } finally {
            return;
         }
      }
   }

   final void setColourEffect(int effect) {
      if ((Camera.getFeatures(0) & 32) != 0) {
         if (this._colourEffect != effect) {
            this._colourEffect = effect;
         }

         Camera.setOption(4, effect);
      }
   }

   final void setFlashMode(int flashMode) {
      if (this._flashMode != flashMode) {
         this._flashMode = flashMode;
      }

      Camera.setOption(1, flashMode);
   }

   final void setWhiteBalance(int wb) {
      if (this._whiteBalance != wb) {
         this._whiteBalance = wb;
      }

      Camera.setOption(2, wb);
   }

   final void vfPause() {
      if (this._vfState != 1) {
         if (this._vfState == 3) {
            ActiveMediaObservable.setInactive(this);
            this.setVfState(0);
         }
      } else {
         if (this._vfPauseBitmap == null || this._vfPauseBitmap.getHeight() != this._vfHeight || this._vfPauseBitmap.getWidth() != this._vfWidth) {
            this._vfPauseBitmap = new Bitmap(197, this._vfWidth, this._vfHeight);
         }

         Camera.pauseViewfinder(this._vfPauseBitmap);
         ActiveMediaObservable.setInactive(this);
         this.setVfState(2);
         this.invalidate();
      }
   }

   final void vfResume() {
      if (this._vfState == 2) {
         this.getScreen().doPaint();
         ActiveMediaObservable.setActive(this);
         Camera.resumeViewfinder();
         this.setVfState(1);
      } else {
         if (this._vfState != 4) {
            this.vfStart(0);
         }
      }
   }

   @Override
   public final boolean isAlert() {
      return false;
   }

   @Override
   public final boolean isForce() {
      return this._forceMedia;
   }

   @Override
   public final int codecUsed() {
      return -1;
   }

   @Override
   public final boolean isAudioInUse() {
      return false;
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._vfState == 2 && this._vfPauseBitmap != null) {
         graphics.drawBitmap(0, 0, this._vfPauseBitmap.getWidth(), this._vfPauseBitmap.getHeight(), this._vfPauseBitmap, 0, 0);
      }
   }

   @Override
   protected final void onUndisplay() {
      this.vfStop(false);
      super.onUndisplay();
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
   protected final void onDisplay() {
      this.vfStart(0);
      super.onDisplay();
   }

   private final void updateVfOptions() {
      this.setFlashMode(this._flashMode);
      this.setWhiteBalance(this._whiteBalance);
      this.setColourEffect(this._colourEffect);
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
   protected final void layout(int width, int height) {
      int tempWidth = Math.min(width, height * 4 / 3) & -16;
      int tempHeight = tempWidth * 3 / 4;
      this._vfHeight = tempHeight;
      this._vfWidth = tempWidth;
      this.setExtent(this._vfWidth, this._vfHeight);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return false;
   }

   private ViewfinderField() {
      super(36028809903865856L);
      this.setVfState(0);
      if (this._zoomLevels == null || this._zoomLevels.length == 0) {
         this._zoomLevels = new int[1];
         this._zoomLevels[0] = 100;
      }
   }

   static final ViewfinderField getInstance() {
      if (instance == null) {
         instance = new ViewfinderField();
      }

      return instance;
   }

   private final void setVfState(int newState) {
      if (this._vfState != newState) {
         this._vfState = newState;
      }
   }
}
