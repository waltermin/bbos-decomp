package net.rim.device.api.system;

public interface SystemListener3 extends SystemListener2 {
   void batteryDoorOpened();

   void batteryDoorClosed();

   void usbMSMediumChanged(int var1);
}
