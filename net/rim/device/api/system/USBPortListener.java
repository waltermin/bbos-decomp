package net.rim.device.api.system;

public interface USBPortListener extends IOPortListener {
   int getChannel();

   void dataNotSent();

   void connectionRequested();
}
