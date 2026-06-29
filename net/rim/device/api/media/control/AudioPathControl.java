package net.rim.device.api.media.control;

import javax.microedition.media.Control;

public interface AudioPathControl extends Control {
   int AUDIO_PATH_HANDSET;
   int AUDIO_PATH_HANDSFREE;
   int AUDIO_PATH_BLUETOOTH;
   int AUDIO_PATH_HEADSET;
   int AUDIO_PATH_HEADSET_HANDSFREE;
   int AUDIO_PATH_BLUETOOTH_A2DP;
   int NUM_AUDIO_PATHS;
   int DEFAULT_SINK;

   void setAudioPath(int var1);

   int getAudioPath();

   boolean canSwitchToPath(int var1);

   void toggleSpeakerphone();

   void resetAudioPath();

   void forceActive(boolean var1);

   boolean isPathExplicitlySet();
}
