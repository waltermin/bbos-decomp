package javax.microedition.midlet;

import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

final class MIDletMain$UDPDatagramConnection_PushRegistryConnectionWrapper implements UDPDatagramConnection {
   private UDPDatagramConnection _udpDatagramConnection;
   private Datagram _datagram;

   @Override
   public final void close() {
      this._udpDatagramConnection.close();
   }

   final void pushBack(Datagram d) {
      this._datagram = d;
   }

   @Override
   public final int getLocalPort() {
      return this._udpDatagramConnection.getLocalPort();
   }

   @Override
   public final int getMaximumLength() {
      return this._udpDatagramConnection.getMaximumLength();
   }

   @Override
   public final int getNominalLength() {
      return this._udpDatagramConnection.getNominalLength();
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size) {
      return this._udpDatagramConnection.newDatagram(buf, size);
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size, String addr) {
      return this._udpDatagramConnection.newDatagram(buf, size, addr);
   }

   @Override
   public final Datagram newDatagram(int size) {
      return this._udpDatagramConnection.newDatagram(size);
   }

   @Override
   public final Datagram newDatagram(int size, String addr) {
      return this._udpDatagramConnection.newDatagram(size, addr);
   }

   @Override
   public final void receive(Datagram dgram) {
      synchronized (this) {
         if (this._datagram != null) {
            Datagram d = this._datagram;
            this._datagram = null;
            dgram.setAddress(d);
            byte[] data = d.getData();
            dgram.setLength(data.length);
            dgram.setData(data, 0, data.length);
            return;
         }
      }

      this._udpDatagramConnection.receive(dgram);
   }

   @Override
   public final void send(Datagram dgram) {
      this._udpDatagramConnection.send(dgram);
   }

   @Override
   public final String getLocalAddress() {
      return this._udpDatagramConnection.getLocalAddress();
   }

   public MIDletMain$UDPDatagramConnection_PushRegistryConnectionWrapper(UDPDatagramConnection impl) {
      this._udpDatagramConnection = impl;
   }
}
