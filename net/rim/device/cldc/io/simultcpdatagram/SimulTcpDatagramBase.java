package net.rim.device.cldc.io.simultcpdatagram;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;

public final class SimulTcpDatagramBase extends DatagramBase {
   public SimulTcpDatagramProperties tcpProps;

   public SimulTcpDatagramBase() {
   }

   public SimulTcpDatagramBase(byte[] buffer, int offset, int length, DatagramAddressBase addressBase) {
   }

   public SimulTcpDatagramBase(byte[] buffer, int offset, int length, String addressString) {
   }

   @Override
   public final void reset() {
      this.tcpProps = null;
      super.reset();
   }

   public final void setData(byte[] buffer, int offset, int length, DatagramAddressBase myAddress) {
      super.setData(buffer, offset, length);
      super._addressBase = myAddress;
   }

   @Override
   public final void copy(DatagramBase datagram) {
      super.copy(datagram);
      if (datagram instanceof SimulTcpDatagramBase) {
         this.tcpProps = ((SimulTcpDatagramBase)datagram).tcpProps;
      } else {
         this.tcpProps = null;
      }
   }
}
