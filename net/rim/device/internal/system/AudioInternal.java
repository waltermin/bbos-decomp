package net.rim.device.internal.system;

public final class AudioInternal {
   public static final int TONE_DTMF_1;
   public static final int TONE_DTMF_2;
   public static final int TONE_DTMF_3;
   public static final int TONE_DTMF_4;
   public static final int TONE_DTMF_5;
   public static final int TONE_DTMF_6;
   public static final int TONE_DTMF_7;
   public static final int TONE_DTMF_8;
   public static final int TONE_DTMF_9;
   public static final int TONE_DTMF_0;
   public static final int TONE_DTMF_STAR;
   public static final int TONE_DTMF_HASH;
   public static final int TONE_DTMF_A;
   public static final int TONE_DTMF_B;
   public static final int TONE_DTMF_C;
   public static final int TONE_DTMF_D;
   public static final int TONE_DTMF_SEND;
   public static final int AVC_MODE_NOT_SUPPORTED;
   public static final int AVC_MODE_ENABLED;
   public static final int AVC_MODE_DISABLED;
   public static final int PROG_TONE_NONE;
   public static final int PROG_TONE_END_OF_CALL;
   public static final int PROG_TONE_SUB_BUSY_TONE_EUROPE;
   public static final int PROG_TONE_SUB_CONGESTION_TONE_EUROPE;
   public static final int PROG_TONE_SUB_BUSY_TONE_NA;
   public static final int PROG_TONE_SUB_CONGESTION_TONE_NA;
   public static final int HEADSET_TYPE_MONO;
   public static final int HEADSET_TYPE_STEREO;
   public static final int HEADSET_TYPE_ERROR;
   public static final int HEADSET_TYPE_MONO_NO_MIC;
   public static final int HEADSET_TYPE_STEREO_NO_MIC;

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
