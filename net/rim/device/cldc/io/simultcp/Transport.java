package net.rim.device.cldc.io.simultcp;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.simultcpdatagram.SimulTcpDatagramBase;
import net.rim.device.cldc.io.simultcpdatagram.SimulTcpDatagramProperties;
import net.rim.device.internal.io.streamdatagram.StreamDatagramAddressBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramTransportBase;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.TCPPacketHeader;
import net.rim.vm.WeakReference;

public final class Transport extends StreamDatagramTransportBase implements SimulTcpConstants {
   private int _maxTcpPayloadSize = TCPPacketHeader.getMaxPacketSize();

   @Override
   public final void init() {
      super.init(this.openNativeConnection());
      EventLogger.register(447071754022829032L, "net.rim.tcp", 2);
      EventLogger.logEvent(447071754022829032L, 1413695860, 0);
      if (4 != RadioInternal.simulTCPCommand(200, -1, 0, 0, 0)) {
         Application.getApplication().invokeLater(new Transport$1(this));
         EventLogger.logEvent(447071754022829032L, "API version mismatch in simultcp - simulator DLL".getBytes(), 0);
      }
   }

   private final DatagramConnection openNativeConnection() {
      DatagramConnection newConnection = new net.rim.device.cldc.io.simultcpdatagram.Protocol();
      byte[] tempIp = new byte[4];
      DatagramAddressBase.writeInt(tempIp, 0, -1);
      String address = StreamDatagramAddressBase.makeAddress(false, tempIp, -1, -1);
      ((ConnectionBaseInterface)newConnection).openPrim(address, 0, true);
      return newConnection;
   }

   protected final int getMaximumPayloadLength() {
      return this._maxTcpPayloadSize;
   }

   @Override
   protected final void sendDatagram(Datagram dgram) {
      this.addSendRequest(super._subConnection, dgram);
   }

   @Override
   protected final boolean isEssential(Datagram dgram) {
      return true;
   }

   @Override
   protected final boolean passToListeners(Datagram datagram) {
      SimulTcpDatagramBase sDgram = (SimulTcpDatagramBase)datagram;
      SimulTcpDatagramProperties props = sDgram.tcpProps;
      int numListeners = super._superServerConnections.size();

      for (int i = 0; i < numListeners; i++) {
         SimulTcpServerSocketConnection serverConn = (SimulTcpServerSocketConnection)((WeakReference)super._superServerConnections.elementAt(i)).get();
         if (props.controlCode == 7 && this.passDatagramToTcpServerSocketConn(serverConn, sDgram)) {
            return true;
         }
      }

      if (props.controlCode == 7) {
         throw new Object("Simultcp: passToListeners: fledge-Java mismatch");
      } else {
         return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final boolean passDatagramToTcpServerSocketConn(SimulTcpServerSocketConnection serverConn, SimulTcpDatagramBase sDgram) {
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         if (sDgram.tcpProps.destinationPort == serverConn.getLocalPort()) {
            Protocol newConn = new Protocol();
            newConn.spawnNewConnectionForServerSocket(sDgram, serverConn.getMode(), serverConn.getTimeouts());
            serverConn.addConnectionToBacklog(newConn);
            return true;
         }

         var9 = false;
      } finally {
         if (var9) {
            try {
               serverConn.close();
               return false;
            } finally {
               return false;
            }
         }
      }

      return false;
   }

   protected final boolean checkConnectionBacklog(Datagram datagram) {
      return false;
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      if (!this.passUpDatagram(datagram)) {
         this.tcpReset((SimulTcpDatagramBase)datagram);
      }
   }

   private final void tcpReset(SimulTcpDatagramBase tcpdatagram) {
   }

   public final WeakReference[] getConnections() {
      return super._superConnections;
   }

   public final DatagramConnectionBase getSubConnection() {
      return super._subConnection;
   }
}
