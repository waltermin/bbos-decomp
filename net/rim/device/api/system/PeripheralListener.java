package net.rim.device.api.system;

public interface PeripheralListener {
   int TYPE_UNKNOWN = 3071;
   int TYPE_USER = 2816;
   int TYPE_CAR_CHARGER = 2817;
   int TYPE_CRADLE = 2818;
   int TYPE_NONE = 2819;
   int TYPE_TRAVEL_CHARGER = 2823;

   void peripheralChange(int var1);
}
