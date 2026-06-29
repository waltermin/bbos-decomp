package net.rim.device.internal.media;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.media.metadata.MetadataHandlerFactoryImpl;
import net.rim.device.internal.system.InternalServices;

public final class AAAmain {
   private static String STREAMING_PLAYER = "net.rim.device.internal.media.StreamingPlayer";
   private static String STREAMING_MEDIA_PLAYER = "net.rim.device.internal.media.StreamingMediaPlayer";

   AAAmain() {
   }

   public static final void libMain(String[] args) {
      register();
   }

   public static final void register() {
      MediaStreamingManagerImpl.init();
      MetadataHandlerFactoryImpl.init();
      PlayerRegistry.register("audio/midi", "net.rim.device.internal.media.MidiPlayer");
      PlayerRegistry.register("audio/x-gsm", STREAMING_PLAYER);
      PlayerRegistry.register("audio/basic", STREAMING_PLAYER);
      if (!InternalServices.isSoftwareCapable(8)) {
         PlayerRegistry.register("audio/x-wav", STREAMING_PLAYER);
         PlayerRegistry.register("audio/amr", STREAMING_PLAYER);
         PlayerRegistry.register("audio/mpeg", STREAMING_PLAYER);
         PlayerRegistry.register("audio/mp4", STREAMING_PLAYER);
         PlayerRegistry.register("audio/aac", STREAMING_PLAYER);
         PlayerRegistry.register("audio/3gpp", STREAMING_PLAYER);
         if (RadioInfo.getNetworkType() == 4) {
            PlayerRegistry.register("audio/3gpp2", STREAMING_PLAYER);
         }
      } else {
         if (InternalServices.isSoftwareCapable(2)) {
            PlayerRegistry.register("audio/x-wav", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("audio/amr", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("audio/mpeg", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("audio/mp4", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("audio/aac", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("audio/3gpp", STREAMING_MEDIA_PLAYER);
            if (RadioInfo.getNetworkType() == 4) {
               PlayerRegistry.register("audio/3gpp2", STREAMING_MEDIA_PLAYER);
            }
         }

         if (InternalServices.isSoftwareCapable(7)) {
            PlayerRegistry.register("video/mp4", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("video/3gpp", STREAMING_MEDIA_PLAYER);
            if (RadioInfo.getNetworkType() == 4) {
               PlayerRegistry.register("video/3gpp2", STREAMING_MEDIA_PLAYER);
            }

            if (RadioInfo.getNetworkType() != 4) {
               PlayerRegistry.register("video/x-msvideo", STREAMING_MEDIA_PLAYER);
            }

            PlayerRegistry.register("video/quicktime", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("application/rtsp", STREAMING_MEDIA_PLAYER);
         }

         if (MediaNatives.isAudioDecoderCodecSupported(12)) {
            PlayerRegistry.register("audio/x-ms-wma", STREAMING_MEDIA_PLAYER);
         }

         if (MediaNatives.isAudioDecoderCodecSupported(13)) {
            PlayerRegistry.register("audio/qcelp", STREAMING_MEDIA_PLAYER);
         }

         if (MediaNatives.isVideoDecoderCodecSupported(4)) {
            PlayerRegistry.register("video/x-ms-asf", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("video/x-ms-wm", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("video/x-ms-wmv", STREAMING_MEDIA_PLAYER);
            PlayerRegistry.register("video/x-ms-wmx", STREAMING_MEDIA_PLAYER);
            return;
         }
      }
   }
}
