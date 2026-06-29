package net.rim.device.api.system;

public interface GANStatusListener extends RadioListener {
   int GAN_EVENT_OCCURRED;

   void ganEventOccurred(int var1, int var2, int var3);
}
