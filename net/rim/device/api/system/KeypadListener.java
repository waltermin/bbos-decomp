package net.rim.device.api.system;

public interface KeypadListener {
   int STATUS_ALT = 1;
   int STATUS_NOT_FROM_KEYPAD = 32768;
   int STATUS_SHIFT = 2;
   int STATUS_CAPS_LOCK = 4;
   int STATUS_KEY_HELD_WHILE_ROLLING = 8;
   int STATUS_ALT_LOCK = 16;
   int STATUS_SHIFT_LEFT = 32;
   int STATUS_SHIFT_RIGHT = 64;
   int STATUS_TRACKWHEEL = 1073741824;
   int STATUS_FOUR_WAY = 536870912;
}
