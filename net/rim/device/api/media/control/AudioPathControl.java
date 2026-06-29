package net.rim.device.api.media.control;

import javax.microedition.media.Control;

public interface AudioPathControl extends Control {
   int AUDIO_PATH_HANDSET = 0;
   int AUDIO_PATH_HANDSFREE = 1;
   int AUDIO_PATH_BLUETOOTH = 2;
   int AUDIO_PATH_HEADSET = 3;
   int AUDIO_PATH_HEADSET_HANDSFREE = 4;
   int AUDIO_PATH_BLUETOOTH_A2DP = 5;
   int NUM_AUDIO_PATHS = 6;
   int DEFAULT_SINK = -1;

   void setAudioPath(int var1);

   int getAudioPath();

   boolean canSwitchToPath(int var1);

   void toggleSpeakerphone();

   void resetAudioPath();

   void forceActive(boolean var1);

   boolean isPathExplicitlySet();
}
