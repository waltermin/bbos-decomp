package net.rim.device.cldc.io.tcp;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.streamdatagram.StreamDatagramServerSocketConnectionBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramTransportBase;
import net.rim.device.internal.io.tcp.TcpAddress;
import net.rim.device.internal.io.tcp.TcpConnectionIdentifier;
import net.rim.device.internal.io.tcp.TcpUtils;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

final class TcpServerSocketConnection extends StreamDatagramServerSocketConnectionBase {
   public TcpServerSocketConnection(StreamDatagramTransportBase transport, Tunnel tunnel) {
      super._transport = (Transport)transport;
      super._tunnel = tunnel;
      super._transport.initForServerSocketConnections();
   }

   @Override
   protected final void openPrimChores() {
      try {
         int localPort = TcpUtils.addToTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
         if (super._myAddress.getLocalPort() != localPort) {
            super._myAddress.setLocalPort(localPort);
         }
      } finally {
         EventLogger.logEvent(447071754022829032L, 1413696867, 0);
         TcpUtils.logConnectionDatabase();
         throw new Object("Max connections opened.");
      }
   }

   final Connection openPrim(String name, String apn, int mode, boolean timeouts) {
      return this.openPrim(new TcpAddress(name, apn), mode, timeouts);
   }

   @Override
   public final int getLocalPort() {
      this.throwIOExceptionIfThisConnIsClosed();
      return ((TcpAddress)super._myAddress).getLocalPort();
   }

   @Override
   public final StreamConnection acceptAndOpen() {
      this.throwIOExceptionIfThisConnIsClosed();
      synchronized (super._transport._connectionTableWaitObj) {
         while (super._transport.isConnectionTableFull()) {
            try {
               super._transport._connectionTableWaitObj.wait();
            } finally {
               continue;
            }
         }
      }

      int myPort;
      while ((myPort = ((TcpAddress)super._myAddress).getLocalPort()) != 0) {
         synchronized (super._backlogWaitObj) {
            if (this.isConnectionWaiting(myPort)) {
               if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51) && !Firewall.getInstance().allowConnection("tcp", "", false)) {
                  throw new Object("Permission denied");
               }

               Object temp;
               if ((temp = this.getNextBackloggedConnection(myPort)) != null) {
                  return (StreamConnection)temp;
               }
            } else {
               try {
                  super._backlogWaitObj.wait();
                  this.throwIOExceptionIfThisConnIsClosed();
               } finally {
                  continue;
               }
            }
         }
      }

      throw new Object();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final StreamConnection getNextBackloggedConnection(int portToLookFor) {
      this.throwIOExceptionIfThisConnIsClosed();
      Protocol tcpConnection = null;
      int sizeOfBacklog = super._tcpConnectionBacklog.size();

      int i;
      for (i = 0; i < sizeOfBacklog; i++) {
         tcpConnection = (Protocol)super._tcpConnectionBacklog.elementAt(i);
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            if (tcpConnection != null) {
               if (tcpConnection.getLocalPort() == portToLookFor) {
                  var9 = false;
                  break;
               }

               var9 = false;
            } else {
               var9 = false;
            }
         } finally {
            if (var9) {
               super._tcpConnectionBacklog.removeElementAt(i);
               sizeOfBacklog--;
               i--;
               continue;
            }
         }
      }

      if (i == sizeOfBacklog) {
         return null;
      }

      synchronized (super._backlogWaitObj) {
         super._tcpConnectionBacklog.removeElementAt(i);
         super._backlogWaitObj.notifyAll();
         return tcpConnection;
      }
   }

   final boolean isBacklogFull() {
      synchronized (super._backlogWaitObj) {
         return super._tcpConnectionBacklog.size() >= 30;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void addConnectionToBacklog(StreamConnection streamConnection) {
      this.throwIOExceptionIfThisConnIsClosed();
      synchronized (super._backlogWaitObj) {
         boolean contains = false;
         int sizeOfBacklog = super._tcpConnectionBacklog.size();

         for (int i = 0; i < sizeOfBacklog; i++) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               Object ioe = super._tcpConnectionBacklog.elementAt(i);
               if (ioe != null) {
                  if (!contains && streamConnection.equals(ioe)) {
                     contains = true;
                  }

                  ((Protocol)ioe).getLocalPort();
                  var10 = false;
               } else {
                  var10 = false;
               }
            } finally {
               if (var10) {
                  super._tcpConnectionBacklog.removeElementAt(i);
                  sizeOfBacklog--;
                  i--;
                  continue;
               }
            }
         }

         if (!contains) {
            super._tcpConnectionBacklog.addElement(streamConnection);
            super._transport.addConnection((WeakReference)(new Object(streamConnection)));
         }

         super._backlogWaitObj.notifyAll();
      }
   }

   final void addConnectionToPendingConnectionList(StreamConnection streamConnection) {
      this.throwIOExceptionIfThisConnIsClosed();
      synchronized (super._pendingConnectionWaitObj) {
         super._pendingConnectionList.addElement(streamConnection);
      }
   }

   @Override
   public final void close() {
      if (!super._isClosed) {
         super._isClosed = true;
         if (super._myAddress instanceof TcpAddress) {
            TcpUtils.removeFromTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
         }

         this.closeAssociatedConnections();
         super._tunnel.close();
         if (super._transport != null) {
            super._transport.close(this);
         }

         synchronized (super._backlogWaitObj) {
            super._backlogWaitObj.notifyAll();
         }
      }
   }
}
