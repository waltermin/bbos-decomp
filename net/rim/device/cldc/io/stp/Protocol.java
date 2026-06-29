package net.rim.device.cldc.io.stp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase address) {
      return true;
   }
}
