package net.rim.device.cldc.io.nativebase;

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;

public class NativeConnectionBase extends DatagramConnectionBase {
   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) throws ConnectionNotFoundException {
      if (!this.checkNetwork()) {
         throw new ConnectionNotFoundException();
      }

      super.openPrim(name, mode, timeouts);
      EventLogger.logEvent(super._transport.GUID, 1229874030, 0);
      return this;
   }

   @Override
   protected boolean isAddressed(DatagramAddressBase addressBase) {
      return super._receiveFilter.equals(addressBase);
   }

   protected boolean checkNetwork() {
      return true;
   }

   @Override
   public int getProperties(String name) {
      return 2;
   }

   public DatagramBase receiveDatagramBase() {
      return null;
   }
}
