package net.rim.device.cldc.io.commlink;

import net.rim.device.api.io.DatagramBase;

final class CommLinkDatagram extends DatagramBase {
   CommLinkDatagram(byte[] buffer, int offset, int length, String address) {
   }

   @Override
   public final void copy(DatagramBase datagram) {
      super.copy(datagram);
      datagram.copyFlagsInto(this);
   }
}
