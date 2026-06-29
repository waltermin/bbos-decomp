package net.rim.device.api.ui;

public interface UserInputEventListener {
   int DEVICE_KEYBOARD;
   int DEVICE_TRACKWHEEL;
   int DEVICE_STYLUS;
   int DEVICE_TRACKBALL;

   void onUserInput(int var1, int var2);
}
