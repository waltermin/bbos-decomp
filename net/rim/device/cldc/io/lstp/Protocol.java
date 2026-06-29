package net.rim.device.cldc.io.lstp;

import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      if (name.length() > 0) {
         LstpUtil.getInstance().registerAppName(name);
      }

      super.openPrim(name, mode, timeouts);
      EventLogger.logEvent(super._transport.GUID, 1229874030, 0);
      return this;
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return super._receiveFilter.equals(addressBase);
   }
}
