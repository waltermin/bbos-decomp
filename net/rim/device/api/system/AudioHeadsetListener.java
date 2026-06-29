package net.rim.device.api.system;

public interface AudioHeadsetListener extends AudioListener {
   int HEADSET_TYPE_MONO = 0;
   int HEADSET_TYPE_STEREO = 1;
   int HEADSET_SINGLE_BUTTON = 0;
   int HEADSET_ACTION_BUTTON = 1;
   int HEADSET_VOLUME_UP_BUTTON = 2;
   int HEADSET_VOLUME_DOWN_BUTTON = 3;
   int HEADSET_NEXT_BUTTON = 4;
   int HEADSET_PREVIOUS_BUTTON = 5;

   void headsetButtonClick(int var1, int var2);

   void headsetButtonUnclick(int var1, int var2);

   void headsetInserted(int var1);

   void headsetRemoved();
}
