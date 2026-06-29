package net.rim.device.api.system;

public interface AudioHeadsetListener extends AudioListener {
   int HEADSET_TYPE_MONO;
   int HEADSET_TYPE_STEREO;
   int HEADSET_SINGLE_BUTTON;
   int HEADSET_ACTION_BUTTON;
   int HEADSET_VOLUME_UP_BUTTON;
   int HEADSET_VOLUME_DOWN_BUTTON;
   int HEADSET_NEXT_BUTTON;
   int HEADSET_PREVIOUS_BUTTON;

   void headsetButtonClick(int var1, int var2);

   void headsetButtonUnclick(int var1, int var2);

   void headsetInserted(int var1);

   void headsetRemoved();
}
