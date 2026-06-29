package net.rim.device.api.system;

public interface USBPortListener2 extends USBPortListener {
   void connectionAuthenticationRequired(int var1);

   void disconnected(int var1);
}
