package net.rim.device.cldc.io.commlink;

import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;

public final class Protocol extends DatagramConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      super.openPrim(name, mode, timeouts);
      ((Transport)super._transport).registerName(name);
      return this;
   }

   @Override
   protected final boolean isAddressed(String address) {
      return address != null && address.length() != 0 ? address.equals(super._addressBase.getAddress()) : true;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return this.isAddressed(addressBase.getAddress());
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int offset, int length, String addr) {
      return super.newDatagram(buf, offset, length, super._addressBase.getAddress());
   }

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   public final void pushStatusScreen() {
      ((Transport)super._transport)._statusScreen.push();
   }

   public final void popStatusScreen() {
      ((Transport)super._transport)._statusScreen.pop();
   }
}
