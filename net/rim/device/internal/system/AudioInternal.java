package net.rim.device.internal.system;

public final class AudioInternal {
   public static final int TONE_DTMF_1 = 0;
   public static final int TONE_DTMF_2 = 1;
   public static final int TONE_DTMF_3 = 2;
   public static final int TONE_DTMF_4 = 4;
   public static final int TONE_DTMF_5 = 5;
   public static final int TONE_DTMF_6 = 6;
   public static final int TONE_DTMF_7 = 8;
   public static final int TONE_DTMF_8 = 9;
   public static final int TONE_DTMF_9 = 10;
   public static final int TONE_DTMF_0 = 13;
   public static final int TONE_DTMF_STAR = 12;
   public static final int TONE_DTMF_HASH = 14;
   public static final int TONE_DTMF_A = 3;
   public static final int TONE_DTMF_B = 7;
   public static final int TONE_DTMF_C = 11;
   public static final int TONE_DTMF_D = 15;
   public static final int TONE_DTMF_SEND = 16;
   public static final int AVC_MODE_NOT_SUPPORTED = 0;
   public static final int AVC_MODE_ENABLED = 2;
   public static final int AVC_MODE_DISABLED = 1;
   public static final int PROG_TONE_NONE = 16;
   public static final int PROG_TONE_END_OF_CALL = 15;
   public static final int PROG_TONE_SUB_BUSY_TONE_EUROPE = 4;
   public static final int PROG_TONE_SUB_CONGESTION_TONE_EUROPE = 6;
   public static final int PROG_TONE_SUB_BUSY_TONE_NA = 5;
   public static final int PROG_TONE_SUB_CONGESTION_TONE_NA = 7;
   public static final int HEADSET_TYPE_MONO = 0;
   public static final int HEADSET_TYPE_STEREO = 1;
   public static final int HEADSET_TYPE_ERROR = 2;
   public static final int HEADSET_TYPE_MONO_NO_MIC = 3;
   public static final int HEADSET_TYPE_STEREO_NO_MIC = 4;

   private AudioInternal() {
   }

   public static final native void audioSetVoiceBandMode(int var0);

   public static final native boolean setToneVolume(int var0);

   public static final native boolean startTone(int var0);

   public static final native boolean stopTone();

   public static final native boolean startInbandTone(int var0);

   public static final native boolean stopInbandTone();

   public static final native boolean mute(boolean var0);

   public static final native boolean requestHACModeChange(boolean var0);

   public static final native boolean getHACMode();

   public static final native boolean requestAVCModeChange(int var0);

   public static final native int getAVCMode();

   public static final int mapDTMFToneToAudioTone(byte dtmfTone) {
      switch (dtmfTone) {
         case 35:
            return 14;
         case 42:
            return 12;
         case 48:
            return 13;
         case 49:
            return 0;
         case 50:
            return 1;
         case 51:
            return 2;
         case 52:
            return 4;
         case 53:
            return 5;
         case 54:
            return 6;
         case 55:
            return 8;
         case 56:
            return 9;
         case 57:
            return 10;
         case 65:
            return 3;
         case 66:
            return 7;
         case 67:
            return 11;
         case 68:
            return 15;
         default:
            throw new IllegalArgumentException();
      }
   }

   public static final native void setCallProgressTone(int var0, boolean var1);

   public static final native int getHeadsetType();

   public static final native int dtmfRead(byte[] var0);

   public static final native boolean setVolume(int var0);
}
