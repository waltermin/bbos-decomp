package net.rim.device.cldc.io.sync;

import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramConnectionBase;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      super.openPrim(name, mode, timeouts);
      if (name.startsWith("//")) {
         name = name.substring(2);
         return new SyncConnection(name, super._transport);
      } else {
         throw new IOException();
      }
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }
}
