package net.rim.device.api.ui;

public interface UserInputEventListener {
   int DEVICE_KEYBOARD = 1;
   int DEVICE_TRACKWHEEL = 2;
   int DEVICE_STYLUS = 3;
   int DEVICE_TRACKBALL = 4;

   void onUserInput(int var1, int var2);
}
