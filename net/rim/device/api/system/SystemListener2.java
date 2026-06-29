package net.rim.device.api.system;

public interface SystemListener2 extends SystemListener {
   int POWER_OFF_KEY_PRESSED = 1;
   int USB_STATE_CABLE_CONNECTED = 1;
   int USB_STATE_CABLE_DISCONNECTED = 4;
   int USB_STATE_ENUMERATED = 2;
   int USB_STATE_NOT_ENUMERATED = 8;
   int USB_STATE_MS_INTERFACE_ENUMERATED = 16;

   void powerOffRequested(int var1);

   void cradleMismatch(boolean var1);

   void fastReset();

   void backlightStateChange(boolean var1);

   void usbConnectionStateChange(int var1);
}
