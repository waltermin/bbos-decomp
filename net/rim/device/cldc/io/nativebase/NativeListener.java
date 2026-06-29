package net.rim.device.cldc.io.nativebase;

import net.rim.device.api.io.DatagramAddressBase;

public interface NativeListener {
   int SETUP_NATIVE_LISTENER;

   void datagramStatus(DatagramAddressBase var1, int var2);
}
