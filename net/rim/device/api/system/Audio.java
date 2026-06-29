package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaNatives;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class Audio {
   public static final int AUDIO_CODEC_WAVE;
   public static final int AUDIO_CODEC_ADPCM;
   public static final int AUDIO_CODEC_MIDI;
   public static final int AUDIO_CODEC_MP3;
   public static final int AUDIO_CODEC_G711A;
   public static final int AUDIO_CODEC_G711U;
   public static final int AUDIO_CODEC_VOICENOTE;
   public static final int AUDIO_CODEC_AMR;
   public static final int AUDIO_CODEC_RAW_PCM;
   public static final int AUDIO_CODEC_AAC;
   public static final int AUDIO_CODEC_GSM610;
   public static final int AUDIO_CODEC_WMA;
   public static final int AUDIO_CODEC_QCELP;
   public static final int AUDIO_CODEC_EVRC;
   public static final int SAMPLE_RATE_8KHZ;
   public static final int SAMPLE_RATE_16KHZ;
   public static final int SAMPLE_RATE_32KHZ;
   public static final int SAMPLE_RATE_44_1KHZ;
   public static final int SAMPLE_RATE_48KHZ;
   public static final int AUDIO_OK;
   public static final int AUDIO_ERROR_UNKNOWN;
   public static final int AUDIO_ERROR_BAD_DATA;
   public static final int AUDIO_ERROR_BAD_STATE;
   public static final int AUDIO_ERROR_FILESYSTEM_FULL;
   public static final int AUDIO_REQUEST_PENDING;

   private Audio() {
   }

   private static final void assertDeviceSettingsPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   private static final void assertMediaPermission() {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
   }

   public static final boolean isSupported() {
      return true;
   }

   public static final void enable(boolean enable) {
   }

   public static final native int getVolume();

   public static final boolean setVolume(int volume) {
      assertDeviceSettingsPermission();
      return AudioInternal.setVolume(volume);
   }

   public static final native boolean isHeadsetConnected();

   public static final boolean hasBuiltInHeadset() {
      return InternalServices.isDeviceCapable(3);
   }

   public static final boolean isCodecSupported(int codec) {
      return codec >= 0 && codec < 15 ? MediaNatives.isAudioDecoderCodecSupported(codec) : false;
   }

   public static final boolean isRecordingCodecSupported(int codec) {
      return codec >= 0 && codec < 15 ? MediaNatives.isAudioEncoderCodecSupported(codec) : false;
   }

   public static final int playFile(int audioCodec, int fs, String fileName) {
      throw new UnsupportedOperationException();
   }

   public static final int recordFile(int audioCodec, int fs, String fileName) {
      throw new UnsupportedOperationException();
   }

   public static final int stopFile(int audioCodec, int fs, String fileName) {
      throw new UnsupportedOperationException();
   }

   public static final void addListener(Application app, AudioListener listener) {
      assertMediaPermission();
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(21) == null) {
            dispatchManager.setDispatcher(21, new AudioEventDispatcher());
         }
      }

      app.addListener(21, listener);
   }

   public static final void removeListener(Application app, AudioListener listener) {
      assertMediaPermission();
      app.removeListener(21, listener);
   }
}
