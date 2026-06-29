package net.rim.device.api.system;

public interface SystemListener2 extends SystemListener {
   int POWER_OFF_KEY_PRESSED;
   int USB_STATE_CABLE_CONNECTED;
   int USB_STATE_CABLE_DISCONNECTED;
   int USB_STATE_ENUMERATED;
   int USB_STATE_NOT_ENUMERATED;
   int USB_STATE_MS_INTERFACE_ENUMERATED;

   void powerOffRequested(int var1);

   void cradleMismatch(boolean var1);

   void fastReset();

   void backlightStateChange(boolean var1);

   void usbConnectionStateChange(int var1);
}
