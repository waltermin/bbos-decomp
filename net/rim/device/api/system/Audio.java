package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaNatives;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class Audio {
   public static final int AUDIO_CODEC_WAVE = 0;
   public static final int AUDIO_CODEC_ADPCM = 1;
   public static final int AUDIO_CODEC_MIDI = 2;
   public static final int AUDIO_CODEC_MP3 = 3;
   public static final int AUDIO_CODEC_G711A = 4;
   public static final int AUDIO_CODEC_G711U = 5;
   public static final int AUDIO_CODEC_VOICENOTE = 6;
   public static final int AUDIO_CODEC_AMR = 7;
   public static final int AUDIO_CODEC_RAW_PCM = 9;
   public static final int AUDIO_CODEC_AAC = 10;
   public static final int AUDIO_CODEC_GSM610 = 11;
   public static final int AUDIO_CODEC_WMA = 12;
   public static final int AUDIO_CODEC_QCELP = 13;
   public static final int AUDIO_CODEC_EVRC = 14;
   public static final int SAMPLE_RATE_8KHZ = 8000;
   public static final int SAMPLE_RATE_16KHZ = 16000;
   public static final int SAMPLE_RATE_32KHZ = 32000;
   public static final int SAMPLE_RATE_44_1KHZ = 44100;
   public static final int SAMPLE_RATE_48KHZ = 48000;
   public static final int AUDIO_OK = 0;
   public static final int AUDIO_ERROR_UNKNOWN = 1;
   public static final int AUDIO_ERROR_BAD_DATA = 2;
   public static final int AUDIO_ERROR_BAD_STATE = 3;
   public static final int AUDIO_ERROR_FILESYSTEM_FULL = 4;
   public static final int AUDIO_REQUEST_PENDING = 5;

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
