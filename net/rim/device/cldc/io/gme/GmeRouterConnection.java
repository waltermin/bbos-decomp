package net.rim.device.cldc.io.gme;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;

class GmeRouterConnection {
   protected Transport _transport;
   protected DatagramConnectionBase _subConnection;

   public GmeRouterConnection(Transport transport, DatagramConnectionBase subConnection) {
      this._transport = transport;
      this._subConnection = subConnection;
      transport._receiveThread.addConnection(subConnection, this);
   }

   public boolean isSendChoked() {
      throw null;
   }

   public void send(DatagramBase _1, GMEDatagramInfo _2, Datagram _3) {
      throw null;
   }

   public void cancel(Datagram datagram) {
      this._subConnection.cancel(datagram);
   }

   public void receivedFrom(GMEDatagramInfo _1) {
      throw null;
   }
}
