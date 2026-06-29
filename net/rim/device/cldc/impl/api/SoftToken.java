package net.rim.device.cldc.impl.api;

public interface SoftToken {
   String getSerialNum();

   String getNickName();

   String getPasscode();

   boolean isPINCached();

   void clearCachedPIN();
}
