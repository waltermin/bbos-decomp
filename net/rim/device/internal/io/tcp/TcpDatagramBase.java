package net.rim.device.internal.io.tcp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;

public class TcpDatagramBase extends DatagramBase {
   public TcpDatagramProperties _tcpProps;

   public TcpDatagramBase() {
   }

   public TcpDatagramBase(byte[] buffer, int offset, int length, DatagramAddressBase addressBase) {
   }

   public TcpDatagramBase(byte[] buffer, int offset, int length, String addressString) {
   }

   @Override
   public void reset() {
      this._tcpProps = null;
      super.reset();
   }

   public void setData(byte[] buffer, int offset, int length, DatagramAddressBase myAddress) {
      super.setData(buffer, offset, length);
      super._addressBase = myAddress;
   }

   @Override
   public void copy(DatagramBase datagram) {
      super.copy(datagram);
      if (datagram instanceof TcpDatagramBase) {
         this._tcpProps = ((TcpDatagramBase)datagram)._tcpProps;
      } else {
         this._tcpProps = null;
      }
   }
}
