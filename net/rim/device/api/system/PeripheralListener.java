package net.rim.device.api.system;

public interface PeripheralListener {
   int TYPE_UNKNOWN;
   int TYPE_USER;
   int TYPE_CAR_CHARGER;
   int TYPE_CRADLE;
   int TYPE_NONE;
   int TYPE_TRAVEL_CHARGER;

   void peripheralChange(int var1);
}
