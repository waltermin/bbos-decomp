package net.rim.device.cldc.io.sync;

import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramConnectionBase;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      super.openPrim(name, mode, timeouts);
      if (name.startsWith("//")) {
         name = name.substring(2);
         return new SyncConnection(name, super._transport);
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }
}
