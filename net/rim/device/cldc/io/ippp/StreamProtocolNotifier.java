package net.rim.device.cldc.io.ippp;

import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.firewall.Firewall;
import net.rim.vm.TraceBack;

public final class StreamProtocolNotifier implements ServerSocketConnection, DatagramListener {
   protected Transport _transport;
   protected URL _url;
   protected boolean _closed;
   protected String _name;
   protected boolean _timeouts;

   @Override
   public final synchronized void close() {
      this.deregisterForNotifications();
      this._closed = true;
      this.notify();
   }

   @Override
   public final synchronized StreamConnection acceptAndOpen() {
      if (this._closed) {
         throw new Object();
      }

      int connectionID = this._transport.getConnectionID((short)this._url.getPort());
      if (connectionID == 0) {
         label42:
         try {
            this.wait();
         } finally {
            break label42;
         }

         connectionID = this._transport.getConnectionID((short)this._url.getPort());
      }

      StreamConnection streamConnection = null;
      if (connectionID != 0) {
         Cache cache = this._transport.getCache();
         Queue queue = cache.get(connectionID);
         DatagramProtocol protocol = new DatagramProtocol(queue, this._name, this._timeouts);
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
            && !Firewall.getInstance().allowConnection(this._url.getScheme(), "", protocol.getProperties(this._url.toString()))) {
            throw new Object("Permission denied");
         } else {
            return new StreamProtocol(protocol, false, true);
         }
      } else {
         throw new Object("Invalid IPPP Queue State");
      }
   }

   protected final void registerWithTransport(String transportName) {
      if (transportName == null) {
         throw new Object();
      }

      StringBuffer temp = (StringBuffer)(new Object());
      temp.append("net.rim.device.cldc.io.").append(transportName).append(".Transport");
      this._transport = (Transport)TransportRegistry.get(temp.toString());
   }

   protected final void deregisterForNotifications() {
      this._transport.deregisterNotifierConnection((short)this._url.getPort());
   }

   protected final void registerForNotifications() {
      if (!this._transport.registerNotifierConnection((short)this._url.getPort(), this)) {
         StringBuffer exceptionMsg = (StringBuffer)(new Object());
         exceptionMsg.append("Port [").append(this._url.getPort()).append("] is unavailable");
         throw new Object(exceptionMsg.toString());
      }
   }

   @Override
   public final void dataReceived(SocketDatagram datagram) {
   }

   @Override
   public final synchronized void connectRequestReceived(SocketDatagram datagram) {
      this.notify();
   }

   @Override
   public final void disconnectOrderReceived(SocketDatagram datagram) {
   }

   @Override
   public final void errorReceived(SocketDatagram datagram) {
   }

   @Override
   public final String getLocalAddress() {
      return (String)(new Object(RadioInfo.getIPAddress(0)));
   }

   @Override
   public final int getLocalPort() {
      return this._url.getPort();
   }

   public StreamProtocolNotifier(String name, boolean timeouts) {
      this._url = (URL)(new Object("serverSocket", name));
      this._name = name;
      this._timeouts = timeouts;
      this.registerWithTransport("ippp");
      this.registerForNotifications();
   }
}
