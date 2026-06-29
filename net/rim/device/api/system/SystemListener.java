package net.rim.device.api.system;

public interface SystemListener {
   void powerOff();

   void powerUp();

   void batteryLow();

   void batteryGood();

   void batteryStatusChange(int var1);
}
