package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class MediaNatives {
   public static final int MEDIA_STOPPED;
   public static final int MEDIA_PAUSE_COMPLETE;
   public static final int MEDIA_ERROR;
   public static final int MEDIA_SEEK_ADDRESS;
   public static final int MEDIA_LOADED;
   public static final int MEDIA_STATUS;
   public static final int MEDIA_AUTH_REQUIRED;
   public static final int MEDIA_PARAMS_UPDATED;
   public static final int VIDEO_CODEC_MPEG1;
   public static final int VIDEO_CODEC_MPEG4;
   public static final int VIDEO_CODEC_WMV;
   public static final int VIDEO_CODEC_H263;

   private MediaNatives() {
   }

   static final void addListener(Application app, MediaEventListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(20) == null) {
            dispatchManager.setDispatcher(20, new MediaNatives$MediaNativesEventDispatcher());
         }
      }

      if (app != null && listener != null) {
         app.addListener(20, listener);
      }
   }

   public static final native int getNumberOfStreamingChannels();

   public static final native int getChannelVolume(int var0);

   public static final native int getStreamingDecodeInstances(int var0);

   public static final native int getStreamingEncodeInstances(int var0);

   public static final native int[] getStreamingSessionBufferSizes();

   public static final native int[] getStreamingSessionHandles();

   public static final boolean isAudioEncoderCodecSupported(int codec) {
      return InternalServices.isSoftwareCapable(12) ? getStreamingEncodeInstances(codec) > 0 : false;
   }

   public static final boolean isAudioDecoderCodecSupported(int codec) {
      if (RadioInfo.areWAFsSupported(8) && codec == 6) {
         return true;
      } else {
         return InternalServices.isSoftwareCapable(2) ? getStreamingDecodeInstances(codec) > 0 : false;
      }
   }

   public static final boolean isVideoDecoderCodecSupported(int codec) {
      return InternalServices.isSoftwareCapable(7) ? isVideoDecoderCodecSupported0(codec) : false;
   }

   private static final native boolean isVideoEncoderCodecSupported0(int var0);

   public static final boolean isVideoEncoderCodecSupported(int codec) {
      return InternalServices.isSoftwareCapable(7) ? isVideoEncoderCodecSupported0(codec) : false;
   }

   private static final native boolean isVideoDecoderCodecSupported0(int var0);

   public static final native int playStream(int var0, int var1, int var2, int var3, int var4);

   public static final native boolean playUplinkBuffer0(int var0);

   public static final native int recordPause();

   public static final native int recordResume();

   public static final native int recordStart(int var0, int var1);

   public static final native int recordStop();

   static final void removeListener(Application app, MediaEventListener listener) {
      if (app != null && listener != null) {
         app.removeListener(20, listener);
      }
   }

   public static final native int setChannelVolume(int var0, int var1);

   public static final native int stopStream(int var0, boolean var1);

   public static final native void stopUplinkBuffer0(boolean var0);

   static final native long init0(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, int var9);

   static final native long initFile0(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   static final native long initURL0(String var0, String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   static final native int passCredentials0(int var0, String var1);

   static final native int play0(int var0);

   static final native int pause0(int var0, byte[] var1);

   static final native void populatePauseBitmap0(byte[] var0);

   static final native int seek0(int var0, int var1);

   static final native int seekComplete0(int var0);

   static final native int signifyNoMoreData0(int var0);

   static final native int unload0(int var0, boolean var1);

   static final native int getLength0(int var0);

   static final native boolean isSeekable0(int var0);

   static final native void resize0(int var0, int var1, int var2);

   static final native void relocate0(int var0, int var1, int var2);

   static final native void resizeAndRelocate0(int var0, int var1, int var2, int var3, int var4);

   static final native void parametersChanged0(int var0);

   static final native int getContentWidth0(int var0);

   static final native int getContentHeight0(int var0);

   static final native int getPlayableStreams0(int var0);
}
