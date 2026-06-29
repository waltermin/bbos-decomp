package net.rim.device.cldc.io.ippp;

import java.io.DataInput;
import java.io.DataOutput;
import net.rim.device.api.io.DatagramBase;

public final class SocketDatagram extends IPPPDatagramBase {
   private String _domainName;
   private short _port;

   public SocketDatagram() {
      this(null, 0, 0);
   }

   public SocketDatagram(byte[] buffer, int offset, int length) {
      super(buffer, offset, length);
   }

   public SocketDatagram(byte[] buffer, int offset, int length, String address) {
      super(buffer, offset, length, address);
   }

   @Override
   public final void simpleReset() {
      super.simpleReset();
      this._domainName = null;
      this._port = 0;
   }

   @Override
   public final void writeProtocolData(DataOutput out) {
      out.writeUTF(this._domainName);
      out.writeShort(this._port);
   }

   @Override
   public final void readProtocolData(DataInput in) {
      this.setDomainName(in.readUTF());
      this.setPort(in.readShort());
   }

   @Override
   public final void copy(DatagramBase srcDatagram) {
      SocketDatagram tcpDatagram = (SocketDatagram)srcDatagram;
      super.copy(tcpDatagram);
      this.setDomainName(tcpDatagram.getDomainName());
      this.setPort(tcpDatagram.getPort());
   }

   public final String getDomainName() {
      return this._domainName;
   }

   @Override
   public final short getPort() {
      return this._port;
   }

   public final void setDomainName(String domainName) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setPort(short port) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
