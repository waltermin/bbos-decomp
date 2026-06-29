package net.rim.device.apps.internal.phone.api;

public interface InHolsterEventListener {
   int TRACKWHEEL_ROLL = 0;
   int TRACKWHEEL_CLICK = 1;
   int ESC_DBL_CLICK = 2;
   int HOLSTER_OUT_AND_IN = 3;
   int HOLSTER_OUT = 4;
   int HOLSTER_IN = 5;
   int PHONE_KEY_DOWN = 6;
   int PHONE_KEY_UP = 7;
   int MUTE_KEY_DOWN = 8;
   int VOLUME_KEY_PRESSED = 9;

   void inHolsterEvent(int var1, int var2);
}
