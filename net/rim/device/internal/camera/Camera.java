package net.rim.device.internal.camera;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.internal.system.EventDispatchManager;

public final class Camera {
   public static final int OPTION_FLASH = 1;
   public static final int OPTION_WHITE_BALANCE = 2;
   public static final int OPTION_CAMERA_EFFECT = 4;
   public static final int OPTION_PICTURE_SIZE = 8;
   public static final int OPTION_QUALITY = 16;
   public static final int OPTION_SET_FREQUENCY = 32;
   public static final int FEATURE_WHITE_BALANCE = 1;
   public static final int FEATURE_DIGITAL_ZOOM = 2;
   public static final int FEATURE_OPTICAL_ZOOM = 4;
   public static final int FEATURE_PORTRAIT_MODE = 8;
   public static final int FEATURE_LANDSCAPE_MODE = 16;
   public static final int FEATURE_COLOUR_EFFECT = 32;
   public static final int FEATURE_JPEG_COMPRESSION = 64;
   public static final int FEATURE_PICTURE_SIZE = 128;
   public static final int FEATURE_FREQ_MANUAL_SELECT = 256;
   public static final int FLASH_OFF = 0;
   public static final int FLASH_ON = 1;
   public static final int FLASH_AUTO = 2;
   public static final int QUALITY_LOW = 0;
   public static final int QUALITY_MID = 1;
   public static final int QUALITY_HIGH = 2;
   public static final int QUALITY_COUNT = 3;
   public static final int WB_AUTO = 0;
   public static final int WB_SUNNY = 1;
   public static final int WB_CLOUDY = 2;
   public static final int WB_NIGHT = 3;
   public static final int WB_INDOOR = 4;
   public static final int WB_TUNGSTEN = 5;
   public static final int WB_FLUORESCENT = 6;
   public static final int CE_NORMAL = 0;
   public static final int CE_NEGATIVE = 1;
   public static final int CE_SOLARISE = 2;
   public static final int CE_SKETCH = 3;
   public static final int CE_EMBOSS = 4;
   public static final int CE_BLACKWHITE = 5;
   public static final int CE_SEPIA = 6;
   public static final int CE_ANTIQUE = 7;
   public static final int FREQUENCY_60_HZ = 1;
   public static final int FREQUENCY_50_HZ = 0;
   private static final int UNSPECIFED_PARM = -1;
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
