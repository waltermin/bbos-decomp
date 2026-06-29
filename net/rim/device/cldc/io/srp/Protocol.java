package net.rim.device.cldc.io.srp;

import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
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

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      SrpAddress addr = (SrpAddress)super.newDatagramAddressBase(address, swap);
      if (super._addressBase != null) {
         SrpAddress base = (SrpAddress)super._addressBase;
         addr.setLinkType(base.getLinkType());
         addr.setConnectionType(base.getConnectionType());
      }

      return addr;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      SrpAddress addr = (SrpAddress)super.newDatagramAddressBase(addressBase, swap);
      if (super._addressBase != null) {
         SrpAddress base = (SrpAddress)super._addressBase;
         addr.setLinkType(base.getLinkType());
         addr.setConnectionType(base.getConnectionType());
      }

      return addr;
   }

   final int getLinkType() {
      return ((SrpAddress)super._addressBase).getLinkType();
   }

   final int getConnectionType() {
      return ((SrpAddress)super._addressBase).getConnectionType();
   }
}
