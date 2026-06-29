package net.rim.device.cldc.io.ippp;

public interface DatagramListener {
   void dataReceived(SocketDatagram var1);

   void connectRequestReceived(SocketDatagram var1);

   void disconnectOrderReceived(SocketDatagram var1);

   void errorReceived(SocketDatagram var1);
}
