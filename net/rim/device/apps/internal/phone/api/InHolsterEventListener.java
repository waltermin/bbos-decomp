package net.rim.device.apps.internal.phone.api;

public interface InHolsterEventListener {
   int TRACKWHEEL_ROLL;
   int TRACKWHEEL_CLICK;
   int ESC_DBL_CLICK;
   int HOLSTER_OUT_AND_IN;
   int HOLSTER_OUT;
   int HOLSTER_IN;
   int PHONE_KEY_DOWN;
   int PHONE_KEY_UP;
   int MUTE_KEY_DOWN;
   int VOLUME_KEY_PRESSED;

   void inHolsterEvent(int var1, int var2);
}
