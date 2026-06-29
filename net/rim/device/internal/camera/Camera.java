package net.rim.device.internal.camera;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.internal.system.EventDispatchManager;

public final class Camera {
   public static final int OPTION_FLASH;
   public static final int OPTION_WHITE_BALANCE;
   public static final int OPTION_CAMERA_EFFECT;
   public static final int OPTION_PICTURE_SIZE;
   public static final int OPTION_QUALITY;
   public static final int OPTION_SET_FREQUENCY;
   public static final int FEATURE_WHITE_BALANCE;
   public static final int FEATURE_DIGITAL_ZOOM;
   public static final int FEATURE_OPTICAL_ZOOM;
   public static final int FEATURE_PORTRAIT_MODE;
   public static final int FEATURE_LANDSCAPE_MODE;
   public static final int FEATURE_COLOUR_EFFECT;
   public static final int FEATURE_JPEG_COMPRESSION;
   public static final int FEATURE_PICTURE_SIZE;
   public static final int FEATURE_FREQ_MANUAL_SELECT;
   public static final int FLASH_OFF;
   public static final int FLASH_ON;
   public static final int FLASH_AUTO;
   public static final int QUALITY_LOW;
   public static final int QUALITY_MID;
   public static final int QUALITY_HIGH;
   public static final int QUALITY_COUNT;
   public static final int WB_AUTO;
   public static final int WB_SUNNY;
   public static final int WB_CLOUDY;
   public static final int WB_NIGHT;
   public static final int WB_INDOOR;
   public static final int WB_TUNGSTEN;
   public static final int WB_FLUORESCENT;
   public static final int CE_NORMAL;
   public static final int CE_NEGATIVE;
   public static final int CE_SOLARISE;
   public static final int CE_SKETCH;
   public static final int CE_EMBOSS;
   public static final int CE_BLACKWHITE;
   public static final int CE_SEPIA;
   public static final int CE_ANTIQUE;
   public static final int FREQUENCY_60_HZ;
   public static final int FREQUENCY_50_HZ;
   private static final int UNSPECIFED_PARM;
   public static int _pictureWidth = -1;
   public static int _pictureHeight = -1;
   public static int _pictureQuality = -1;
   private static int UNKNOWN_MODE_HANDLE = -1;
   public static int CAMERA_MODE_HANDLE = 1;
   public static int VIDEO_MODE_HANDLE = 2;
   private static int _internalHandle = UNKNOWN_MODE_HANDLE;

   public static final native int[] getMaxPictureDimensions();

   public static final int openViewfinder(int x, int y, int width, int height) {
      int handle = UNKNOWN_MODE_HANDLE;
      startViewfinder(x, y, width, height);
      handle = CAMERA_MODE_HANDLE;
      _internalHandle = handle;
      return handle;
   }

   private static final native void startViewfinder(int var0, int var1, int var2, int var3);

   private static final native void stopViewfinder();

   public static final void closeViewfinder(int handle) {
      if (handle == _internalHandle || _internalHandle == UNKNOWN_MODE_HANDLE) {
         stopViewfinder();
         _internalHandle = UNKNOWN_MODE_HANDLE;
      }
   }

   public static final native void pauseViewfinder(Bitmap var0);

   public static final native void resumeViewfinder();

   public static final native boolean takePicture();

   public static final native void getPreview(Bitmap var0);

   public static final native byte[] getPicture(int var0, int var1, int var2, String var3, String var4);

   public static final byte[] getPicture(String timeStamp, String model) {
      if (_pictureWidth != -1 && _pictureHeight != -1 && _pictureQuality != -1) {
         return getPicture(_pictureWidth, _pictureHeight, _pictureQuality, timeStamp, model);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final native int[] getZoomLevels();

   public static final native void setZoomLevel(int var0);

   public static final native void setOption(int var0, int var1);

   public static final void setPictureResolution(int width, int height, int quality) {
      if (quality >= 0 && quality < 3) {
         if (_pictureWidth != width || _pictureHeight != height) {
            _pictureWidth = width & 65535;
            _pictureHeight = height & 65535;
            if ((getFeatures(0) & 128) != 0) {
               setOption(8, _pictureWidth << 16 | _pictureHeight);
            }
         }

         if (_pictureQuality != quality) {
            _pictureQuality = quality;
            if ((getFeatures(0) & 128) != 0) {
               setOption(16, quality);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final native int queryStatus();

   public static final boolean isViewfinderActive() {
      return queryStatus() == 1;
   }

   public static final native int getFeatures(int var0);

   public static final int getColourEffects() {
      return getFeatures(32);
   }

   public static final void addListener(Application app, CameraListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(55) == null) {
            dispatchManager.setDispatcher(55, new CameraEventDispatcher());
         }
      }

      app.addListener(55, listener);
   }

   public static final void removeListener(Application app, CameraListener listener) {
      app.removeListener(55, listener);
   }

   public static final int openVideoViewfinder(int x, int y, int vfWidth, int vfHeight, int recWidth, int recHeight, int audioCodec, int videoCodec) {
      startVideoViewfinder(x, y, vfWidth, vfHeight, recWidth, recHeight, audioCodec, videoCodec);
      _internalHandle = VIDEO_MODE_HANDLE;
      return _internalHandle;
   }

   private static final native void startVideoViewfinder(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public static final native void recordVideoToFile(int var0, int var1);

   public static final native void recordVideoToStream(int var0, int var1);

   public static final native void startVideoRecord();

   public static final native void stopVideoRecord();

   public static final native void finalizeVideoRecord();

   public static final native void transcodeVideoFile(int var0, int var1);

   public static final native void transcodeVideoStream(int var0, int var1);
}
