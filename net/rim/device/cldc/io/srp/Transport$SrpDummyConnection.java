package net.rim.device.cldc.io.srp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;

final class Transport$SrpDummyConnection extends DatagramConnectionBase {
   Transport$SrpDummyConnection() {
      super._isActive = true;
   }

   @Override
   public final Datagram newDatagram(int length) {
      this.checkForClosed();
      return new SrpDatagramInternal(null, 0, length);
   }

   @Override
   public final Datagram newDatagram(byte[] buffer, int offset, int length) {
      this.checkForClosed();
      return new SrpDatagramInternal(buffer, offset, length);
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return true;
   }
}
